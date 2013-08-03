package dcpu;

import static dcpu.DcpuConstants.*;
import java.util.*;

public class Dcpu {
	public long debug = 0;
	public Registers regs;
	public IOperand[] operands;
	public IOperation[] operations;
	public IAdvOperation[] advops;
	public DcpuMemory memory;
	public long cyclecnt;
	public boolean ifFailed;
	public Queue<Character> interrupts;
	public boolean queue_interrupts;
	public Queue<Runnable> cpuTodo;
	public Set<CpuWatcher> watchers;
	public SortedMap<Character, Hardware> hardware;
	public boolean wfi;
	private long cyclecntLim;
	
	
	long tally;
	public Dcpu() {
		regs = new Registers();
		operands = new IOperand[0x40];
		operations = new IOperation[0x20];
		advops = new IAdvOperation[0x20];
		memory = new DcpuMemory();
		interrupts = new LinkedList<>();
		cpuTodo = new LinkedList<>();
		queue_interrupts = true;
		watchers = new HashSet<>();
		hardware = new TreeMap<>();
		wfi = false;

		fill_operand_extractors();
		fill_operation_executors();
		fill_adv_operation_executors();
	}

	public void runInCpuThread(Runnable r) {
		synchronized(cpuTodo) {
			cpuTodo.add(r);
		}
	}
	
	//run for at least cycles cp cycles, leaving the extra
	//to shorten the next run
	public void step_cycles(long cycles) {
		cyclecntLim += cycles;
		synchronized(cpuTodo) {
			while(!cpuTodo.isEmpty()) {
				cpuTodo.poll().run();
			}
		}
		while (cyclecntLim - cyclecnt > cycles) {
			step();
		}
	}
	
	public void step() {
		long cyclecntStart = cyclecnt;
		if (ifFailed) {
			Instruction inst = new Instruction(memory.get(regs.pc++));
			long cyclecnt_freeze = cyclecnt;
			if (inst.opcode()!=ADV) {
				operands[inst.b()].fetch(inst.b(), false);
				operands[inst.a()].fetch(inst.a(), true);
				cyclecnt= cyclecnt_freeze+1;
			} else {
				operands[inst.a()].fetch(inst.a(), true);
				cyclecnt= cyclecnt_freeze+1;
			}
			if(!IS_IF(inst.opcode())) {
				ifFailed = false;
			}
		} else if (!queue_interrupts && !interrupts.isEmpty()) {
			wfi = false;
			if (regs.ia != 0) {
				queue_interrupts = true;
				memory.set(--regs.sp, regs.pc);
				memory.set(--regs.sp, regs.gp[REG_A]);
				regs.pc = regs.ia;
				regs.gp[REG_A] = interrupts.poll();
			} else {
				interrupts.poll();
			}
		} else {
			if (debug > 0) {
				debug -=1;
				System.out.printf("%04x(%04x)\n", (int)regs.pc, (int)memory.get(regs.pc));
			}
			
			if (wfi) {
				cyclecnt+=1;
				for (CpuWatcher watcher : watchers) {
					watcher.cpu_changed(this, 1, true);
				}
				return;
			}
			
			Instruction inst = new Instruction(memory.get(regs.pc++));
			if (inst.opcode() == ADV) {
				char a_pc = regs.pc;
				int afrag = inst.a();
				IOperand target = operands[afrag];
				char a = target.fetch(afrag, false);
				
				IAdvOperation op = advops[inst.b()];
				if (op != null) {
					op.compute(a, target, afrag, a_pc);
				} else {
					cyclecnt+=16;
				}
				
			} else {
				char b_pc = regs.pc;
				int bfrag = inst.b();
				IOperand target = operands[bfrag];
				char b = target.fetch(bfrag, false);
				int afrag = inst.a();
				char a = operands[afrag].fetch(afrag, true);
				
				IOperation op = operations[inst.opcode()];
				
				if (op != null) {
					op.compute(a, b, target, bfrag, b_pc);
				} else {
					cyclecnt+=16;
				}
			}
		}
		
		for (CpuWatcher watcher : watchers) {
			watcher.cpu_changed(this, cyclecnt - cyclecntStart, false);
		}
	}
	
