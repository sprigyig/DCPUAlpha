package render;

import java.awt.Image;
import java.awt.geom.AffineTransform;

import physics.XYTSource;

public class XYTRenderNode extends RenderNode {

	protected double x;
	protected double y;
	protected double theta;
	protected XYTSource src;
	
	public XYTRenderNode(XYTSource src) {
		this(src, null);
		this.src = src;
	}
	
	public XYTRenderNode(XYTSource src, Image i) {
		super(i);
		this.src = src;
	}
	
	public XYTRenderNode(double x, double y, double theta) {
		this(x,y,theta, null);
	}
	
	public XYTRenderNode(double x, double y, double theta, Image i) {
		super(i);
		this.x = x;
		this.y = y;
		this.theta = theta;
	}
	
	protected void transform(AffineTransform root) {
		if (src!=null) {
			
			root.translate(src.position_x(), src.position_y());
			root.rotate(src.alignment_theta());
		} else {
			root.translate(x, y);
			root.rotate(theta);
		}
		
	}
}
