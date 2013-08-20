package physics;

import java.util.List;

public class Body {
	public float x, y, rot, xnrg, ynrg, rotnrg;
	public float mass, ri;
	
	private float xs, ys, rots;//last speeds
	public void apply(List<BodyForce>forces, int msPerTick) {
		float fx = 0, fy=0, torque=0;
		for (BodyForce f : forces) {
			fx += f.forceX(rot)*msPerTick;
			fy += f.forceY(rot)*msPerTick;
			torque += f.torque()*msPerTick;
		}
		
		xnrg += (fx/mass);
		ynrg += (fy/mass);
		rotnrg += (torque/ri);
		
		xs = xspeed_calc();
		ys = yspeed_calc();
		rots = rotspeed_calc();
		
		x+=xs;
		y+=ys;
		rot+=rots;
	}
	
	public float xspeed() {
		return xs;
	}
	
	public float yspeed() {
		return ys;
	}
	
	public float rotspeed() {
		return rots;
	}
	
	public float xspeed_calc() {
		return (float) (xnrg >= 0 ? Math.sqrt(2*xnrg / mass): -Math.sqrt(2*(-xnrg) / mass));
	}
	
	public float yspeed_calc() {
		return (float)(ynrg >= 0 ? Math.sqrt(2*ynrg / mass): -Math.sqrt(2*(-ynrg) / mass));
	}
	
	public float rotspeed_calc() {
		return (float)(rotnrg >= 0 ? Math.sqrt(2*rotnrg / ri) : -Math.sqrt(2 *(-rotnrg) / ri));
	}
	
	public Body(float x, float y, float rot, float mass, float ri) {
		super();
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.mass = mass;
		this.ri = ri;
	}
	
	public Body predictNextState() {
		return new Body(x + xs, y + ys, rot + rots, mass, ri);
	}
	
	public float headingTo(Body b) {
		return (float) Math.atan2(b.x-x, b.y-y) - rot;
	}
	
	public void reset() {
		rot = x = y = xnrg = ynrg = rotnrg = 0f;
	}

}
