package shipmaker.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import render.FocusableOverlay;
import render.MouseEventType;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.CatalogPartType;
import shipmaker.EditorShip;
import shipmaker.catalog.BasicCapacitor;
import shipmaker.catalog.PositionSensor;
import shipmaker.catalog.StandardEngine;
import shipmaker.catalog.StandardGenerator;
import shipmaker.catalog.Synchronizer;

public class CatalogSelector extends XYTRenderNode implements FocusableOverlay {
	
	private ArrayList<CatalogPartType> types;
	private int scrollPoint;
	private OverlayManager om;
	private CatalogPartType selectedType;
	private EditorShip ship;
	
	private static final int ROW_HEIGHT = 15;
	private static final int ROWS = 8;
	private static final int VPADDING = 10;
	private static final int WIDTH = 200;
	
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
			g.setColor(Color.white);
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			int index = position + scrollPoint;
			if (index >=0 && index < types.size()) {
				if (selected) {
					g.fillRoundRect(0, 0, WIDTH, ROW_HEIGHT, 8, 8);
					g.setColor(prefs.spaceColor());
				} 
				g.drawString(types.get(index).name(), VPADDING, ROW_HEIGHT-2);
			}
			
			
			
		}
		
		public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
			if (t != MouseEventType.MOUSE_PRESS) return false;
			
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
	
	public CatalogSelector(OverlayManager om, EditorShip es) {
		super(0, -(ROWS * ROW_HEIGHT), 0);
		this.ship = es;
		
		scrollPoint = 0;
		types = new ArrayList<CatalogPartType>();
		types.add(new StandardEngine());
		types.add(new StandardGenerator());
		types.add(new BasicCapacitor());
		types.add(new PositionSensor());
		types.add(new Synchronizer());
		
		
		XYTRenderNode list = new XYTRenderNode(0, 0, 0);
		for (int i=0; i<ROWS; i++) {
			list.addChild(new TypeRow(i));
		}
		addChild(list);
		XYTRenderNode preview = new XYTRenderNode(WIDTH + (ROWS*ROW_HEIGHT-40)/2, (ROWS*ROW_HEIGHT+40)/2, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				if (selectedType!=null) {
					g.setColor(Color.white);
					g.setStroke(new BasicStroke(2));
					g.setFont(new Font("SansSerif", Font.BOLD, 12));
					g.drawString("Add", -10, 35);
					int w = ROWS*ROW_HEIGHT-40;
					g.drawRoundRect(-w/2, -w/2, w, w, 8, 8);
					selectedType.preview(g, prefs);
					
				}
			}
			public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
				
				if (e.getButton() == MouseEvent.BUTTON1 && t == MouseEventType.MOUSE_PRESS) {
					Point2D.Float pt = RenderNode.reverse(root, e);
					
					int w = ROWS*ROW_HEIGHT-40;
					if (Math.abs(pt.getX()) < w/2 && Math.abs(pt.getY()) < w/2) {
						ship.addPart(selectedType);
						selectedType = null;
						return true;
					}
					
				}
				return false;
			}
		};
		addChild(preview);
		addChild(new XYTRenderNode(WIDTH+10, 10, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setStroke(new BasicStroke(2));
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 8, 8);
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
				g.setStroke(new BasicStroke(2));
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 8, 8);
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
		g.setStroke(new BasicStroke(2));
		g.drawRoundRect(0, 0, WIDTH, ROWS*ROW_HEIGHT, 8, 8);
		g.setFont(new Font("SansSerif", Font.BOLD, 16));
		g.drawString("Part Blueprints", 10, -4);
	}
	
	public void lostFocus() {
		selectedType = null;
	}

}
