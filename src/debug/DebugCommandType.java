package debug;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import dcpu.CpuWatcher;
import dcpu.Dcpu;

public enum DebugCommandType {
	PAUSE(new DebugCommandAction() {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params,
				DebugClientServer debugClientServer) {
			Dcpu c = null;
			if (params.length > 0) {
				c = CPUForId(cpus, params[0]);
			}

			DebugResponse resp = new DebugResponse();
			resp.payload = new long[] {};

			if (c != null) {
				c.pause();
				resp.userAlert = "cpu paused";
				resp.status = CommandStatus.PASS;
			} else {
				resp.userAlert = "id not found";
				resp.status = CommandStatus.FAIL;
			}

			return resp;
		}
	}), UNPAUSE(new DebugCommandAction() {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params,
				DebugClientServer debugClientServer) {
			Dcpu c = null;
			if (params.length > 0) {
				c = CPUForId(cpus, params[0]);
			}

			DebugResponse resp = new DebugResponse();
			resp.payload = new long[] {};

			if (c != null) {
				c.unpause();
				resp.userAlert = "cpu unpaused";
				resp.status = CommandStatus.PASS;
			} else {
				resp.userAlert = "id not found";
				resp.status = CommandStatus.FAIL;
			}

			return resp;
		}
	}), CPUREGS(new DebugCommandAction() {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params,
				DebugClientServer debugClientServer) {
			Dcpu c = null;
			if (params.length > 0) {
				c = CPUForId(cpus, params[0]);
			}

			DebugResponse resp = new DebugResponse();

			if (c != null) {
				resp.payload = new long[] { c.regs.gp[0], c.regs.gp[1],
						c.regs.gp[2], c.regs.gp[3], c.regs.gp[4], c.regs.gp[5],
						c.regs.gp[6], c.regs.gp[7], c.regs.ex, c.regs.ia,
						c.regs.pc, c.regs.sp };
				resp.userAlert = "cpu unpaused";
				resp.status = CommandStatus.PASS;
			} else {
				resp.payload = new long[] {};
				resp.userAlert = "id not found";
				resp.status = CommandStatus.FAIL;
			}

			return resp;
		}
	}), LIST_CPUS(new DebugCommandAction() {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params,
				DebugClientServer debugClientServer) {
			DebugResponse resp = new DebugResponse();
			resp.payload = new long[cpus.size()];
			int i = 0;
			for (Dcpu d : cpus) {
				resp.payload[i++] = d.id;
			}
			resp.userAlert = "";
			resp.status = CommandStatus.PASS;
			return resp;
		}
	}), BREAK(new DebugCommandAction() {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params,
				DebugClientServer debugClientServer) {
			if (params.length < 2) {
				return new DebugResponse(CommandStatus.FAIL,
						"not enough parameters", new long[] {});
			}
			Dcpu c = CPUForId(cpus, params[0]);
			if (c == null) {
				return new DebugResponse(CommandStatus.FAIL, "cpu not found",
						new long[] {});
			}
			return toggleBreak(c, params[1], debugClientServer);
		}

	}), STEP(new DebugCommandAction() {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params,
				final DebugClientServer debugClientServer) {
			if (params.length < 1) {
				return new DebugResponse(CommandStatus.FAIL,
						"not enough parameters", new long[] {});
			}
			Dcpu c = CPUForId(cpus, params[0]);
			if (c == null) {
				return new DebugResponse(CommandStatus.FAIL, "cpu not found",
						new long[] {});
			}
			c.pause();// should be already, but just in case, don't create
						// concurrency problems with addWatcher
			c.addWatcher(new CpuWatcher() {
				CpuWatcher that;
				{
					that = this;
				}

				public void cpu_changed(final Dcpu cpu, long cyclesAdvanved,
						boolean idle) {
					cpu.pause();
					cpu.runInCpuThread(new Runnable() {
						public void run() {
							cpu.removeWatcher(that);
							DebugResponse resp = new DebugResponse();
							resp.userAlert = "Stepped";
							resp.payload = new long[]{};
							resp.status = CommandStatus.UNSOLICITED;
							debugClientServer.sendResponse(resp);
						}

					});
					
				}
			});
			c.unpause();
			return new DebugResponse(CommandStatus.PASS, "cpu stepping",
					new long[] {});
		}

	}), LISTMEM(new DebugCommandAction() {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params,
				DebugClientServer debugClientServer) {
			DebugResponse ret = new DebugResponse();

			int start = (int) params[1];
			int len = (int) params[2];
			
			if (params.length < 3 || start < 0
					|| start+len > 0x10000 ) {
				return new DebugResponse(CommandStatus.FAIL,
						"Invalid Address Range", new long[] {});
			}

			Dcpu c = CPUForId(cpus, params[0]);
			if (c == null) {
				return new DebugResponse(CommandStatus.FAIL, "cpu not found",
						new long[] {});
			}

			ret.userAlert = "";
			ret.status = CommandStatus.PASS;
			ret.payload = new long[len];

			for (int i = 0; i < len; i++) {
				ret.payload[i] = c.memory.get((char) (i + start));
			}

			return ret;
		}

	});

	public final DebugCommandAction action;

	DebugCommandType(DebugCommandAction d) {
		this.action = d;
	}

	public static interface DebugCommandAction {
		public DebugResponse run(Collection<Dcpu> cpus, long[] params,
				DebugClientServer debugClientServer);
	}

	private static Dcpu CPUForId(Collection<Dcpu> cpus, long id) {
		for (Dcpu d : cpus) {
			if (d.id == id) {
				return d;
			}
		}
		return null;
	}

	private static DebugResponse toggleBreak(final Dcpu c, long addr,
			final DebugClientServer debugClientServer) {
		synchronized (breakpoints) {
			if (!breakpoints.containsKey(c)) {
				breakpoints.put(c, new HashSet<Character>());
				System.out.println("making new cpu bp set for cpu id:" + c.id);
			}
			final HashSet<Character> bpmap = breakpoints.get(c);
			final Character caddr = (char) addr;
			boolean add = !bpmap.contains(caddr);
			if (add) {
				bpmap.add(caddr);
				final CpuWatcher w = new CpuWatcher() {
					CpuWatcher that;
					{
						that = this;
					}

					public void cpu_changed(final Dcpu cpu,
							long cyclesAdvanved, boolean idle) {
						if (cpu.regs.pc == caddr) {
							if (bpmap.contains(caddr)) {
								cpu.pause();
								debugClientServer.sendResponse(new DebugResponse(CommandStatus.UNSOLICITED, "breakpoint reached", new long[]{}));
							} else {
								cpu.runInCpuThread(new Runnable() {
									public void run() {
										cpu.removeWatcher(that);
									}
								});
							}
						}
					}
				};
				c.runInCpuThread(new Runnable() {
					public void run() {
						c.addWatcher(w);
					}
				});

			} else {
				bpmap.remove(caddr);
			}
		}
		return new DebugResponse(CommandStatus.PASS, "breakpoint toggled",
				new long[] {});
	}

	private static HashMap<Dcpu, HashSet<Character>> breakpoints = new HashMap<>();
}
