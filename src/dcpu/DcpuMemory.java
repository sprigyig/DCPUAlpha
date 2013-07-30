package dcpu;

import java.util.HashSet;
import java.util.Set;

public class DcpuMemory {
	public char[] physical_memory;
	public Set<MemoryPlugin> mapped;
	public Set<MemoryWatcher> watchers;
	
	public DcpuMemory() {
		physical_memory = new char[0x10000];
		mapped = new HashSet<MemoryPlugin>();
		watchers = new HashSet<MemoryWatcher>();
	}
	
	public void set(char addr, char value) {
		physical_memory[addr] = value;
		for (MemoryPlugin pl : mapped) {
			if (pl.contains(addr)) {
				pl.set(addr,value);
			}
		}
		notifyOfChange(addr, addr);
	}
	
	public char get(char addr) {
		if (mapped.isEmpty()) {
			return physical_memory[addr];
		} else {
			for (MemoryPlugin pl : mapped) {
				if (pl.contains(addr))
					return pl.get(addr);
			}
			return physical_memory[addr];
		}
	}
	
	public void notifyOfChange(char start, char end) {
		for (MemoryWatcher wa: watchers) {
			wa.memoryChanged(start, end);
		}
	}
}