	private void fill_adv_operation_executors() {
		advops[JSR] = new IAdvOperation() {
			public void compute(char a, IOperand target,
					int a_instruction_fragment, char a_fetch_pc) {
				memory.set(--regs.sp, regs.pc);
				regs.pc = a;
				cyclecnt+=3;
			}
		};
		
		advops[WFI] = new IAdvOperation() {
			public void compute(char a, IOperand target, int a_instruction_fragment,
					char a_fetch_pc) {
				cyclecnt+=1;
				wfi = true;
			}
		};
		
		advops[INT] = new IAdvOperation() {
			public void compute(char a, IOperand target, int a_instruction_fragment,
					char a_fetch_pc) {
				cyclecnt+=4;
				interrupts.add(a);
			}
		};
		
		advops[IAG] = new IAdvOperation() {
			public void compute(char a, IOperand target,
					int a_instruction_fragment, char a_fetch_pc) {
				cyclecnt+=1;
				target.set(a_instruction_fragment, regs.ia, a_fetch_pc);
			}
		};
		
		advops[IAS] = new IAdvOperation() {
			public void compute(char a, IOperand target, int a_instruction_fragment,
					char a_fetch_pc) {
				cyclecnt+=1;
				regs.ia = a;
			}
		};
		
		advops[RFI] = new IAdvOperation() {
			
			public void compute(char a, IOperand target, int a_instruction_fragment,
					char a_fetch_pc) {
				regs.gp[REG_A] = memory.get(regs.sp++);
				regs.pc = memory.get(regs.sp++);
				queue_interrupts = false;
				cyclecnt+=3;
			}
		};
		
		advops[IAQ] = new IAdvOperation() {
			public void compute(char a, IOperand target, int a_instruction_fragment,
					char a_fetch_pc) {
				queue_interrupts = a!=0;
				cyclecnt+=2;
			}
		};
		
		advops[HWN] = new IAdvOperation() {
			public void compute(char a, IOperand target,
					int a_instruction_fragment, char a_fetch_pc) {
				char num = next_hardware_id();
				target.set(a_instruction_fragment, num, a_fetch_pc);
				cyclecnt+=2;
			}
		};
		
		advops[HWQ] = new IAdvOperation() {
			public void compute(char a, IOperand target,
					int a_instruction_fragment, char a_fetch_pc) {
				if (hardware.containsKey(a)) {
					hardware.get(a).query(Dcpu.this);
				}
				cyclecnt+=4;
			}
		};
		
		advops[HWI] = new IAdvOperation() {
			public void compute(char a, IOperand target, int a_instruction_fragment,
					char a_fetch_pc) {
				if (hardware.containsKey(a)) {
					hardware.get(a).interrupted(Dcpu.this);
				}
				cyclecnt+=4;
			}
		};
		
	}

