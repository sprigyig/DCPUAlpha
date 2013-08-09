package shipmaker.knobs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import render.RenderPreferences;
import shipmaker.BlueprintLocation;

public class RadiusKnob extends Knob {
	private BlueprintLocation bpl;

	public RadiusKnob(BlueprintLocation bpl) {
		this.bpl = bpl;
	}

	public float offset_rotation() {
		return 0;
	}

	public static void drawRadius(Graphics2D g) {
		g.drawPolyline(new int[] { -10, 10, 7, 10, 7 }, new int[] {
				0, 0, 3, 0, -3 }, 5);
	}
	
	public void draw(Graphics2D g, RenderPreferences prefs) {
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		g.drawRoundRect(-10, -10, 20, 20, 5, 5);

		drawRadius(g);
	}

	public void tweak(float dx, float dy, float worldx, float worldy) {
		bpl.r += dx;
		bpl.r = Math.max(0, Math.min(bpl.r,1000f));
	}

	public float worldx() {
		return bpl.x + bpl.r;
	}

	public float worldy() {
		return bpl.y;
	}

	public float position_x() {
		return bpl.r;
	}

	public float position_y() {
		return -40;
	}
}
