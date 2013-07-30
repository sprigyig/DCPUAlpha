package render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public abstract class RenderNode {
	
	private static final boolean debug = false;
	protected Image img;
	protected List<RenderNode> children;
	
	public RenderNode() {
		children = new ArrayList<RenderNode>();
	}
	
	public RenderNode(Image i) {
		this();
		img = i;
	}
	
	protected abstract void transform(AffineTransform root);
	
	protected void draw(Graphics2D graphics) {
		if (img != null) {
			graphics.drawImage(img, -img.getWidth(null)/2, -img.getHeight(null)/2, null);
		}
	}
	
	public void render(Graphics2D graphics, AffineTransform root) {
		render(graphics, root, 0);
	}
	
	private void render(Graphics2D graphics, AffineTransform root, int level) {
		transform(root);
		
		if (debug) {
			for (int i=0; i<level; i++) {
				System.out.print(" ");
			}
			System.out.println(root);
		}
		
		graphics.setTransform(root);
		draw(graphics);
		
		for (RenderNode r : children) {
			r.render(graphics, (AffineTransform)root.clone());
		}
	}
	
	public void addChild(RenderNode r) {
		children.add(r);
	}
	
	public void removeChild(RenderNode r) {
		children.remove(r);
	}
}
