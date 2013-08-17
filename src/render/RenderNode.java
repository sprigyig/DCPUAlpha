package render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

public abstract class RenderNode {
	protected Image img;
	protected Collection<RenderNode> children;
	protected Collection<RenderNode> bgChildren;
	
	public RenderNode() {
		children = new ArrayList<RenderNode>();
		bgChildren = new ArrayList<RenderNode>();
	}

	public RenderNode(Image i) {
		this();
		img = i;
	}

	protected abstract void transform(AffineTransform root);

	public void draw(Graphics2D graphics, RenderPreferences prefs) {
		if (img != null) {
			graphics.drawImage(img, -img.getWidth(null) / 2,
					-img.getHeight(null) / 2, null);
		}
	}

	public void render(Graphics2D graphics, AffineTransform root, RenderPreferences prefs) {
		transform(root);
		graphics.setTransform(root);
		draw(graphics, prefs);

		for (RenderNode r : bgChildren) {
			r.render(graphics, (AffineTransform) root.clone(), prefs);
		}
		
		for (RenderNode r : children) {
			r.render(graphics, (AffineTransform) root.clone(), prefs);
		}
	}
	

	public boolean interaction(AffineTransform root, MouseEvent e, MouseEventType t) {
		AffineTransform rootc = (AffineTransform) root.clone();
		transform(rootc);
		
		if (interacted(rootc, e, t)) {
			return true;
		}
		
		transform(root);
		for (RenderNode r : children) {
			if (r.interaction((AffineTransform) root.clone(), e, t)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
		return false;
	}

	public void addChild(RenderNode r) {
		children.add(r);
	}

	public void removeChild(RenderNode r) {
		children.remove(r);
	}

	public void addBgChild(RenderNode r) {
		bgChildren.add(r);
	}

	public void removeBgChild(RenderNode r) {
		bgChildren.remove(r);
	}
	
	
	public int layer() {
		return 1;
	}
	
	public static Point2D.Float reverse(AffineTransform root, MouseEvent e) {
		Point2D.Float src = new Point2D.Float();
		src.setLocation(e.getX(), e.getY());
		try {
			root.invert();
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}
		Point2D.Float dest = new Point2D.Float();
		root.transform(src, dest);
		
		return dest;
	}
}
