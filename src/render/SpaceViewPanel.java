package render;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import env.Space;

public class SpaceViewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Space space;
	private ArrayList<RenderNode> overlays;
	private Viewport vp;
	private RenderNode rootWindow;
	private RenderPreferences prefs;
	private BufferStrategy strat;
	private Canvas canvas;
	
	public SpaceViewPanel(Space sp) {
		super(new BorderLayout());
		
		canvas = new Canvas();
		add(canvas);
		
		prefs = new StandardPrefs();
		space = sp;
		vp = new Viewport(200, -200, 0, 1d);
		ViewportMouseDrag md = new ViewportMouseDrag();
		canvas.addMouseMotionListener(md);
		canvas.addMouseListener(md);
		canvas.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				vp.zoom(-e.getPreciseWheelRotation(), e.getX(), -e.getY());
			}
		});
		overlays = new ArrayList<>();
		rootWindow = new XYTRenderNode(0,0,0);
		rootWindow.addChild(vp);
		rootWindow.addChild(new RenderNode() {
			{
				children = overlays;
			}
			protected void transform(AffineTransform root) {
				root.scale(1, -1);
			}
		});
		new Timer(30, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (strat == null) {
					return;
				}
				 do {
			         do {
			             Graphics graphics = strat.getDrawGraphics();
			             update(graphics);
			             graphics.dispose();
			         } while (strat.contentsRestored());
			         strat.show();
			     } while (strat.contentsLost());
			}
		}).start();
	}
	

	public void addMouseListener(MouseListener l) {
		canvas.addMouseListener(l);
	}
	
	public void removeMouseListener(MouseListener l) {
		canvas.removeMouseListener(l);
	}
	
	public void startGraphics() {
		canvas.createBufferStrategy(2);
		strat = canvas.getBufferStrategy();
	}
	
	public void addOverlay(RenderNode overlay) {
		overlays.add(overlay);
	}
	
	public void removeOverlay(RenderNode overlay) {
		overlays.remove(overlay);
	}
	
	public void update(Graphics g) {
		prefs = new StandardPrefs();
		//prefs = new BlueprintPrefs();
		setBackground(prefs.spaceColor());
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform cart = new AffineTransform();
		cart.scale(1, -1);
		
		space.blockRunning(true);
		rootWindow.render(g2,cart, prefs);
		space.blockRunning(false);
		
	}
	
	private class Viewport extends XYTRenderNode {
		private double zoom;
		public Viewport(double x, double y, double theta, double zoom) {
			super(x, y, theta);
			this.children = space.rendities();
			this.zoom = zoom;
		}
		public void transform(AffineTransform at) {
			super.transform(at);
			at.scale(zoom, zoom);
			
		}
		public void zoom(double amt, double x, double y) {
			amt = amt/Math.abs(amt);
			double oldworldx = (x-this.x)/zoom;
			double oldworldy = (y-this.y)/zoom;
			zoom += amt * (zoom/10);
			double newworldx = (x-this.x)/zoom;
			double newworldy = (y-this.y)/zoom;
			
			
			this.x -= (oldworldx-newworldx)*zoom;
			this.y -= (oldworldy-newworldy)*zoom;
		}
		public void moveCenter(double x, double y) {
			this.x -= x;
			this.y -= y;
		}
	}
	
	private class ViewportMouseDrag extends MouseAdapter {
		int lx, ly;
		
		public ViewportMouseDrag() {
			lx = ly = -1;
		}
		
		public void mouseDragged(MouseEvent e) {
			if (lx!=-1) {
				vp.moveCenter(lx-e.getX(), ly+e.getY());
				lx = e.getX();
				ly = -e.getY();
			}
		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				lx = e.getX();
				ly = -e.getY();
			}
		}

		public void mouseReleased(MouseEvent e) {
			ly = lx = -1;
		}
	}
}
