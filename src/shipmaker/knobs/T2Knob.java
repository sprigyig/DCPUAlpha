package shipmaker.knobs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import render.RenderPreferences;
import shipmaker.partplacer.BlueprintPositionEditor;

public class T2Knob extends Knob {
	int posconst = 15;
	private BlueprintPositionEditor bpl;
	public float offset_rotation() {
		return 0;
	}
	
	public T2Knob(BlueprintPositionEditor blueprintPositionEditor) {
		this.bpl = blueprintPositionEditor;
	}

	public static void drawT2(Graphics2D g) {

		g.drawLine(-5, 5, 5, -5);
		g.drawArc(-7, -7, 14, 14, 0, 45);
		g.drawArc(-7, -7, 14, 14, 180, 45);
	}
	
	public void draw(Graphics2D g, RenderPreferences prefs) {
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		
		int lx = (int)(-10-bpl.bpl().t2 * posconst);
		g.drawLine(lx, 0,-10, 0);
		g.drawLine(lx, -5, lx, 5);
		
		int rx = (int)((Math.PI*2-bpl.bpl().t2) * posconst+10);
		g.drawLine(10, 0, rx, 0);
		g.drawLine(rx, -5, rx, 5);
		
		g.drawRoundRect(-10, -10, 20, 20, 5, 5);

		drawT2(g);
		
	}

	public void tweak(float dx, float dy, float worldx, float worldy) {
		bpl.bpl().t2 += dx/posconst;
		bpl.bpl().t2 = (float) Math.max(Math.min(bpl.bpl().t2, Math.PI*2), 0);
	}

	public float worldx() {
		return bpl.bpl().x + bpl.bpl().t2 * posconst;
	}

	public float worldy() {
		return bpl.bpl().y;
	}

	public float position_x() {
		return bpl.bpl().x + bpl.bpl().t2 * posconst;
	}

	public float position_y() {
		return bpl.bpl().y - 80;
	}
}