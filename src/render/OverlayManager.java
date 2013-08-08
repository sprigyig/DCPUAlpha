package render;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import physics.XYTSource;

public class OverlayManager extends RenderNode implements KeyListener {

	private RenderNode left;
	private RenderNode right;
	private Dimension dimension;
	private FocusableOverlay focused;
	private ArrayList<KeyListener> listeners;
	
	public OverlayManager() {
		this.listeners = new ArrayList<KeyListener>();
		
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
	
	public void addLeft(RenderNode l) {
		left.addChild(l);
	}
	public void addRight(RenderNode r) {
		right.addChild(r);
	}
	public void removeLeft(RenderNode l) {
		left.removeChild(l);
	}
	public void removeRight(RenderNode r) {
		right.removeChild(r);
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
}
