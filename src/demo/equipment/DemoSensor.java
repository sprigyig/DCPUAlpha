package demo.equipment;

import dcpu.Dcpu;
import dcpu.Hardware;
import ships.Equipment;
import ships.Ship;
import static dcpu.DcpuConstants.*;

public class DemoSensor implements Equipment {
	private Ship ship;

	public void addedTo(Ship s) {
		this.ship = s;
		
		s.addPluginHardware(s.cpu.next_hardware_id(), new Hardware() {
			
			public void query(Dcpu parent) {
				
			}
			
			public void plugged_in(Dcpu parent, char id) {
				System.out.println("sensor id" + ((int)id));
			}
			
			public void interrupted(Dcpu parent) {
				parent.regs.gp[REG_C] = 0;
				if (Math.abs((int)ship.me.x)<0x7FFF) {
					parent.regs.gp[REG_A] = (char)((int)ship.me.x);
				} else {
					parent.regs.gp[REG_C] |= 1;
				}
				
				if (Math.abs((int)ship.me.y)<0x7FFF) {
					parent.regs.gp[REG_B] = (char)((int)ship.me.y);
				} else {
					parent.regs.gp[REG_C] |= 2;
				}
				
				int rotdeg = (((int)((ship.me.rot / Math.PI) * 180))%360);
				while (rotdeg < 0) {
					rotdeg += 360;
				}				
				parent.regs.gp[REG_X] = (char)rotdeg;
				
				
				int rotspeeddeg = (((int)((ship.me.rotspeed_calc() / Math.PI) * 180*33)));
				parent.regs.gp[REG_Y] = (char)rotspeeddeg;
				
				
				return;
			}
		});
	}

	public void reset() {
		
	}

	public void physicsTickPostForce() {
		
	}

	public void triggerSynchronizedEvent(char id, int cyclesAgo) {
	}

	public void physicsTickPreForce() {
		// TODO Auto-generated method stub
		
	}

}
