package shipmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import physics.Body;
import physics.XYTSource;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.knobs.PositionKnob;
import shipmaker.knobs.RadiusKnob;
import shipmaker.knobs.T1Knob;
import shipmaker.knobs.T2Knob;
import env.Entity;

public class BlueprintPositionEditor implements Entity {
	private static final BasicStroke normal = new BasicStroke(1,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private static final BasicStroke dashed = new BasicStroke(1f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f,
			new float[] { 2f }, 0f);


	private static class BPLBaseNode extends RenderNode {
		private BlueprintLocation bpl;

		BPLBaseNode(BlueprintLocation pbpl) {
			this.bpl = pbpl;

			this.addChild(new PositionKnob(bpl));

			this.addChild(new T1Knob(bpl) );
			this.addChild(new T2Knob(bpl) );
			
			this.addChild(new BPLAngleNode(bpl));
		}

		protected void transform(AffineTransform root) {
		}

		public void draw(Graphics2D g, RenderPreferences prefs) {
			g.setStroke(dashed);
			g.setColor(prefs.borderColor().brighter());
			g.drawLine(0, 0, 0, (int) bpl.y);
			g.drawLine(0, (int) bpl.y, (int) bpl.x, (int) bpl.y);
			if (bpl.r >= 20) {
				g.setStroke(normal);
				g.drawLine((int) bpl.x, (int) bpl.y, (int) bpl.x + 20,
						(int) bpl.y);
			}

			int size = 8;
			int threshold = 20;

			int rbx = 0, rby = 0;
			if (bpl.x < -threshold) {
				rbx = -10;
			}
			if (bpl.x > threshold) {
				rbx = 10;
			}
			if (bpl.y < -threshold) {
				rby = (int) (bpl.y + size);
			}
			if (bpl.y > threshold) {
				rby = (int) (bpl.y - size);
			}

			if (rby != 0 && rbx != 0) {
				g.setStroke(new BasicStroke(1));
				g.drawLine(0, rby, rbx, rby);
				g.drawLine(rbx, rby, rbx, (int)bpl.y);
			}
		}
		
		public int layer() {
			return 2;
		}
	}

	private static class BPLAngleNode extends XYTRenderNode implements
			XYTSource {
		private BlueprintLocation bpl;

		public BPLAngleNode(BlueprintLocation pbpl) {
			super(null);
			this.src = this;
			this.bpl = pbpl;
			this.addChild(new RadiusKnob(bpl));
			
			addChild(new BPLAlignmentNode(bpl));
		}

		public float position_x() {
			return bpl.x;
		}

		public float position_y() {
			return bpl.y;
		}

		public float alignment_theta() {
			return bpl.t1;
		}

		public void draw(Graphics2D g, RenderPreferences prefs) {
			g.setColor(prefs.borderColor().brighter());
			g.setStroke(dashed);
			g.drawLine(0, 0, (int) bpl.r, 0);

			if (bpl.r >= 20) {
				g.setStroke(normal);
				int rad = 20;
				g.drawArc(-rad, -rad, 2 * rad, 2 * rad, 0,
						(int) (180 * bpl.t1 / Math.PI));
			}
			g.setStroke(normal);
			g.drawLine((int) bpl.r, 0, (int) bpl.r + 20, 0);
		}
		
		public int layer() {
			return 2;
		}
	}

	private static class BPLAlignmentNode extends XYTRenderNode implements
			XYTSource {
		private BlueprintLocation bpl;

		public BPLAlignmentNode(BlueprintLocation bpl) {
			super(null);
			this.src = this;
			this.bpl = bpl;
		}

		public float position_x() {
			return bpl.r;
		}

		public float position_y() {
			return 0;
		}

		public float alignment_theta() {
			return bpl.t2;
		}

		public void draw(Graphics2D g, RenderPreferences prefs) {
			g.setStroke(normal);
			g.setColor(prefs.borderColor().brighter());
			g.drawArc(-20, -20, 40, 40, 0, (int) (bpl.t2 * 180 / Math.PI));

			g.setColor(Color.black);
			g.setStroke(new BasicStroke(prefs.borderThickness(),
					BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
			g.drawLine(1, 0, 40, 0);
			g.drawPolyline(new int[] { 10, 0, 10 }, new int[] { -10, 0, 10 }, 3);
			g.setColor(Color.white);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_BEVEL));
			g.drawLine(1, 0, 40, 0);
			g.drawPolyline(new int[] { 10, 0, 10 }, new int[] { -10, 0, 10 }, 3);
		}
		public int layer() {
			return 2;
		}
	}

	BlueprintLocation bpl;
	BPLBaseNode base;
	private Body bod;

	public BlueprintPositionEditor(Body b) {
		bpl = new BlueprintLocation();
		base = new BPLBaseNode(bpl);
		bod = b;
	}

	public void tickInternals(int msPerTick) {
		bod.x = (float) (bpl.x + Math.cos(bpl.t1)*bpl.r);
		bod.y = (float) (bpl.y + Math.sin(bpl.t1)*bpl.r);
		bod.rot = bpl.t1 + bpl.t2;
	}

	public void tickPhysics(int msPerTick) {

	}

	public RenderNode getVisuals() {
		return base;
	}

}
