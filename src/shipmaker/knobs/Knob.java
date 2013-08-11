package shipmaker.knobs;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import physics.XYTSource;
import render.MouseEventType;
import render.XYTRenderNode;

public abstract class Knob extends XYTRenderNode implements XYTSource {
	float x, y;

	public Knob() {
		super(null);
		src = this;

	}

	public float position_x() {
		return x;
	}

	public float position_y() {
		return y;
	}

	public abstract float offset_rotation();

	public float alignment_theta() {
		return -offset_rotation();
	}

	Point2D last = null;
	boolean dragging = false;

	public boolean interacted(AffineTransform root, MouseEvent e,
			MouseEventType t) {
		System.out.println("k:"+t.name());
		Point2D.Float src = new Point2D.Float();
		src.setLocation(e.getX(), e.getY());
		try {
			root.invert();
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}

		Point2D pt = root.transform(src, null);

		if (t == MouseEventType.MOUSE_PRESS && Math.abs(pt.getX()) < 10
				&& Math.abs(pt.getY()) < 10) {
			System.out.println(pt.getX());
			dragging = true;
			last = pt;
			pt.setLocation(pt.getX() + worldx(), pt.getY() + worldy());
		} else if (dragging && t == MouseEventType.MOUSE_DRAG) {
			pt.setLocation(pt.getX() + worldx(), pt.getY() + worldy());
			tweak((float) (pt.getX() - last.getX()),
					(float) (pt.getY() - last.getY()), (float) pt.getX(),
					(float) pt.getY());
			last = pt;
		} else if (dragging && t == MouseEventType.MOUSE_RELEASE) {
			dragging = false;
			last = null;
		} else {
			return false;
		}
		return true;
	}

	public abstract void tweak(float dx, float dy, float worldx, float worldy);

	public abstract float worldx();

	public abstract float worldy();

	public int layer() {
		return 2;
	}
}