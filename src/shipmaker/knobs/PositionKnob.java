package shipmaker.knobs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import render.RenderPreferences;
import shipmaker.BlueprintLocation;

public class PositionKnob extends Knob {
	private BlueprintLocation bpl;

	public PositionKnob(BlueprintLocation bpl) {
		this.bpl = bpl;
	}
	
	public float offset_rotation() {
		return 0;
	}

	public static void drawLeftright(Graphics2D g) {

		g.drawPolyline(
				new int[] { -7, -10, -7, -10, 10, 7, 10, 7 },
				new int[] { -3, 0, 3, 0, 0, 3, 0, -3 }, 8);

	}
	
	public static void drawUpdown(Graphics2D g) {
		g.drawPolyline(new int[] { -3, 0, 3, 0, 0, 3, 0, -3 },
				new int[] { -7, -10, -7, -10, 10, 7, 10, 7 }, 8);
	}
	
	public void draw(Graphics2D g, RenderPreferences prefs) {
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		g.drawRoundRect(-10, -10, 20, 20, 5, 5);
		drawLeftright(g);
		drawUpdown(g);
	}

	public void tweak(float dx, float dy, float worldx, float worldy) {
		bpl.x += dx;
		bpl.y += dy;
		bpl.x = Math.min(Math.max(bpl.x, -1000f), 1000f);
		bpl.y = Math.min(Math.max(bpl.y, -1000f), 1000f);
	}

	public float worldx() {
		return bpl.x;
	}

	public float worldy() {
		return bpl.y;
	}

	public float position_x() {
		return bpl.x;
	}

	public float position_y() {
		return bpl.y - 20;
	}
}