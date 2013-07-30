package demo.equipment;

import java.awt.Color;
import java.awt.Graphics2D;

import dcpu.Dcpu;
import dcpu.Hardware;

import physics.BodyForce;
import physics.ForceSource;
import render.RTTRenderNode;
import ships.Equipment;
import ships.Ship;
import static dcpu.DcpuConstants.*;

public class DemoEngine implements Equipment, ForceSource {
	private RTTRenderNode render;
	private float maxForce;
	private float pctOn;
	
	private float r, t1, t2;
	
	private float t2Adjust;
	private BodyForce force;

	public DemoEngine(float r, float t1, float t2, float pmaxForce) {
		this.render = new RTTRenderNode(this) {
			public void draw(Graphics2D g) {
				g.setColor(new Color(255, 255, 255));
				g.fillRect(-25, -15, 20, 30);
				g.setColor(new Color(110, 0, 0));
				g.fillRect(-5, -10, 5, 20);
				if (pctOn > 0.01) {
					g.setColor(new Color(180,180,255));
					g.fillPolygon(new int[]{0,0,16}, new int[]{-8,8,0}, 3);
					g.setColor(new Color(220,220,255));
					g.fillPolygon(new int[]{0,0,22}, new int[]{-4,4,0}, 3);
					
				}
			}
		};
		this.maxForce = pmaxForce;
		this.pctOn = 0f;
		
		this.r = r;
		this.t1 = t1;
		this.t2 = t2;
		
		this.force = new BodyForce(this);
	}
	
	public void addedTo(Ship s) {
		s.addRenderNode(render);
		s.addBodyForce(force);
		s.addPluginHardware(s.cpu.next_hardware_id(), new Hardware() {
			public void query(Dcpu parent) {
			}
			
			public void plugged_in(Dcpu parent, char id) {
			}
			
			public void interrupted(Dcpu parent) {
				pctOn = (float)parent.regs.gp[REG_A]/0xFFFF;
			}
		});
	}

	public float position_radius() {
		return r;
	}

	public float position_theta() {
		return t1;
	}

	public float alignment_theta() {
		return t2+t2Adjust;
	}

	public void setPctOn(float f) {
		pctOn = f;
	}
	
	public float force_magnitude() {
		return maxForce * pctOn;
	}

	public void reset() {
		this.pctOn = 0f;
	}

	public void physicsTick() {
		
	}
}
