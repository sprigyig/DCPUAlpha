package equipment;

import ships.Equipment;
import ships.Ship;

public class Generator implements Equipment {

	
	private Ship ship;

	public void addedTo(Ship s) {
		ship = s;
	}

	public void reset() {
		
	}

	public void physicsTickPreForce() {
		
	}

	public void physicsTickPostForce() {
		ship.power.contribute(20);
	}

	public void triggerSynchronizedEvent(char id, int cyclesAgo) {
		
	}

}