	private void fill_operation_executors() {
		operations[SET] = new ExFixedOperation(1) {
			public int compute(char a, char b) {
				return a;
			}
		};

		operations[ADD] = new IntOperation(2) {
			public int compute(char a, char b) {
				return a + b;
			}
		};

		operations[SUB] = new IntOperation(2) {
			public int compute(char a, char b) {
				return ((int) b) - ((int) a);
			}
		};

		operations[MUL] = new IntOperation(2) {
			public int compute(char a, char b) {
				return a * b;
			}
		};

		operations[MLI] = new IntOperation(2) {
			public int compute(char a, char b) {
				return sign_extend(a) * sign_extend(b);
			}
		};

		operations[DIV] = new IOperation() {
			public void compute(char a, char b, IOperand target,
					int a_instruction_fragment, char a_fetch_pc) {
				cyclecnt+=3;
				if (a==0) {
					target.set(a_instruction_fragment, (char)(0), a_fetch_pc);
					regs.ex = (char)0;
				} else {
					int result = (b<<16)/a;
					regs.ex = (char)result;
					result = b/a;
					target.set(a_instruction_fragment, (char)(result), a_fetch_pc);
				}
			}
		};

		operations[DVI] = new IOperation() {
			public void compute(char a, char b, IOperand target,
					int a_instruction_fragment, char a_fetch_pc) {
				int ai = sign_extend(a);
				int bi = sign_extend(b);

				cyclecnt+=3;
				if (ai == 0) {
					target.set(a_instruction_fragment, (char)(0), a_fetch_pc);
					regs.ex = (char)0;
				} else {
					int result = (bi<<16)/ai;
					regs.ex = (char)result;
					result = bi/ai;
					target.set(a_instruction_fragment, (char)(result), a_fetch_pc);
				}
			}
		};

		operations[MOD] = new ExFixedOperation(3) {
			public int compute(char a, char b) {
				if (a==0) return 0;
				return b % a;
			}
		};

		operations[MDI] = new ExFixedOperation(3) {
			public int compute(char a, char b) {
				if (a==0) return 0;
				return sign_extend(b) % sign_extend(a);
			}
		};

		operations[AND] = new ExFixedOperation(1) {
			public int compute(char a, char b) {
				return a & b;
			}
		};

		operations[BOR] = new ExFixedOperation(1) {
			public int compute(char a, char b) {
				return a | b;
			}
		};
		operations[XOR] = new ExFixedOperation(1) {
			public int compute(char a, char b) {
				return a ^ b;
			}
		};

		operations[ASR] = new IOperation() {
			public void compute(char a, char b, IOperand target,
					int a_instruction_fragment, char a_fetch_pc) {
				int result = sign_extend(b) << (16 - a);
				regs.ex = (char)result;
				result = result >> 16;
				target.set(a_instruction_fragment, (char)result, a_fetch_pc);
				cyclecnt+=1;
			}
		};
		
		operations[SHR] = new IOperation() {
			public void compute(char a, char b, IOperand target,
					int a_instruction_fragment, char a_fetch_pc) {
				int result = b << (16 - (int)a);
				regs.ex = (char)result;
				result = result >> 16;
				target.set(a_instruction_fragment, (char)result, a_fetch_pc);
				cyclecnt+=1;
			}
		};

		operations[SHL] = new IntOperation(1) {
			public int compute(char a, char b) {
				return ((int) b) << a;
			}
		};

		operations[IFB] = new ConditionalOperation(2) {
			public boolean compute(char a, char b) {
				return (b & a) != 0;
			}
		};

		operations[IFC] = new ConditionalOperation(2) {
			public boolean compute(char a, char b) {
				return (b & a) == 0;
			}
		};

		operations[IFE] = new ConditionalOperation(2) {
			public boolean compute(char a, char b) {
				return b == a;
			}
		};

		operations[IFN] = new ConditionalOperation(2) {
			public boolean compute(char a, char b) {
				return b != a;
			}
		};

		operations[IFG] = new ConditionalOperation(2) {
			public boolean compute(char a, char b) {
				return b > a;
			}
		};

		operations[IFA] = new ConditionalOperation(2) {
			public boolean compute(char a, char b) {
				return sign_extend(b) > sign_extend(a);
			}
		};

		operations[IFL] = new ConditionalOperation(2) {
			public boolean compute(char a, char b) {
				return b < a;
			}
		};

		operations[IFU] = new ConditionalOperation(2) {
			public boolean compute(char a, char b) {
				return sign_extend(b) < sign_extend(a);
			}
		};

		operations[ADX] = new IntOperation(3) {

			public int compute(char a, char b) {
				return a + b + regs.ex;
			}
		};

		operations[SBX] = new IntOperation(3) {
			public int compute(char a, char b) {
				return b - a + regs.ex;
			}
		};

		operations[STI] = new IOperation() {
			public void compute(char a, char b, IOperand target,
					int b_instruction_fragment, char b_fetch_pc) {
				target.set(b_instruction_fragment, a, b_fetch_pc);
				regs.gp[REG_I]++;
				regs.gp[REG_J]++;
				cyclecnt+=2;
			}
		};

		operations[STD] = new IOperation() {
			public void compute(char a, char b, IOperand target,
					int b_instruction_fragment, char b_fetch_pc) {
				target.set(b_instruction_fragment, a, b_fetch_pc);
				regs.gp[REG_I]--;
				regs.gp[REG_J]--;
				cyclecnt+=2;
			}
		};
	}

