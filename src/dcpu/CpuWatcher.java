package dcpu;

public interface CpuWatcher {
	public void cpu_changed(Dcpu cpu, long cyclesAdvanved);
}
