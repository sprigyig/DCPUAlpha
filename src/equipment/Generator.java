package equipment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import physics.Body;
import physics.XYTSource;
import render.BodyRenderNode;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import ships.Equipment;
import ships.Ship;

public class Generator implements Equipment, XYTSource {

	private Ship ship;
	private int x, y, t;

	public Generator(int x, int y, int t) {
		this.x = x;
		this.y = y;
		this.t = t;
	}
	
	public static RenderNode makeIndependantPart(Body base) {
		return new BodyRenderNode(base) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				Generator.draw(g, prefs, 255, false, 20);
			}
		};
	}
	
	public static void draw(Graphics2D g, RenderPreferences prefs, int color, boolean flash, int len) {
		int scale = 15;
		g.setColor(prefs.borderColor());
		int t = prefs.borderThickness();
		g.setStroke(new BasicStroke(2 * t,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		g.drawRect(-scale, -scale, 2*scale, 2*scale);
		
		g.setColor(prefs.body2());
		g.fillRect(-scale, -scale, scale*2, scale*2);
		
		
		
		color = Math.max(0, Math.min(color,255));
		
		
		
		g.setColor(prefs.borderColor());
		
		if (flash) {
			g.setColor(prefs.body1());
		}
		
		g.drawRect(-10, -2, 20, 4);
		
		g.setColor(new Color(255-color, color, 0));
		
		g.fillRect(-10, -2, len, 4);
	}

	
	public void addedTo(Ship s) {
		ship = s;
		s.addRenderNode(new XYTRenderNode(this) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				int color = (int) (ship.power.getPower() * 255 / ship.power.getCapacity());
				int len = (int) (ship.power.getPower() * 20 / ship.power.getCapacity());
				boolean flash = ship.getCpuFreeze() > 0 && System.currentTimeMillis()%500 < 250;
				Generator.draw(g, prefs, color, flash, len);
			}
		});
	}

	public void reset() {

	}

	public void physicsTickPreForce() {

	}

	public void physicsTickPostForce() {
		ship.power.contribute(17);
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
