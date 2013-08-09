package shipmaker.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;

import render.FocusableOverlay;
import render.MouseEventType;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.CatalogPartType;
import shipmaker.catalog.StandardEngine;
import shipmaker.catalog.StandardGenerator;

public class CatalogSelector extends XYTRenderNode implements FocusableOverlay {
	
	private ArrayList<CatalogPartType> types;
	private int scrollPoint;
	private OverlayManager om;
	private CatalogPartType selectedType;
	
	private static final int ROW_HEIGHT = 15;
	private static final int ROWS = 8;
	private static final int VPADDING = 10;
	private static final int WIDTH = 200;
	private static final int OPTS_WIDTH = 100;
	
	private class TypeRow extends XYTRenderNode {
		private int position;
		
		public TypeRow(int position) {
			super(0, ROW_HEIGHT * position, 0);
			this.position = position;
		}
		
		private CatalogPartType representedType() {
			int index = position + scrollPoint;
			if (index >=0 && index < types.size()) {
				return types.get(index);
			}
			return null;
		}
		
		public void draw(Graphics2D g, RenderPreferences prefs) {
			boolean selected = selectedType == representedType();
			g.setColor(selected ? Color.orange : Color.white);
			g.setFont(new Font("SansSerif", Font.PLAIN, 10));
			int index = position + scrollPoint;
			if (index >=0 && index < types.size()) {
				g.drawString(types.get(index).name(), VPADDING, ROW_HEIGHT-3);
				
				if (selected) {
					g.drawRoundRect(0, 0, WIDTH, ROW_HEIGHT, 5, 5);
				}
			}
			
			
			
		}
		
		public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
			Point2D.Float src = new Point2D.Float();
			src.setLocation(e.getX(), e.getY());
			try {
				root.invert();
			} catch (NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}
			Point2D dest = new Point2D.Float();
			root.transform(src, dest);
			
			if (dest.getY() >=0 && dest.getY()<=ROW_HEIGHT && dest.getX() > 0 && dest.getX() < WIDTH) {
				selectedType = representedType();
				om.setFocused(CatalogSelector.this);
				return true;
			}
			
			return false;
		}
	}
	
	public CatalogSelector(OverlayManager om) {
		super(0, -(ROWS * ROW_HEIGHT), 0);
		scrollPoint = 0;
		types = new ArrayList<CatalogPartType>();
		types.add(new StandardEngine());
		types.add(new StandardGenerator());
		types.add(new StandardEngine() {
			public String name() {
				return super.name()+"2";
			}
		});
		types.add(new StandardGenerator());
		types.add(new StandardEngine(){
			public String name() {
				return super.name()+"3";
			}
		});
		types.add(new StandardGenerator());
		types.add(new StandardEngine(){
			public String name() {
				return super.name()+"4";
			}
		});
		types.add(new StandardGenerator());
		types.add(new StandardEngine(){
			public String name() {
				return super.name()+"5";
			}
		});
		types.add(new StandardGenerator());
		
		XYTRenderNode list = new XYTRenderNode(0, 0, 0);
		for (int i=0; i<ROWS; i++) {
			list.addChild(new TypeRow(i));
		}
		addChild(list);
		XYTRenderNode preview = new XYTRenderNode(VPADDING * 2 + WIDTH + OPTS_WIDTH/2, 50, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				if (selectedType!=null) {
					selectedType.preview(g, prefs);
				}
			}
		};
		addChild(preview);
		addChild(new XYTRenderNode(WIDTH+10, 10, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setStroke(new BasicStroke(1));
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 5, 5);
				g.drawLine(-5, 0, 0, -5);
				g.drawLine(5, 0, 0, -5);
			}
			public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
				
				if (e.getButton() == MouseEvent.BUTTON1 && t == MouseEventType.MOUSE_PRESS) {
					Point2D.Float pt = RenderNode.reverse(root, e);
					
					if (Math.abs(pt.getX()) < 10 && Math.abs(pt.getY()) < 10) {
						scroll(-1);
						return true;
					}
					
					
				}
				return false;
			}
		});
		addChild(new XYTRenderNode(WIDTH+10, 30, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setStroke(new BasicStroke(1));
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 5, 5);
				g.drawLine(-5, 0, 0, 5);
				g.drawLine(5, 0, 0, 5);
			}
			public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
				
				if (e.getButton() == MouseEvent.BUTTON1 && t == MouseEventType.MOUSE_PRESS) {
					Point2D.Float pt = RenderNode.reverse(root, e);
					
					if (Math.abs(pt.getX()) < 10 && Math.abs(pt.getY()) < 10) {
						scroll(1);
						return true;
					}
					
					
				}
				return false;
			}
		});
		this.om = om;
	}
	
	private void scroll(int dist) {
		scrollPoint += dist;
	}
	
	public void draw(Graphics2D g, RenderPreferences prefs) {
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(1));
		g.drawRect(0, 0, WIDTH, ROWS*ROW_HEIGHT);
		
	}
	
	public void lostFocus() {
		selectedType = null;
	}

}
