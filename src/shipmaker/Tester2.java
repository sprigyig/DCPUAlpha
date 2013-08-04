package shipmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

import physics.XYTSource;
import render.RenderNode;
import render.RenderPreferences;
import render.SpaceViewPanel;
import render.XYTRenderNode;
import env.Entity;
import env.Space;

public class Tester2 {
	private static final BasicStroke normal = new BasicStroke(1,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private static final BasicStroke dashed = new BasicStroke(1f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f,
			new float[] { 2f }, 0f);

	private static class BlueprintLocation {
		public float x, y, t1, t2, r;
	}

	private static class BPLBaseNode extends RenderNode {
		private BlueprintLocation bpl;

		BPLBaseNode(BlueprintLocation bpl) {
			this.bpl = bpl;
			this.addChild(new BPLAngleNode(bpl));
		}

		protected void transform(AffineTransform root) {
		}

		public void draw(Graphics2D g, RenderPreferences prefs) {
			g.setStroke(dashed);
			g.setColor(Color.white);
			g.drawLine(0, 0, (int) bpl.x, 0);
			g.drawLine((int) bpl.x, 0, (int) bpl.x, (int) bpl.y);
			if (bpl.r >= 20) { 
				g.setStroke(normal);
				g.drawLine((int) bpl.x, (int) bpl.y, (int) bpl.x+20, (int) bpl.y);
			}
		}
	}

	private static class BPLAngleNode extends XYTRenderNode implements
			XYTSource {
		private BlueprintLocation bpl;

		public BPLAngleNode(BlueprintLocation bpl) {
			super(null);
			this.src = this;
			this.bpl = bpl;
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
			g.setColor(Color.white);
			g.setStroke(dashed);
			g.drawLine(0, 0, (int) bpl.r, 0);

			if (bpl.r >= 20) {
				g.setStroke(normal);
				int rad = 20;
				g.drawArc(-rad, -rad, 2 * rad, 2 * rad, 0,
						(int) (180 * bpl.t1 / Math.PI));
			}
			g.setStroke(normal);
			g.drawLine((int)bpl.r, 0, (int)bpl.r+20, 0);
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
			g.setColor(Color.white);
			g.drawArc(-20, -20, 40, 40, 0, (int)(bpl.t2 * 180/Math.PI));
			
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(prefs.borderThickness(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
			g.drawLine(1, 0, 40, 0);
			g.drawPolyline(new int[]{10, 0, 10}, new int[]{-10, 0, 10}, 3);
			g.setColor(Color.white);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
			g.drawLine(1, 0, 40, 0);
			g.drawPolyline(new int[]{10, 0, 10}, new int[]{-10, 0, 10}, 3);
		}
	}

	public static void main(String[] args) {
		Space s = new Space();
		final BlueprintLocation bpl = new BlueprintLocation();
		s.addEntity(new Entity() {
			public void tickInternals(int msPerTick) {
				bpl.x = 40;
				bpl.y = -20;
				bpl.t1 = (float) (1 * (Math.PI/8));
				bpl.t2 = (float) (3 * Math.PI/8);
				bpl.r = 100;
			}

			public void tickPhysics(int msPerTick) {
			}

			public RenderNode getVisuals() {
				return new BPLBaseNode(bpl);
			}
		});
		JPanel svp = new SpaceViewPanel(s);
		JFrame jf = new JFrame();
		jf.add(svp);
		jf.setSize(400, 400);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.start();
	}
}
