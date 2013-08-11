package shipmaker.knobs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import render.RenderPreferences;
import shipmaker.partplacer.BlueprintPositionEditor;

public class RadiusKnob extends Knob {
	private BlueprintPositionEditor bpl;

	public RadiusKnob(BlueprintPositionEditor blueprintPositionEditor) {
		this.bpl = blueprintPositionEditor;
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
		bpl.bpl().r += dx;
		bpl.bpl().r = Math.max(0, Math.min(bpl.bpl().r,1000f));
	}

	public float worldx() {
		return bpl.bpl().x + bpl.bpl().r;
	}

	public float worldy() {
		return bpl.bpl().y;
	}

	public float position_x() {
		return bpl.bpl().r;
	}

	public float position_y() {
		return -40;
	}
}
