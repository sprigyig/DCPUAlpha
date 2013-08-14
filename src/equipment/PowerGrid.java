package equipment;

import dcpu.CpuWatcher;
import dcpu.Dcpu;
import dcpu.Hardware;
import ships.Equipment;
import ships.Ship;
import static dcpu.DcpuConstants.*;

public class PowerGrid implements Equipment{

	private long capacity;
	private long power;
	private Ship ship;
	
	int nonIdleCycles;
	private int cyclesPerCost;
	private int cycleCost;
	private int cycles;
	private char hwid;

	public PowerGrid(long capacity, int cyclesPerCost, int cycleCost, char hwid) {
		this.capacity = capacity;
		this.power = capacity;
		
		this.cyclesPerCost = cyclesPerCost;
		this.cycleCost = cycleCost;
		cycles = 0;
		this.hwid = hwid;
	}
	
	public void addedTo(Ship s) {
		s.power = this;
		this.ship = s;
		s.cpu.addWatcher(new CpuWatcher() {
			public void cpu_changed(Dcpu cpu, long cyclesAdvanved, boolean idle) {
				if (!idle) {
					cycles += cyclesAdvanved;
					sink( (cycles/cyclesPerCost)*cycleCost);
					cycles = cycles % cyclesPerCost;
				}
			}
		});
		s.cpu.addHardware(hwid, new Hardware() {
			public void query(Dcpu parent) {
			}
			
			public void plugged_in(Dcpu parent, char id) {
			}
			
			public void interrupted(Dcpu parent) {
				if (parent.regs.gp[REG_A]==0) {
					parent.regs.gp[REG_A]= (char)
							(getPower()*0xFFFF/getCapacity());
				}
			}
		});
	}

	public void reset() {
		
	}

	public void physicsTickPreForce() {
		
	}

	public void physicsTickPostForce() {
		
	}

	public void triggerSynchronizedEvent(char id, int cyclesAgo) {
		
	}

	public void contribute(long l) {
		power += l;
		if (power > capacity) {
			power = capacity;
		}
	}
	
	public void sink (long l) {
		power -= l;
		if (power < 0) {
			ship.brownOut();
		}
	}

	public long getPower() {
		return power;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setPower(long power) {
		this.power = power;
	}
	
	public void capacityAdded(long capacity) {
		this.capacity += capacity;
	}
	
	public void setHwid(char hwid) {
		this.hwid = hwid;
	}
}
