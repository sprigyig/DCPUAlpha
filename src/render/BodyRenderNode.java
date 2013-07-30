package render;

import java.awt.geom.AffineTransform;

import physics.Body;

public class BodyRenderNode extends RenderNode {
	private Body b;

	public BodyRenderNode(Body b) {
		this.b = b;
	}
	
	protected void transform(AffineTransform root) {
		root.translate(b.x, b.y);
		root.rotate(b.rot);
	}
	
}
