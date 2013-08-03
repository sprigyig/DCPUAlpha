package equipment;

import dcpu.CpuWatcher;
import dcpu.Dcpu;
import ships.Equipment;
import ships.Ship;

public class PowerGrid implements Equipment{

	private long capacity;
	private long power;
	private Ship ship;
	
	int nonIdleCycles;
	private int cyclesPerCost;
	private int cycleCost;
	private int cycles;

	public PowerGrid(long capacity, int cyclesPerCost, int cycleCost) {
		this.capacity = capacity;
		this.power = capacity;
		
		this.cyclesPerCost = cyclesPerCost;
		this.cycleCost = cycleCost;
		cycles = 0;
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
	
}
