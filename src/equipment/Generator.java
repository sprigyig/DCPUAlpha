package equipment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import physics.XYTSource;
import regret.GlobalHacks;
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

	public void addedTo(Ship s) {
		ship = s;
		s.addRenderNode(new XYTRenderNode(this) {
			public void draw(Graphics2D g) {
				int scale = 15;
				g.setColor(GlobalHacks.getBorderColor());
				g.setStroke(new BasicStroke(2 * GlobalHacks.borderThickness(),
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				
				g.drawRect(-scale, -scale, 2*scale, 2*scale);
				
				g.setColor(Color.gray);
				g.fillRect(-scale, -scale, scale*2, scale*2);
				
				int color = (int) (ship.power.getPower() * 255 / ship.power.getCapacity());
				int len = (int) (ship.power.getPower() * 20 / ship.power.getCapacity());
				
				g.setColor(GlobalHacks.getBorderColor());
				g.drawRect(-10, -2, 20, 4);
				
				g.setColor(new Color(255-color, color, 0));
				
				g.fillRect(-10, -2, len, 4);
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
