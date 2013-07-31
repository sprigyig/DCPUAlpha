package physics;

import java.util.List;

public class Body {
	public float x, y, rot, xnrg, ynrg, rotnrg;
	public float mass, ri;
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
		
		x += xspeed();
		y += yspeed();
		rot += rotspeed();
	}
	
	public float xspeed() {
		return (float) (xnrg >= 0 ? Math.sqrt(2*xnrg / mass): -Math.sqrt(2*(-xnrg) / mass));
	}
	
	public float yspeed() {
		return (float)(ynrg >= 0 ? Math.sqrt(2*ynrg / mass): -Math.sqrt(2*(-ynrg) / mass));
	}
	
	public float rotspeed() {
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
	public void reset() {
		rot = x = y = xnrg = ynrg = rotnrg = 0f;
	}

}
