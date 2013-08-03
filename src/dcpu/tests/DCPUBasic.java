package dcpu.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import dcpu.Dcpu;
import dcpu.MemoryWatcher;
import static dcpu.DcpuConstants.*;

public class DCPUBasic {
	
	private static void assertRegval(Dcpu d, int reg, int val) {
		assertEquals((char)val, d.regs.gp[reg]);
	}
	
	private static void assertRegvalCyclecnt(Dcpu d, int reg, int val, int cyclecnt) {
		assertRegval(d, reg, val);
		assertEquals(cyclecnt, d.cyclecnt);
	}
	
	@Test
	public void sub() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_A, SHORT_LIT(9)),
				ASSEMBLE(SET, REG_B, SHORT_LIT(10)),
				ASSEMBLE(SUB, REG_A, REG_B)
				);

		d.step();
		assertRegvalCyclecnt(d, REG_A, 9, 1);
		
		d.step();
		assertRegvalCyclecnt(d, REG_B, 10, 2);
		
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0xFFFF, 4);
		assertEquals((char)0xFFFF, d.regs.ex);
	}
	
	@Test
	public void add() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_A, SHORT_LIT(-1)),
				ASSEMBLE(SET, REG_B, SHORT_LIT(-1)),
				ASSEMBLE(ADD, REG_A, REG_B)
				);

		d.step();
		assertRegvalCyclecnt(d, REG_A, 0xFFFF, 1);
		
		d.step();
		assertRegvalCyclecnt(d, REG_B, 0xFFFF, 2);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0xFFFE, 4);
		assertEquals((char)0x1, d.regs.ex);
	}
	
	@Test
	public void mul() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_A, LONG_LIT), 0x1001,
				ASSEMBLE(SET, REG_B, LONG_LIT), 0x1000,
				ASSEMBLE(MUL, REG_A, REG_B)
				);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0x1001, 2);
		
		d.step();
		assertRegvalCyclecnt(d, REG_B, 0x1000, 4);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0x1000, 6);
		assertEquals((char)0x100, d.regs.ex);
	}
	
	@Test
	public void mli() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_A, LONG_LIT), 0xFFF0,
				ASSEMBLE(SET, REG_B, LONG_LIT), 0x10,
				ASSEMBLE(MLI, REG_A, REG_B)
				);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0xFFF0, 2);
		
		d.step();
		assertRegvalCyclecnt(d, REG_B, 0x10, 4);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0xFF00, 6);
		assertEquals((char)0xFFFF, d.regs.ex);
	}
	
	@Test
	public void div() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_A, LONG_LIT), 0xFFFF,
				ASSEMBLE(SET, REG_B, LONG_LIT), 0x10,
				ASSEMBLE(DIV, REG_A, REG_B)
				);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0xFFFF, 2);
		
		d.step();
		assertRegvalCyclecnt(d, REG_B, 0x10, 4);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0x0FFF, 7);
		assertEquals((char)0xF000, d.regs.ex);
	}
	
	@Test
	public void dvi() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_A, LONG_LIT), 0xFFFF,
				ASSEMBLE(SET, REG_B, LONG_LIT), 0x10,
				ASSEMBLE(DVI, REG_A, REG_B)
				);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0xFFFF, 2);
		
		d.step();
		assertRegvalCyclecnt(d, REG_B, 0x10, 4);
		
		d.step();
		assertRegvalCyclecnt(d, REG_A, 0x0000, 7);
		assertEquals((char)0xF000, d.regs.ex);
	}
	
	@Test
	public void subroutine() {
		Dcpu d = mkcpu(
				ASSEMBLE(ADV, JSR, SHORT_LIT(2)),
				ASSEMBLE(SUB, PC, SHORT_LIT(1)),
				ASSEMBLE(SET, REG_A, SHORT_LIT(2)),
				ASSEMBLE(SET, PC, POP)
				);

		int[] cyclecnts = new int[] {
				3, //jsr
				1, //set a, 2
				1, //set pc, pop
				2, //sub pc, 1
		};
		
		for (int i=0; i<4; i++) {
			d.cyclecnt = 0;
			d.step();
			assertEquals(d.cyclecnt, cyclecnts[i]);
		}
		
		assertRegval(d, REG_A, 2);
	}
	
	@Test
	public void stack_test() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_B, SHORT_LIT(3)),
				ASSEMBLE(SET, PUSH, REG_B),
				ASSEMBLE(SET, REG_B, SHORT_LIT(10)),
				ASSEMBLE(MOD, REG_B, POP)
				);
		
		int[] cyclecnts = new int[] {
				1, //set b, 3
				1, //set push, b
				1, //set b, 10
				3, //mod b, pop
		};
		
		for (int i=0; i<4; i++) {
			d.cyclecnt = 0;
			d.step();
			assertEquals(d.cyclecnt, cyclecnts[i]);
		}
		
		assertRegval(d, REG_B, 1);
	}
	
	private void check_conditional(int conditional, int a, int b, boolean expect_true) {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_A, LONG_LIT), a,
				ASSEMBLE(SET, REG_B, LONG_LIT), b,
				ASSEMBLE(conditional, REG_A, REG_B),
				ASSEMBLE(SET, EX, LONG_LIT), 100,
				ASSEMBLE(SET, REG_C, SHORT_LIT(1))
				);
		d.step();//set a
		d.step();//set b
		d.step();//execute conditional
		
		assertEquals(!expect_true, d.ifFailed);
		
		d.cyclecnt = 0;
		d.step();//execute or step over set ex, 1
		
		if (expect_true) {
			assertEquals(2, d.cyclecnt);
		} else {
			assertEquals(1, d.cyclecnt);
		}
		
		assertEquals(false, d.ifFailed);
		d.step();//always run set c, 1
		
		assertRegval(d, REG_C, 1);
	}
	
	@Test
	public void branches() {
		check_conditional(IFB, 0x8000, 0xFFFF, true);
		check_conditional(IFB, 0x8000, 0x7FFF, false);
		
		check_conditional(IFC, 0x8000, 0xFFFF, false);
		check_conditional(IFC, 0x8000, 0x7FFF, true);
		
		check_conditional(IFE, 0x1, 0x1, true);
		check_conditional(IFE, 0x10, 0x20, false);
		
		check_conditional(IFN, 0x1, 0x1, false);
		check_conditional(IFN, 0x10, 0x20, true);
		
		check_conditional(IFG, 0x8000, 0x8000, false);
		check_conditional(IFG, 0x8000, 0x8001, false);
		check_conditional(IFG, 0x8000, 0x1, true);
		
		check_conditional(IFA, 0x8000, 0x8000, false);
		check_conditional(IFA, 0x8001, 0x8000, true);
		check_conditional(IFA, 0x8000, 0x1, false);
		
		check_conditional(IFL, 0x8000, 0x8000, false);
		check_conditional(IFL, 0x8000, 0x8001, true);
		check_conditional(IFL, 0x8000, 0x1, false);
		
		check_conditional(IFU, 0x8000, 0x8000, false);
		check_conditional(IFU, 0x8000, 0x8001, true);
		check_conditional(IFU, 0x8000, 0x1, true);
	}
	
	@Test
	public void conditional_fallthrough() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, REG_A, SHORT_LIT(4)),
				ASSEMBLE(SET, REG_B, SHORT_LIT(3)),
				ASSEMBLE(IFE, REG_A, REG_B),
				ASSEMBLE(IFG, REG_A, SHORT_LIT(0)),
				ASSEMBLE(ADV, JSR, LONG_LIT),0x100,
				ASSEMBLE(SET, REG_C, SHORT_LIT(10))
		);
		
		d.step();//set a, 4
		d.step();//set b, 3
		
		assertEquals(2, d.cyclecnt);
		
		d.step();//IFE, a, b
		assertEquals(true, d.ifFailed);
		assertEquals(4, d.cyclecnt);
		
		d.step();//skips over the IFG, taking one cycle
		assertEquals(5, d.cyclecnt);
		
		d.step();//skips over JSR, taking one cycle
		assertEquals(6, d.cyclecnt);
		
		d.step();//set c, 10
		assertEquals(7, d.cyclecnt);
		assertEquals(10, d.regs.gp[REG_C]);
	}
	
	@Test
	public void noops() {
		Dcpu d = mkcpu(
				ASSEMBLE(ADV, ADVNOOP4, LONG_LIT), 100,
				ASSEMBLE(NOOP1, LONG_LIT, LONG_LIT), 100, 100,
				ASSEMBLE(NOOP2, REG_A, SHORT_LIT(10)),
				ASSEMBLE(NOOP1, LONG_LIT, REG_A),100,
				ASSEMBLE(SET, LONG_LIT, REG_C)
		);
		
		d.memory.watchers.add(new MemoryWatcher() {
			public void memoryChanged(char start, char end) {
				fail("Noop changed memory");
			}
		});
		
		d.step();
		d.step();
		d.step();
		d.step();
		d.step();
	}
	
	@Test
	public void operand_memory_access() {
		Dcpu d = mkcpu(
				ASSEMBLE(SET, SP, SHORT_LIT(1)),//0
				ASSEMBLE(SET, REG_A, PICK), 11,//1
				ASSEMBLE(SET, REG_B, INDIRECT(REG_A)),//3
				ASSEMBLE(SET, REG_C, INDIRECT_OFFSET(REG_A)),2,//4
				ASSEMBLE(SET, PICK, SHORT_LIT(1)), 11,//6
				ASSEMBLE(SET, INDIRECT(REG_A), SHORT_LIT(2)),//8
				ASSEMBLE(SET, INDIRECT_OFFSET(REG_A), SHORT_LIT(3)), 2,//10
				10,//11
				11,//12
				12,//13
				13 //14
		);
		
		d.step(); //set sp, 1
		
		d.cyclecnt = 0;
		d.step(); //set a, pick 12 => set a, [1+12] => set a, 11
		assertEquals(11, d.regs.gp[REG_A]);
		assertEquals(2, d.cyclecnt);
		
		d.cyclecnt = 0;
		d.step(); //set b, [a] => set b, [11] => set b, 10
		assertEquals(10, d.regs.gp[REG_B]);
		assertEquals(1, d.cyclecnt);
		
		d.cyclecnt = 0;
		d.step(); //set c, [a+2] => set c, [11+2] => set c, 12
		assertEquals(12, d.regs.gp[REG_C]);
		assertEquals(2, d.cyclecnt);
		
		d.cyclecnt = 0;
		d.step(); //set pick 11, 1
		assertEquals((char)1, d.memory.get((char)(d.regs.sp + 11)));
		assertEquals(2, d.cyclecnt);
		
		d.cyclecnt = 0;
		d.step(); //set [a], 2
		assertEquals((char)2, d.memory.get(d.regs.gp[REG_A]));
		assertEquals(1, d.cyclecnt);
		
		d.cyclecnt = 0;
		d.step(); //set [a+2], 3
		assertEquals((char)3, d.memory.get((char)(d.regs.gp[REG_A]+2)));
		assertEquals(2, d.cyclecnt);
	}
	
	@Test
	public void literal_indirect() {
		Dcpu d = mkcpu(
			ASSEMBLE(ADD, LIT_IND, LIT_IND), 1, 2	
		);
		d.step();
		assertEquals(4, d.cyclecnt);
		assertEquals((char)3, d.memory.get((char)1));
	}
	
	@Test
	public void peek() {
		Dcpu d = mkcpu(
			ASSEMBLE(SET, PUSH, SHORT_LIT(1)),
			ASSEMBLE(ADD, PEEK, PEEK)
		);
		
		d.step();
		assertEquals((char)1, d.memory.get(d.regs.sp));
		assertEquals(1, d.cyclecnt);
		
		d.cyclecnt = 0;
		d.step();
		assertEquals((char)2, d.memory.get(d.regs.sp));
		assertEquals(2, d.cyclecnt);
	}
	
	private void math_test(int op, int ex_start, int a, int b, int a_result, int ex_end, int cycles) {
		Dcpu d = mkcpu(
			ASSEMBLE(op, REG_A, REG_B)
		);
		d.regs.ex = (char)ex_start;
		d.regs.gp[REG_A] = (char)a;
		d.regs.gp[REG_B] = (char)b;
		
		d.step();
		assertEquals((char)a_result, d.regs.gp[REG_A]);
		assertEquals((char)ex_end, d.regs.ex);
		assertEquals(cycles, d.cyclecnt);
	}
	
	@Test
	public void misc_math() {
		math_test(MOD, 0, 10, 0, 0, 0, 3);
		math_test(MDI, 0, -7, 16, -7, 0, 3);
		math_test(MDI, 0, -10, 0, 0, 0, 3);
		math_test(AND, 1, 0x5, 0xc, 0x4, 1, 1);
		math_test(BOR, 1, 0x5, 0xc, 0xD, 1, 1);
		math_test(XOR, 1, 0x5, 0xc, 0x9, 1, 1);
		math_test(SHR, 0, 0x1001, 4, 0x100, 0x1000, 1);
		math_test(SHL, 0, 0x1001, 4, 0x10, 0x1, 1);
		math_test(ASR, 0, 0x8000, 1, 0xC000, 0, 1);
		math_test(ASR, 0, 0x4001, 1, 0x2000, 0x8000, 1);
		math_test(ADX, 1, 0xFFFF, 1, 1, 1, 3);
		math_test(SBX, 1, 3, 5, 0xFFFF, 0xFFFF, 3);
		math_test(SBX, 1, 0xFFFF, 0, 0, 1, 3);
	}
	
	@Test
	public void sti_std() {
		Dcpu d = mkcpu(
				ASSEMBLE(STI, INDIRECT(REG_I), INDIRECT(REG_J)),
				ASSEMBLE(STD, INDIRECT(REG_I), INDIRECT(REG_J))
		);
		
		d.regs.gp[REG_I] = 0x100;
		d.regs.gp[REG_J] = 0x200;
		d.memory.set(d.regs.gp[REG_J], 'x');
		d.memory.set((char)(d.regs.gp[REG_J]+1), 'y');
		
		d.step();
		assertEquals('x',d.memory.get((char)0x100));
		assertEquals((char)0x101, d.regs.gp[REG_I]);
		assertEquals((char)0x201, d.regs.gp[REG_J]);
		assertEquals(2, d.cyclecnt);
		
		d.step();
		assertEquals('y',d.memory.get((char)0x101));
		assertEquals((char)0x100, d.regs.gp[REG_I]);
		assertEquals((char)0x200, d.regs.gp[REG_J]);
		assertEquals(4, d.cyclecnt);
		
		
		
	}
}
