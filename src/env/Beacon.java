package env;

import physics.Body;
import ships.Ship;

public class Beacon {
	public Body me;
	private char id;
	private int strength;

	public Beacon(Body location, char id, int bcastStrength) {
		this.me = location;
		this.id = id;
		this.strength = bcastStrength;
	}
	
	public char id() {
		return id;
	}
	
	public void id(char id) {
		this.id = id;
	}
	
	public float apparentAngle(Ship s) {
		float dx = me.x - s.me.x;
		float dy = me.y - s.me.y;
		double theta = Math.atan2(dy, dx);
		theta -= s.me.rot;
		
		return (float) theta;
	}
	
	public int apparentStrength(float x, float y, int sensorQuality) {
		float dx = me.x - x;
		float dy = me.y - y;
		
		float distance_sq = dx * dx + dy * dy;
		float pow2 = (float) Math.log10(distance_sq);
		return strength+sensorQuality-(int)Math.round(pow2/2);
	}
	
	public int strength() {
		return strength;
	}
}
