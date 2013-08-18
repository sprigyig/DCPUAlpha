package render;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import physics.XYTSource;

public class OverlayManager extends RenderNode implements KeyListener {

	private RenderNode left;
	private RenderNode right;
	private RenderNode bottomLeft;
	private Dimension dimension;
	private FocusableOverlay focused;
	private ArrayList<KeyListener> listeners;
	public Runnable lowPriorityInteraction;
	public Runnable nonproductiveClick;
	public ArrayList<Runnable> afterInteraction;
	private XYTRenderNode topCenter;
	
	public OverlayManager() {
		this.listeners = new ArrayList<KeyListener>();
		afterInteraction = new ArrayList<>();
		
		addChild(left = new XYTRenderNode(0,0,0));
		addChild(right = new XYTRenderNode(new XYTSource() {
			public float position_y() {
				return 0;
			}
			public float position_x() {
				return dimension.width;
			}
			public float alignment_theta() {
				return 0;
			}
			
		}));
		addChild(bottomLeft = new XYTRenderNode(new XYTSource() {
			public float position_y() {
				return dimension.height;
			}
			public float position_x() {
				return 0;
			}
			
			public float alignment_theta() {
				return 0;
			}
		}));
		addChild(topCenter = new XYTRenderNode(new XYTSource() {
			public float position_y() {
				return 0;
			}
			
			public float position_x() {
				return dimension.width/2;
			}
			
			public float alignment_theta() {
				return 0;
			}
		}));
	}

	protected void transform(AffineTransform root) {
		root.scale(1, -1);
	}
	
	public void setWindowDimensions(Dimension d) {
		this.dimension = d;
	}
	

	public void setFocused(FocusableOverlay tic) {
		if (focused !=null && focused != tic) {
			focused.lostFocus();
		}
		this.focused = tic;
	}
	
	
	public RenderNode topLeft() {
		return left;
	}
	
	public RenderNode topRight() {
		return right;
	}
	
	public RenderNode topCenter() {
		return topCenter;
	}
	
	public RenderNode bottomLeft() {
		return bottomLeft;
	}
	
	public void keyPressed(KeyEvent ke) {
		for (KeyListener l : listeners) {
			l.keyTyped(ke);
		}
	}

	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	
	public void addKeyListener(KeyListener l) {
		listeners.add(l);
	}
	
	public void removeKeyListener(KeyListener l) {
		listeners.remove(l);
	}
	
	public void draw(Graphics2D g, RenderPreferences prefs) {
		prefs.setWindow(dimension);
	}
	
	public void afterInteraction(Runnable r) {
		afterInteraction.add(r);
	}
	
	public boolean interaction(AffineTransform root, MouseEvent e, MouseEventType t) {
		boolean result = super.interaction(root, e, t);
		
		for (Runnable r : afterInteraction) {
			r.run();
		}
		afterInteraction.clear();
		return result;
	}

	public void clear() {
		left.bgChildren.clear();
		left.children.clear();
		right.bgChildren.clear();
		right.children.clear();
		bottomLeft.children.clear();
		bottomLeft.bgChildren.clear();
		topCenter.bgChildren.clear();
		topCenter.children.clear();
		setFocused(null);
		listeners.clear();
		lowPriorityInteraction = null;
		nonproductiveClick = null;
		afterInteraction.clear();
	}
}
