package render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;

public abstract class RenderNode {
	protected Image img;
	protected Collection<RenderNode> children;

	public RenderNode() {
		children = new ArrayList<RenderNode>();
	}

	public RenderNode(Image i) {
		this();
		img = i;
	}

	protected abstract void transform(AffineTransform root);

	protected void draw(Graphics2D graphics, RenderPreferences prefs) {
		if (img != null) {
			graphics.drawImage(img, -img.getWidth(null) / 2,
					-img.getHeight(null) / 2, null);
		}
	}

	public void render(Graphics2D graphics, AffineTransform root, RenderPreferences prefs) {
		transform(root);
		graphics.setTransform(root);
		draw(graphics, prefs);

		for (RenderNode r : children) {
			r.render(graphics, (AffineTransform) root.clone(), prefs);
		}
	}

	public void addChild(RenderNode r) {
		children.add(r);
	}

	public void removeChild(RenderNode r) {
		children.remove(r);
	}
}
