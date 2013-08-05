package equipment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import physics.XYTSource;
import render.RenderPreferences;
import render.XYTRenderNode;
import ships.Equipment;
import ships.Ship;

public class Capacitor implements Equipment, XYTSource {
	/*
	 * Capacitors Equipment projection is just ornamental,
	 * they should add to the capacity given to the power grid
	 * in ship creation
	 */
	private float x,y,t;

	public Capacitor(float x, float y, float t) {
		this.x = x;
		this.y = y;
		this.t = t;
	}

	public void addedTo(Ship s) {
		final Ship ship = s;
		s.addRenderNode(new XYTRenderNode(this){
			public void draw(Graphics2D g, RenderPreferences prefs) {
				int scale = 15;
				int podheight = 7;
				g.setColor(prefs.borderColor());
				
				int t = prefs.borderThickness();
				
				g.setStroke(new BasicStroke(2 * t,
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				
				g.drawRect(-scale, -scale, 2*scale, podheight);
				g.drawRect(-scale, scale-podheight, 2*scale, podheight);
				g.drawRect(-scale, -podheight/2, 2*scale, podheight);
				
				g.setColor(prefs.body2());
				
				g.fillRect(-scale, -scale, 2*scale, podheight);
				g.fillRect(-scale, scale-podheight, 2*scale, podheight);
				g.fillRect(-scale, -podheight/2, 2*scale, podheight);
				
				int color = (int) (ship.power.getPower() * 255 / ship.power.getCapacity());
				
				g.setColor(prefs.borderColor());
				
				if (ship.getCpuFreeze() > 0 && System.currentTimeMillis()%500 < 250) {
					g.setColor(new Color(200,200,200));
				}
				
				g.drawArc(-podheight/2, -podheight/2, podheight, podheight, 0, 360);
				color = Math.max(0, Math.min(color,255));
				g.setColor(new Color(255-color, color, 0));
				g.fillArc(-podheight/2, -podheight/2, podheight, podheight, 0, 360);
				
			}
		});
	}

	public void reset() {
		
	}

	public void physicsTickPreForce() {
		
	}

	public void physicsTickPostForce() {
		
	}

	public void triggerSynchronizedEvent(char id, int cyclesAgo) {
		
	}

	public float position_x() {
		return x;
	}

	public float position_y() {
		return y;
	}

	public float alignment_theta() {
		return t;
	}
	
}
