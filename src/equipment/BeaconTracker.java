package equipment;

import static dcpu.DcpuConstants.REG_A;
import static dcpu.DcpuConstants.REG_B;
import static dcpu.DcpuConstants.REG_C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import physics.Body;
import ships.Equipment;
import ships.Ship;
import dcpu.CpuWatcher;
import dcpu.Dcpu;
import dcpu.Hardware;
import env.Beacon;
import env.Ncn450;
import env.Space;

public class BeaconTracker implements Equipment {

	int strength;
	int trackingSlots;
	char trackingIds[];
	private char hwid;
	private Ship ship;

	public BeaconTracker(int strength, int trackingSlots, char hwid) {
		super();
		this.strength = strength;
		this.trackingSlots = trackingSlots;
		this.trackingIds = new char[trackingSlots];
		this.hwid = hwid;
	}

	public void addedTo(Ship s) {
		this.ship = s;
		s.addPluginHardware(hwid, new Hardware() {
			public void query(Dcpu parent) {
				parent.queryResult(0xEA9C6232, 0x32610000 | strength
						| (trackingSlots << 8), 8);
			}

			public void plugged_in(Dcpu parent, char id) {

			}

			public void interrupted(Dcpu parent) {
				if (parent.regs.gp[REG_A] == (char) 0) {
					fullScan(parent);
				} else if (parent.regs.gp[REG_A] == (char) 1) {
					lookup(parent, parent.regs.gp[REG_B], ship.me);
				} else if (parent.regs.gp[REG_A] == (char) 2) {
					startTrack(parent, parent.regs.gp[REG_B],
							parent.regs.gp[REG_C]);
				}
			}

			private void startTrack(final Dcpu parent, final char beacon,
					final char id) {
				if (id < trackingSlots) {
					parent.regs.gp[REG_A] = (char) 0;
					CpuWatcher w = new CpuWatcher() {
						int cycles = 40;

						public void cpu_changed(Dcpu cpu, long cyclesAdvanced,
								boolean idle) {
							cycles -= cyclesAdvanced;
							if (cycles <= 0) {
								final CpuWatcher that = this;
								parent.runInCpuThread(new Runnable() {
									public void run() {
										parent.removeWatcher(that);
										trackingIds[id] = beacon;
									}

								});
							}
						}
					};
					parent.addWatcher(w);
				} else {
					parent.regs.gp[REG_A] = 0x4;
				}
				parent.cyclecnt += 4;
			}

			private void lookup(Dcpu parent, char id, Body self) {
				boolean fast = false;
				for (int i = 0; i < trackingIds.length; i++) {
					if (id == trackingIds[i])
						fast = true;
				}

				parent.cyclecnt += fast ? 4 : 40;
				ship.power.sink(fast ? 0 : 15);
				Beacon lookingUp = null;
				for (Beacon b : ship.space.beacons()) {
					if (b.id() == id) {
						if (lookingUp == null) {
							lookingUp = b;
						} else {
							if (b.apparentStrength(ship.me.x, ship.me.y, strength) > 
								lookingUp.apparentStrength(ship.me.x, ship.me.y, strength)) {
								lookingUp = b;
							}
						}
					}
				}
				
				char addr = parent.regs.gp[REG_C];
				if (lookingUp == null) {
					parent.memory.set((char) (addr+5), (char)0x1);
					return;
				}
				
				int apparentStr = lookingUp.apparentStrength(ship.me.x, 
						ship.me.y, strength); 
				
				if (apparentStr==-1) {
					parent.memory.set((char) (addr+5), (char)0x2);
				} else if (apparentStr > 0) {
					parent.memory.set((char) (addr+5), (char)0);
					char strOctets = (char) (lookingUp.strength() << 8 | apparentStr); 
					parent.memory.set(addr++, (char)strOctets);
					float fheading = ship.me.headingTo(lookingUp.me);
					Ncn450 heading = new Ncn450(fheading);
					parent.memory.set(addr++, heading.upper);
					parent.memory.set(addr++, heading.lower);
					
					Body next = ship.me.predictNextState();
					Body beaconNext = lookingUp.me.predictNextState();
					
					float nfheading = next.headingTo(beaconNext);
					Ncn450 av = new Ncn450((nfheading - fheading) * 1000 / Space.MS_PER_TICK);
					parent.memory.set(addr++, av.upper);
					parent.memory.set(addr++, av.lower);
				} else {
					parent.memory.set((char) (addr+5), (char)1);
				}
				

			}

			private void fullScan(Dcpu parent) {
				ArrayList<Beacon> beacons = new ArrayList<>();
				beacons.addAll(ship.space.beacons());
				Collections.sort(beacons, new Comparator<Beacon>() {
					public int compare(Beacon b1, Beacon b2) {
						return b1.apparentStrength(ship.me.x, ship.me.y, strength)-
								b2.apparentStrength(ship.me.x, ship.me.y, strength);
					}
				});
				parent.cyclecnt+=20;
				char addr = parent.regs.gp[REG_C];
				for (int i=0; i<parent.regs.gp[REG_B] && i<beacons.size(); i++) {
					char str = (char)beacons.get(i)
							.apparentStrength(ship.me.x, ship.me.y, strength);
					if (str >= 0) {
						parent.memory.set(addr++, beacons.get(i).id());
						parent.memory.set(addr++, str);
						Ncn450 n = new Ncn450(beacons.get(i).apparentAngle(ship));
						parent.memory.set(addr++, n.upper);
						parent.memory.set(addr++, n.lower);
						parent.cyclecnt += 50;
						ship.power.sink(10);
					}
				}
			}
		});
	}

	public void reset() {
		this.trackingIds = new char[trackingSlots];
	}

	public void physicsTickPreForce() {
		ship.power.sink(trackingSlots);
	}

	public void physicsTickPostForce() {

	}

	public void triggerSynchronizedEvent(char id, int cyclesAgo) {

	}
}
