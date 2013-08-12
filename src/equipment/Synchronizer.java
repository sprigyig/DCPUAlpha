package equipment;

import dcpu.CpuWatcher;
import dcpu.Dcpu;
import dcpu.Hardware;
import ships.Equipment;
import ships.Ship;
import static dcpu.DcpuConstants.*;

public class Synchronizer implements Equipment, Hardware, CpuWatcher {

	int count;
	
	char syncTriggerNum;
	int syncTriggerTime;
	
	char rolloverIrq;

	private Ship ship;
	private char alarmIrq;
	private char hwid;
	
	public static final char READ_TIMER   = 0;
	public static final char SET_ALARM    = 1;
	public static final char SET_ROLL_IRQ = 2;
	public static final char SET_FIRE_IRQ = 3;
	public static final char TRIG_SYNC    = 4;
	
	public Synchronizer(char hwid) {
		this.hwid = hwid;
	}
	
	public void addedTo(Ship s) {
		s.addPluginHardware(hwid, this);
		this.ship = s;
		syncTriggerTime = 100001;
		syncTriggerNum = 'x';
	}

	public void reset() {
		count = 0;
	}
	public void physicsTickPreForce() {
	}

	public void physicsTickPostForce() {
	}

	public void triggerSynchronizedEvent(char id, int cyclesAgo) {
		
	}

	public void plugged_in(Dcpu parent, char id) {
		parent.addWatcher(this);
	}

	public void query(Dcpu parent) {
		
	}

	public void interrupted(Dcpu parent) {
		switch(parent.regs.gp[REG_A]) {
		case READ_TIMER:
			parent.regs.gp[REG_J] = (char)count;
			parent.regs.gp[REG_I] = (char)(count>>16);
			parent.cyclecnt+=2;
			break;
		case SET_ALARM:
			syncTriggerTime = ((int)parent.regs.gp[REG_X])<<16 | parent.regs.gp[REG_Y];
			syncTriggerNum = parent.regs.gp[REG_Z];
			parent.cyclecnt+=3;
			break;
		case SET_ROLL_IRQ:
			rolloverIrq = parent.regs.gp[REG_B];
			parent.cyclecnt+=1;
			break;
		case SET_FIRE_IRQ:
			alarmIrq = parent.regs.gp[REG_B];
			parent.cyclecnt+=1;
			break;
		case TRIG_SYNC:
			parent.cyclecnt+=1;
			ship.triggerSynchronizedEvent(parent.regs.gp[REG_Z], 0);
			break;
		}
	}

	public void cpu_changed(Dcpu cpu, long cycles, boolean idle) {
		int oldcount = count;
		count += cycles;
		
		if (count - syncTriggerTime >= 0 && count - syncTriggerTime < cycles) {
			if (syncTriggerNum!=0) {
				ship.triggerSynchronizedEvent(syncTriggerNum, count-syncTriggerTime);
			}
			if (alarmIrq!=0) {
				cpu.interrupts.add(alarmIrq);
				cpu.debug = 10;
			}
		}
		
		if (count < oldcount && rolloverIrq != 0) {
			cpu.interrupts.add(rolloverIrq);
		}
	}
}
