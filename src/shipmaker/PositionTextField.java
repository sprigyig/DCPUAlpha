package shipmaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import render.MouseEventType;
import render.RenderPreferences;
import render.XYTRenderNode;
import env.Space;

public abstract class PositionTextField extends XYTRenderNode {
	boolean editing;
	String contents;
	private Object suffix;
	private String prefix;

	public PositionTextField(double x, double y, double theta,
			Space s, String prefix, Object suffix) {
		super(x, y, theta);
		
		this.prefix = prefix;
		this.suffix = suffix;
		
		editing = false;
		contents = null;
		s.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (editing) {
					System.out.println(e.getKeyCode());
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (contents.length()>0) {
							set(Float.parseFloat(contents));
						}
						editing = false;
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						editing = false;
					} else if (e.getKeyCode() >= KeyEvent.VK_0 &&
							e.getKeyCode() <= KeyEvent.VK_9) {
						contents+=(e.getKeyChar());
					} else if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
						if (contents.indexOf('.')==-1) {
							contents+='.';
						}
					} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
						if (contents.length() > 0)
						contents = contents.substring(0,contents.length()-1);
					} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
						if (contents.length() == 0) {
							contents+="-";
						}
					}
					
				}
			}
			
			public void keyReleased(KeyEvent e) {
				
			}
			
			public void keyPressed(KeyEvent e) {
				
			}
		});
	}

	public void draw(Graphics2D g, RenderPreferences prefs) {
		g.setColor(Color.white);
		g.setFont(new Font("SansSerif", Font.BOLD, 14));
		g.drawRoundRect(-10, -10, 20, 20, 5, 5);
		drawIcon(g);
		if (editing) {
			g.drawString(prefix+contents+"\u258c"+suffix, 15, 5);
		} else {
			g.drawString(prefix+String.format("%.04f", get())+suffix, 15, 5);
		}
	}

	public abstract void drawIcon(Graphics2D g);

	public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
		if (t != MouseEventType.MOUSE_PRESS) return false;
		if (!editing) {
			Point2D.Float src = new Point2D.Float();
			src.setLocation(e.getX(), e.getY());
			try {
				root.invert();
			} catch (NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}
			Point2D dest = new Point2D.Float();
			root.transform(src, dest);
			
			if (Math.abs(dest.getX())<10 && Math.abs(dest.getY())<10) {
				contents = "";
				editing = true;
				return true;
			}
		} else {
			editing = false;
		}
		return false;
	}
	public abstract void set(float f);
	public abstract float get();
}