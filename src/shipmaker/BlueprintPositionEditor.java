package shipmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import physics.XYTSource;
import render.MouseEventType;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import env.Entity;

public class BlueprintPositionEditor implements Entity {
	private static final BasicStroke normal = new BasicStroke(1,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private static final BasicStroke dashed = new BasicStroke(1f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f,
			new float[] { 2f }, 0f);

	private static abstract class Tweaker extends XYTRenderNode implements
			XYTSource {
		float x, y;

		public Tweaker() {
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
						(float) (pt.getY() - last.getY()), (float)pt.getX(), (float)pt.getY());
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
	}

	private static class BPLBaseNode extends RenderNode {
		private BlueprintLocation bpl;

		BPLBaseNode(BlueprintLocation pbpl) {
			this.bpl = pbpl;

			this.addChild(new Tweaker() {

				public float offset_rotation() {
					return 0;
				}

				public void draw(Graphics2D g, RenderPreferences prefs) {
					g.setColor(Color.white);
					g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_BEVEL));
					g.drawRoundRect(-10, -10, 20, 20, 5, 5);

					g.drawPolyline(
							new int[] { -7, -10, -7, -10, 10, 7, 10, 7 },
							new int[] { -3, 0, 3, 0, 0, 3, 0, -3 }, 8);

					g.drawPolyline(new int[] { -3, 0, 3, 0, 0, 3, 0, -3 },
							new int[] { -7, -10, -7, -10, 10, 7, 10, 7 }, 8);
				}

				public void tweak(float dx, float dy, float worldx, float worldy) {
					bpl.x += dx;
					bpl.y += dy;
				}

				public float worldx() {
					return bpl.x;
				}

				public float worldy() {
					return bpl.y;
				}

				public float position_x() {
					return bpl.x;
				}

				public float position_y() {
					return bpl.y - 20;
				}
			});

			this.addChild(new Tweaker() {
				int posconst = 15;
				public float offset_rotation() {
					return 0;
				}

				public void draw(Graphics2D g, RenderPreferences prefs) {
					g.setColor(Color.white);
					g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_BEVEL));
					int lx = (int) (-10-bpl.t1 * posconst);
					g.drawLine(-10, 0, lx, 0);
					g.drawLine(lx, -5, lx, 5);
					
					int rx = (int)((Math.PI*2-bpl.t1) * posconst+10);
					
					g.drawLine(10, 0, rx, 0);
					g.drawLine(rx, -5,rx, 5);
					
					g.drawRoundRect(-10, -10, 20, 20, 5, 5);

					g.drawArc(-25, -25, 30, 30, 0, -45);
					g.drawLine(-9, -9, 0, 0);
					
				}

				public void tweak(float dx, float dy, float worldx, float worldy) {
					bpl.t1 += dx/posconst;
					bpl.t1 = (float) Math.max(Math.min(bpl.t1, Math.PI*2), 0);
				}

				public float worldx() {
					return bpl.x + bpl.t1 * posconst;
				}

				public float worldy() {
					return bpl.y;
				}

				public float position_x() {
					return bpl.x + bpl.t1 * posconst;
				}

				public float position_y() {
					return bpl.y - 60;
				}
			});
			this.addChild(new Tweaker() {
				int posconst = 15;
				public float offset_rotation() {
					return 0;
				}

				public void draw(Graphics2D g, RenderPreferences prefs) {
					g.setColor(Color.white);
					g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_BEVEL));
					
					int lx = (int)(-10-bpl.t2 * posconst);
					g.drawLine(lx, 0,-10, 0);
					g.drawLine(lx, -5, lx, 5);
					
					int rx = (int)((Math.PI*2-bpl.t2) * posconst+10);
					g.drawLine(10, 0, rx, 0);
					g.drawLine(rx, -5, rx, 5);
					
					g.drawRoundRect(-10, -10, 20, 20, 5, 5);

					
					g.drawLine(-5, 5, 5, -5);
					g.drawArc(-7, -7, 14, 14, 0, 45);
					g.drawArc(-7, -7, 14, 14, 180, 45);
					
				}

				public void tweak(float dx, float dy, float worldx, float worldy) {
					bpl.t2 += dx/posconst;
					bpl.t2 = (float) Math.max(Math.min(bpl.t2, Math.PI*2), 0);
				}

				public float worldx() {
					return bpl.x + bpl.t2 * posconst;
				}

				public float worldy() {
					return bpl.y;
				}

				public float position_x() {
					return bpl.x + bpl.t2 * posconst;
				}

				public float position_y() {
					return bpl.y - 80;
				}
			});
			
			this.addChild(new BPLAngleNode(bpl));
		}

		protected void transform(AffineTransform root) {
		}

		public void draw(Graphics2D g, RenderPreferences prefs) {
			g.setStroke(dashed);
			g.setColor(Color.white);
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
	}

	private static class BPLAngleNode extends XYTRenderNode implements
			XYTSource {
		private BlueprintLocation bpl;

		public BPLAngleNode(BlueprintLocation pbpl) {
			super(null);
			this.src = this;
			this.bpl = pbpl;
			this.addChild(new Tweaker() {

				public float offset_rotation() {
					return 0;
				}

				public void draw(Graphics2D g, RenderPreferences prefs) {
					g.setColor(Color.white);
					g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_BEVEL));
					g.drawRoundRect(-10, -10, 20, 20, 5, 5);

					g.drawPolyline(new int[] { -10, 10, 7, 10, 7 }, new int[] {
							0, 0, 3, 0, -3 }, 5);
				}

				public void tweak(float dx, float dy, float worldx, float worldy) {
					bpl.r += dx;
					bpl.r = Math.max(0, bpl.r);
				}

				public float worldx() {
					return bpl.x + bpl.r;
				}

				public float worldy() {
					return bpl.y;
				}

				public float position_x() {
					return bpl.r;
				}

				public float position_y() {
					return -40;
				}
			});
			
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
			g.drawLine((int) bpl.r, 0, (int) bpl.r + 20, 0);
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
	}

	BlueprintLocation bpl;
	BPLBaseNode base;

	public BlueprintPositionEditor() {
		bpl = new BlueprintLocation();
		base = new BPLBaseNode(bpl);
	}

	public void tickInternals(int msPerTick) {

	}

	public void tickPhysics(int msPerTick) {

	}

	public RenderNode getVisuals() {
		return base;
	}

}
