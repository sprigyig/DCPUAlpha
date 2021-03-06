package render;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;
import javax.swing.Timer;

import env.Space;

public class SpaceViewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Space space;
	private OverlayManager overlays;
	private Viewport vp;
	private RenderNode rootWindow;
	public RenderPreferences prefs;
	public boolean lockPosition;
	private Timer timer;
	
	public SpaceViewPanel(Space sp) {
		super(new BorderLayout());
		prefs = new StandardPrefs();
		overlays = new OverlayManager();
		
		space = sp;
		vp = new Viewport(0, 0, 0, 1d);
		ViewportMouseDrag md = new ViewportMouseDrag();
		this.addMouseMotionListener(md);
		this.addMouseListener(md);
		this.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				vp.zoom(-e.getPreciseWheelRotation(), e.getX(), -e.getY());
			}
		});
		rootWindow = new XYTRenderNode(0,0,0);
		rootWindow.addChild(vp);
		
		rootWindow.addChild(overlays);
		timer = new Timer(15, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				repaint();
			}
		});
		timer.start();
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				AffineTransform cart = new AffineTransform();
				cart.scale(1, -1);
				space.blockRunning(true);
				rootWindow.interaction(cart, e, MouseEventType.MOUSE_RELEASE);
				space.blockRunning(false);
			}
			
			public void mousePressed(MouseEvent e) {

			}
		});
		this.addKeyListener(overlays);
		this.setFocusable(true);
	}

	public void paint(Graphics g) {
		update(g);
	}
	
	public void update(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(prefs.spaceColor());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform cart = new AffineTransform();
		cart.scale(1, -1);
		overlays.setWindowDimensions(this.getSize());
		space.blockRunning(true);
		rootWindow.render(g2,cart, prefs);
		space.blockRunning(false);
		
	}

	public class Viewport extends XYTRenderNode {
		private double zoom;
		public Viewport(double x, double y, double theta, double zoom) {
			super(x, y, theta);
			this.children = space.rendities();
			this.zoom = zoom;
		}
		public void transform(AffineTransform at) {
			super.transform(at);
			at.translate(getWidth()/2, -getHeight()/2);
			at.scale(zoom, zoom);
			
		}
		public void zoom(double amt, double x, double y) {
			amt = amt/Math.abs(amt);
			double oldworldx = (x-this.x-getWidth()/2)/zoom;
			double oldworldy = (y-this.y+getHeight()/2)/zoom;
			zoom += amt * (zoom/10);
			double newworldx = (x-this.x-getWidth()/2)/zoom;
			double newworldy = (y-this.y+getHeight()/2)/zoom;
			
			
			this.x -= (oldworldx-newworldx)*zoom;
			this.y -= (oldworldy-newworldy)*zoom;
		}
		public void moveCenter(double x, double y) {
			if (!lockPosition) {
				this.x -= x;
				this.y -= y;
			}
		}
	}
	
	private class ViewportMouseDrag extends MouseAdapter {
		int lx, ly;
		int sx, sy;
		
		public ViewportMouseDrag() {
			lx = ly = -1;
		}
		
		public void mouseDragged(MouseEvent e) {
			boolean interacted = false;
			sx = sy = -1;
			
			AffineTransform cart = new AffineTransform();
			cart.scale(1, -1);
			
			if (lx==-1) {
				
				space.blockRunning(true);
				interacted = rootWindow.interaction(cart, e, MouseEventType.MOUSE_DRAG);
				space.blockRunning(false);
				
			}
			
			if (interacted) {
				return;
			}
			
			if (lx!=-1) {
				vp.moveCenter(lx-e.getX(), ly+e.getY());
				lx = e.getX();
				ly = -e.getY();
			}
		}

		public void mousePressed(MouseEvent e) {
			AffineTransform cart = new AffineTransform();
			cart.scale(1, -1);
			space.blockRunning(true);
			boolean interacted = rootWindow.interaction(cart, e, MouseEventType.MOUSE_PRESS);
			space.blockRunning(false);
			
			if (interacted) {
				sx = sy = -1;
				overlays.lowPriorityInteraction = null;
				return;
			}
			
			if (overlays.lowPriorityInteraction != null) {
				space.blockRunning(true);
				overlays.lowPriorityInteraction.run();
				overlays.lowPriorityInteraction = null; 
				sx = sy = -1;
				space.blockRunning(false);
				return;
			}
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				sx=lx = e.getX();
				sy=ly = -e.getY();
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (sx==e.getX() && sy==-e.getY()) {
				if (overlays.nonproductiveClick != null) {
					space.blockRunning(true);
					overlays.nonproductiveClick.run();
					space.blockRunning(false);
				}
			}
			sx = sy = ly = lx = -1;
		}
	}
	
	public OverlayManager overlays() {
		return overlays;
	}

	public void stop() {
		timer.stop();
	}
}
