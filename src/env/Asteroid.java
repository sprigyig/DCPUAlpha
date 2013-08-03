package env;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import physics.Body;
import physics.BodyForce;
import regret.GlobalHacks;
import render.BodyRenderNode;
import render.RenderNode;

public class Asteroid implements Entity {
	Body bod;
	RenderNode roid;
	
	
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
		
		
		final int seed = r.nextInt();
		
		roid = new BodyRenderNode(bod) {
			public void draw(Graphics2D g) {
				Random rbg = new Random(seed+3);
				Random rfg = new Random(seed+3);
				
				int rad = radius + rbg.nextInt(20);
				rfg.nextInt(20);
				
				g.setColor(GlobalHacks.getBorderColor());
				g.setStroke(new BasicStroke(GlobalHacks.borderThickness(), BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				g.fillArc(-rad, -rad, 2*rad, 2*rad, 0, 360);
				{
					int lobes = rbg.nextInt(4)+10;
					for (int i=0;i<lobes;i++) {
						double theta = rbg.nextDouble()*Math.PI*2;
						int x = (int) (Math.cos(theta)*rad);
						int y = (int) (Math.sin(theta)*rad);
						int lrad = rbg.nextInt(radius/2)+radius/2;
						g.fillArc(x-lrad, y-lrad, 2*lrad, 2*lrad, 0, 360);
					}
				}
				g.setColor(new Color(200,170, 170));
				g.fillArc(-rad+2, -rad+2, 2*rad-4, 2*rad-4, 0, 360);
				{
					int lobes = rfg.nextInt(4)+10;
					for (int i=0;i<lobes;i++) {
						double theta = rfg.nextDouble()*Math.PI*2;
						int x = (int) (Math.cos(theta)*rad);
						int y = (int) (Math.sin(theta)*rad);
						int lrad = rfg.nextInt(radius/2)+radius/2-2;
						g.fillArc(x-lrad, y-lrad, 2*lrad, 2*lrad, 0, 360);
					}
				}
				
			}
		};

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
