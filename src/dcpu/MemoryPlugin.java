package dcpu;

public interface MemoryPlugin {
	public void pluggedIn(DcpuMemory parent);
	
	public boolean contains(char address);
	public char get(char address);
	public void set(char address, char value);
}