	private void fill_operand_extractors() {
		IOperand regop = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				regs.gp[instruction_fragment] = value;
			}

			public char fetch(int instruction_fragment, boolean isA) {
				return regs.gp[instruction_fragment];
			}
		};
		for (int i = REG_A; i < GP_REG_COUNT; i++) {
			operands[i] = regop;
		}

		IOperand indregop = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				memory.set(regs.gp[instruction_fragment-INDIRECT(REG_A)], value);
			}

			public char fetch(int instruction_fragment, boolean isA) {
				return memory.get(regs.gp[instruction_fragment-INDIRECT(REG_A)]);
			}
		};
		for (int i = INDIRECT(REG_A); i < INDIRECT(GP_REG_COUNT); i++) {
			operands[i] = indregop;
		}

		IOperand indoffop = new IOperand() {
			public char fetch(int instruction_fragment, boolean isA) {
				cyclecnt += 1;
				char offset = memory.get(regs.pc++);
				return memory
						.get((char) (offset + regs.gp[instruction_fragment-INDIRECT_OFFSET(REG_A)]));
			}

			public void set(int instruction_fragment, char value, char fetch_pc) {
				char offset = memory.get(fetch_pc);
				memory.set((char) (offset + regs.gp[instruction_fragment-INDIRECT_OFFSET(REG_A)]),
						value);
			}
		};
		for (int i = INDIRECT_OFFSET(REG_A); i < INDIRECT_OFFSET(GP_REG_COUNT); i++) {
			operands[i] = indoffop;
		}

		operands[PUSHPOP] = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				memory.set(--regs.sp, value);
			}

			public char fetch(int instruction_fragment, boolean isA) {
				if (isA) {
					return memory.get(regs.sp++);
				} else {
					return 0;
				}
			}
		};

		operands[PEEK] = new IOperand() {
			public char fetch(int instruction_fragment, boolean isA) {
				return memory.get(regs.sp);
			}

			public void set(int instruction_fragment, char value, char fetch_pc) {
				memory.set(regs.sp, value);
			}
		};

		operands[PICK] = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				char offset = memory.get(fetch_pc);
				memory.set((char) (offset + regs.sp), value);
			}

			public char fetch(int instruction_fragment, boolean isA) {
				cyclecnt += 1;
				char offset = memory.get(regs.pc++);
				return memory.get((char) (offset + regs.sp));
			}
		};

		operands[SP] = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				regs.sp = value;
			}

			public char fetch(int instruction_fragment, boolean isA) {
				return regs.sp;
			}
		};

		operands[PC] = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				regs.pc = value;
			}

			public char fetch(int instruction_fragment, boolean isA) {
				return regs.pc;
			}
		};

		operands[EX] = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				regs.ex = value;
			}

			public char fetch(int instruction_fragment, boolean isA) {
				return regs.ex;
			}
		};

		operands[LIT_IND] = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				memory.set(memory.get(fetch_pc), value);
			}

			public char fetch(int instruction_fragment, boolean isA) {
				cyclecnt += 1;
				return memory.get(memory.get(regs.pc++));
			}
		};

		operands[LONG_LIT] = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
			}

			public char fetch(int instruction_fragment, boolean isA) {
				cyclecnt += 1;
				return memory.get(regs.pc++);
			}
		};

		IOperand shortlit = new IOperand() {
			public void set(int instruction_fragment, char value, char fetch_pc) {
				//no way to hit this method.
			}

			public char fetch(int instruction_fragment, boolean isA) {
				return (char) (instruction_fragment - 0x21);
			}
		};

		for (int i = SHORT_LIT(SHORT_LIT_MIN); i < SHORT_LIT(SHORT_LIT_MAX) + 1; i++) {
			operands[i] = shortlit;
		}
	}

	public class Registers {
		public char[] gp = new char[GP_REG_COUNT];
		public char pc, sp, ex, ia;
	}

	public interface IOperand {
		char fetch(int instruction_fragment, boolean isA);

		void set(int instruction_fragment, char value, char fetch_pc);
	}

	public interface IOperation {
		void compute(char a, char b, IOperand target,
				int a_instruction_fragment, char a_fetch_pc);
	}

	public abstract class IntOperation implements IOperation {
		private int cnt;

		public IntOperation(int cnt) {
			this.cnt = cnt;
		}

		public void compute(char a, char b, IOperand target,
				int b_instruction_fragment, char b_fetch_pc) {
			int result = compute(a, b);
			regs.ex = (char) (result >> 16);
			target.set(b_instruction_fragment, (char) result, b_fetch_pc);
			cyclecnt += cnt;
		}

		public abstract int compute(char a, char b);
	}

	public abstract class ExFixedOperation implements IOperation {
		private int cnt;

		public ExFixedOperation(int cnt) {
			this.cnt = cnt;
		}

		public void compute(char a, char b, IOperand target,
				int b_instruction_fragment, char b_fetch_pc) {
			int result = compute(a, b);
			target.set(b_instruction_fragment, (char) (result), b_fetch_pc);
			cyclecnt += cnt;
		}

		public abstract int compute(char a, char b);
	}

	public abstract class ConditionalOperation implements IOperation {
		private int cnt;

		public ConditionalOperation(int cnt) {
			this.cnt = cnt;
		}

		public void compute(char a, char b, IOperand target,
				int b_instruction_fragment, char b_fetch_pc) {
			if (!compute(a, b)) {
				ifFailed = true;
			}
			cyclecnt += cnt;
		}

		public abstract boolean compute(char a, char b);
	}

	public interface IAdvOperation {
		void compute(char a, IOperand target, int a_instruction_fragment, char a_fetch_pc);
	}

	public static class Instruction {
		private char value;

		Instruction(char value) {
			this.value = value;
		}

		int opcode() {
			return (this.value >> 0) & 0x1f;
		}

		int b() {
			return (this.value >> 5) & 0x1f;
		}

		int a() {
			return (this.value >> 10) & 0x3f;
		}
	}
	
	public void addWatcher(CpuWatcher w) {
		watchers.add(w);
	}
	
	public void removeWatcher(CpuWatcher w) {
		watchers.remove(w);
	}
	
	public char next_hardware_id() {
		if (hardware.isEmpty()) {
			return (char)1;
		} else {
			return (char)(hardware.lastKey()+1);
		}
	}
	
	public void addHardware(char id, Hardware hw) {
		hw.plugged_in(this, id);
		hardware.put(id, hw);
	}

	public void reset() {
		regs.ex = 0;
		regs.ia = 0;
		regs.pc = 0;
		regs.sp = 0;
		for (int i=0;i<regs.gp.length;i++) {
			regs.gp[i] = 0;
		}
		ifFailed = false;
		interrupts.clear();
		queue_interrupts = true;
		cpuTodo.clear();
		cyclecntLim = cyclecnt = 0;
	}
}
