package shipmaker.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import physics.XYTSource;
import render.MouseEventType;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.EditorShip;
import shipmaker.EditorShip.EditorShipPart;
import shipmaker.EditorShip.ShipWatcher;
import env.Space;

public class ShipContents extends XYTRenderNode implements ShipWatcher {
	private static final int ROW_HEIGHT = 20;
	
	EditorShip ship;
	Space space;
	OverlayManager om;
	
	private ArrayList<PartLabel> labels;
	

	private class PartLabel extends XYTRenderNode implements XYTSource {
		int index;
		EditorShipPart part;
		
		public PartLabel(EditorShipPart p) {
			super(0, 0, 0);
			src = this;
			this.part = p;
			addChild(new XYTRenderNode(-20, 0, 0) {
				public void draw(Graphics2D g, RenderPreferences prefs) {
					g.setColor(Color.red);
					g.setStroke(new BasicStroke(3));
					g.drawLine(5, 5, 15, 15);
					g.drawLine(5, 15, 15, 5);
					
				}
				public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
					if (t == MouseEventType.MOUSE_PRESS) {
						Point2D.Float pt = RenderNode.reverse(root, e);
						
						if (pt.x > 0 && pt.x < 20 && pt.y > 0 && pt.y < 20) {
							System.out.println("GOTIT");
							ship.removePart(part);
						}
					}
					return false;
				}
			});
		}

		public float position_x() {
			return 0;
		}

		public float position_y() {
			return index * ROW_HEIGHT;
		}

		public float alignment_theta() {
			return 0;
		}
		
		public void draw(Graphics2D g, RenderPreferences prefs) {
			g.setColor(Color.white);
			int fh = 14;
			g.setFont(new Font("SansSerif", Font.BOLD, fh));
			
			int mid = ROW_HEIGHT/2;
			
			g.drawString(part.part.type().name(), 0, mid+fh/2);
		}
	}
	
	public ShipContents(EditorShip ship, Space space, OverlayManager om) {
		super(-200, 0, 0);
		this.ship = ship;
		this.space = space;
		this.om = om;
		ship.addWatcher(this);
		labels = new ArrayList<ShipContents.PartLabel>();
	}
	public void partAdded(EditorShipPart p) {
		PartLabel pl = new PartLabel(p);
		pl.index = labels.size();
		labels.add(pl);
		addChild(pl);
	}
	public void partRemoved(EditorShipPart p) {
		PartLabel pl = null;
		for (PartLabel pli : labels) {
			if (pli.part == p) {
				pl = pli;
				break;
			}
		}
		if (pl != null) {
			labels.remove(pl);
			removeChild(pl);
			for (int i=0; i<labels.size(); i++) {
				labels.get(i).index = i;
			}
		}
	}
	
	
}
