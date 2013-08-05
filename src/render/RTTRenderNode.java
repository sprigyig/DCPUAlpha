package render;

import java.awt.Image;
import java.awt.geom.AffineTransform;

import physics.RTTSource;

public class RTTRenderNode extends RenderNode {
	private RTTSource rttsrc;
	public RTTRenderNode(RTTSource src) {
		this(src, null);
	}
	public RTTRenderNode(RTTSource src, Image image) {
		super(image);
		this.rttsrc = src;
	}
	protected void transform(AffineTransform root) {
		root.rotate(rttsrc.position_theta());
		root.translate(rttsrc.position_radius(), 0);
		root.rotate(rttsrc.alignment_theta());
	}
}
