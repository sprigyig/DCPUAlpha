package debug;

import java.util.Collection;
import java.util.HashMap;

import dcpu.CpuWatcher;
import dcpu.Dcpu;

public enum DebugCommandType {
	PAUSE(new DebugCommandAction() {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params) {
			Dcpu c = null;
			if (params.length > 0) {
				c = CPUForId(cpus, params[0]);
			}
			
			DebugResponse resp = new DebugResponse();
			resp.payload = new long[]{};
			
			if (c != null) {
				c.pause();
				resp.userAlert = "cpu paused";
				resp.status = CommandStatus.PASS;
			} else {
				resp.userAlert = "id not found";
				resp.status = CommandStatus.FAIL;
			}
			
			return resp;
		}}),
		LIST_CPUS(new DebugCommandAction() {
			public DebugResponse run(Collection<Dcpu> cpus, long[] params) {
				DebugResponse resp = new DebugResponse();
				resp.payload = new long[cpus.size()];
				int i=0;
				for (Dcpu d : cpus) {
					resp.payload[i++] = d.id;
				}
				resp.userAlert = "";
				resp.status = CommandStatus.PASS;
				return resp;
			}}),
		BREAK(new DebugCommandAction() {
			public DebugResponse run(Collection<Dcpu> cpus, long[] params) {
				if (params.length < 2) {
					return new DebugResponse(CommandStatus.FAIL, "not enough parameters", new long[]{});
				}
				Dcpu c = CPUForId(cpus, params[0]);
				if (c==null) {
					return new DebugResponse(CommandStatus.FAIL, "cpu not found", new long[]{});
				}
				return toggleBreak(c, params[1]);
			}
			
		});
	
	
	public final DebugCommandAction action;
	DebugCommandType(DebugCommandAction d) {
		this.action = d;
	}
	public static interface DebugCommandAction {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params);
	}
	private static Dcpu CPUForId(Collection<Dcpu> cpus, long id) {
		for (Dcpu d : cpus) {
			if (d.id == id) {
				return d;
			}
		}
		return null;
	}
	private static DebugResponse toggleBreak(final Dcpu c, long addr) {
		synchronized(breakpoints) {
			if (!breakpoints.containsKey(c)) {
				breakpoints.put(c, new HashMap<Character, CpuWatcher>());
			}
			final HashMap<Character, CpuWatcher> bpmap = breakpoints.get(c);
			final Character caddr = (char)addr;
			c.runInCpuThread(new Runnable() {
				public void run() {
					if (bpmap.containsKey(caddr)) {
						c.removeWatcher(bpmap.get(caddr));
					} else {
						CpuWatcher w = new CpuWatcher() {
							public void cpu_changed(Dcpu cpu, long cyclesAdvanved, boolean idle) {
								if (cpu.regs.pc == caddr) {
									cpu.pause();
								}
							}
						};
						c.addWatcher(w);
						bpmap.put(caddr, w);
					}
				}
			});
		}
		return new DebugResponse(CommandStatus.PASS, "breakpoint toggled", new long[]{});
	}
	private static HashMap<Dcpu, HashMap<Character, CpuWatcher>> breakpoints = new HashMap<>();
}
