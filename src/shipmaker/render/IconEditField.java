package shipmaker.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import render.MouseEventType;
import render.OverlayManager;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.partplacer.TextInputControl;

public class IconEditField extends XYTRenderNode {
	private TextInputControl control;
	private int hitboxWidth;
	private int hitboxLeft;
	private OverlayManager om;

	public IconEditField(double x, double y, double theta, OverlayManager o,
			TextInputControl pcontrol) {
		super(x, y, theta);
		this.om = o;

		this.control = pcontrol;

		om.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (control.editing()) {
					control.typed(e);
				}
			}

			public void keyReleased(KeyEvent e) {

			}

			public void keyPressed(KeyEvent e) {

			}
		});
	}

	public void draw(Graphics2D g, RenderPreferences prefs) {
		if (control.editing()) {
			g.setColor(Color.orange);
		} else {
			g.setColor(Color.white);
		}
		g.setFont(new Font("SansSerif", Font.BOLD, 14));
		if (control.drawIcon(g, prefs)) {
			hitboxLeft = -10;
			hitboxWidth = 25;
		} else {
			hitboxLeft = 0;
			hitboxWidth = 0;
		}
		g.drawString(control.content(), hitboxLeft + hitboxWidth, 5);
		hitboxWidth += g.getFontMetrics().stringWidth(control.content());
	}

	public boolean interacted(AffineTransform root, MouseEvent e,
			MouseEventType t) {
		if (t != MouseEventType.MOUSE_PRESS)
			return false;

		if (!control.editing()) {
			Point2D.Float src = new Point2D.Float();
			src.setLocation(e.getX(), e.getY());
			try {
				root.invert();
			} catch (NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}
			Point2D dest = new Point2D.Float();
			root.transform(src, dest);

			if (dest.getX() > hitboxLeft && dest.getX() < hitboxWidth
					&& Math.abs(dest.getY()) < 9) {
				control.startEdit();
				om.setFocused(control);
				return true;
			} else {
				System.out.println("nope");
			}
		} else {
			System.out.println("end edit");
			control.endEdit(false);
		}
		return false;
	}
}