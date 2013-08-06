package shipmaker.knobs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import render.RenderPreferences;
import shipmaker.BlueprintLocation;

public class T1Knob extends Knob {

	int posconst = 15;
	private BlueprintLocation bpl;

	public T1Knob(BlueprintLocation bpl) {
		this.bpl = bpl;
	}
	
	public float offset_rotation() {
		return 0;
	}

	public static void drawT1(Graphics2D g) {
		g.drawArc(-25, -25, 30, 30, 0, -45);
		g.drawLine(-9, -9, 0, 0);
	}
	
	public void draw(Graphics2D g, RenderPreferences prefs) {
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		int lx = (int) (-10 - bpl.t1 * posconst);
		g.drawLine(-10, 0, lx, 0);
		g.drawLine(lx, -5, lx, 5);

		int rx = (int) ((Math.PI * 2 - bpl.t1) * posconst + 10);

		g.drawLine(10, 0, rx, 0);
		g.drawLine(rx, -5, rx, 5);

		g.drawRoundRect(-10, -10, 20, 20, 5, 5);
		drawT1(g);

	}

	public void tweak(float dx, float dy, float worldx, float worldy) {
		bpl.t1 += dx / posconst;
		bpl.t1 = (float) Math.max(Math.min(bpl.t1, Math.PI * 2), 0);
	}

	public float worldx() {
		return bpl.x + bpl.t1 * posconst;
	}

	public float worldy() {
		return bpl.y;
	}

	public float position_x() {
		return bpl.x + bpl.t1 * posconst;
	}

	public float position_y() {
		return bpl.y - 60;
	}
}
