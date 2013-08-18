package shipmaker.render;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import render.MouseEventType;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;

public class Button extends XYTRenderNode {

	private String text;
	int tw;
	int th;
	private Runnable action;

	public Button(double x, double y, String text, Runnable r) {
		super(x, y, 0);
		this.text = text;
		this.action = r;
	}
	public void draw(Graphics2D g, RenderPreferences prefs) {
		g.setFont(new Font("SansSerif", Font.BOLD, 14));
		g.setColor(prefs.overlayTextColor());
		g.drawString(text, 0, 0);
		
		tw = g.getFontMetrics().stringWidth(text);
		th = 14;
		
		g.drawRoundRect(-5, -th-5, tw+10, th+10, 5, 5);
	}
	
	public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
		if (t != MouseEventType.MOUSE_PRESS) return false;
		Point2D.Float pt = RenderNode.reverse(root, e);
		if (pt.x > -5 && pt.x < tw + 5 && pt.y > -th-5 && pt.y < 5) {
			action.run();
			return true;
		}
		return false;
	}
}
