package env;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import physics.Body;
import physics.BodyForce;
import physics.RTTSource;
import regret.GlobalHacks;
import render.BodyRenderNode;
import render.RTTRenderNode;
import render.RenderNode;

public class Asteroid implements Entity {
	Body bod;
	RenderNode roid;
	int radius;
	
	public Asteroid(float x, float y, float vx, float vy, final int radius) {
		Random r = new Random();
		bod= new Body(0, 0, 0, 0, 0);
		bod.mass= 100;
		bod.ri = 100;
		bod.rotnrg = -0.05f + r.nextFloat()*.1f;
		bod.x = x;
		bod.y = y;
		bod.xnrg = bod.mass * vx * vx;
		if (vx<0) bod.xnrg *=-1;
		bod.ynrg = bod.mass * vy * vy;
		if (vy<0) bod.ynrg *=-1;
		this.radius = radius;
		
		roid = new BodyRenderNode(bod) {
			public void draw(Graphics2D g) {
				g.setColor(GlobalHacks.getBorderColor());
				g.setStroke(new BasicStroke(GlobalHacks.borderThickness(), BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				g.drawArc(-radius/2, -radius/2, radius, radius, 0, 360);
				g.setColor(new Color(120,60,20));
				g.fillArc(-radius/2, -radius/2, radius, radius, 0, 360);
			}
		};
		int blobs = r.nextInt(4)+7;
		for (int i=0;i<blobs;i++) {
			final int cmod1 = r.nextInt(20)-10;
			final int cmod2 = r.nextInt(40)-20;
			final int br = r.nextInt(radius-20)+20;
			final float bt = (float) (r.nextFloat()*Math.PI*2);
			final int dr = r.nextInt(radius)+radius/4;
			final int v = r.nextInt(10)-5;
			roid.addChild(new RTTRenderNode(new RTTSource() {
				
				public float position_theta() {
					return bt;
				}
				
				public float position_radius() {
					return br;
				}
				
				public float alignment_theta() {
					return 0;
				}
			}) {
				public void draw(Graphics2D g) {
					
					int d = 40+v;
					g.setColor(GlobalHacks.getBorderColor());
					g.setStroke(new BasicStroke(GlobalHacks.borderThickness()*2, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND));
					g.drawArc(-dr, -dr, dr*2, dr*2, 0, 180-d);
					g.drawArc(-dr, -dr, dr*2, dr*2, 0, -180+d);
					g.setColor(new Color(120+cmod1,60+cmod2,20-cmod1));
					g.fillArc(-dr, -dr, dr*2, dr*2, 0, 360);
				}
			});
		}
	}
	
	public void tickInternals(int msPerTick) {
		
	}

	public void tickPhysics(int msPerTick) {
		bod.apply(new ArrayList<BodyForce>(), msPerTick);
	}

	public RenderNode getVisuals() {
		return roid;
	}

}
