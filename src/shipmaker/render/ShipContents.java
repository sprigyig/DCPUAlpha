package shipmaker.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import dcpu.WorldPauseHandler;
import physics.XYTSource;
import render.CollectionRemoval;
import render.MouseEventType;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.EditorShip;
import shipmaker.EditorShip.EditorShipPart;
import shipmaker.EditorShip.ShipWatcher;
import shipmaker.partplacer.BlueprintPositionEditor;
import env.Entity;
import env.Space;
import equipment.Structure;
import equipment.StructureNode;

public class ShipContents extends XYTRenderNode implements ShipWatcher {
	private static final int ROW_HEIGHT = 20;

	EditorShip ship;
	Space space;
	OverlayManager om;

	EditorShipPart selected;
	BlueprintPositionEditor editor;
	RenderNode options;

	private ArrayList<PartLabel> labels;

	private class PartLabel extends XYTRenderNode implements XYTSource {
		int index;
		EditorShipPart part;

		public PartLabel(EditorShipPart p) {
			super(0, 0, 0);
			src = this;
			this.part = p;
			if (p.part.type().deletable()) {
				addChild(new XYTRenderNode(-20, 0, 0) {
					public void draw(Graphics2D g, RenderPreferences prefs) {
						g.setColor(Color.red);
						g.setStroke(new BasicStroke(3));
						g.drawLine(5, 5, 15, 15);
						g.drawLine(5, 15, 15, 5);

					}

					public boolean interacted(AffineTransform root,
							MouseEvent e, MouseEventType t) {

						if (t == MouseEventType.MOUSE_PRESS) {
							Point2D.Float pt = RenderNode.reverse(root, e);
							if (pt.x > 0 && pt.x < 20 && pt.y > 0 && pt.y < 20) {
								ship.removePart(part);
								return true;
							}
						}
						return false;
					}
				});
			}
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
			g.setColor(selected == part ? Color.orange : Color.white);
			int fh = 14;
			g.setFont(new Font("SansSerif", Font.BOLD, fh));

			int mid = ROW_HEIGHT / 2;

			g.drawString(part.part.type().name(), 0, mid + fh / 2);
		}

		public boolean interacted(AffineTransform root, MouseEvent e,
				MouseEventType t) {

			if (t == MouseEventType.MOUSE_PRESS) {
				Point2D.Float pt = RenderNode.reverse(root, e);
				if (pt.x > 0 && pt.x < 200 && pt.y > 0 && pt.y < ROW_HEIGHT) {
					setSelected(part);
					return true;
				}
			}
			return false;
		}
	}

	public ShipContents(final EditorShip ship, Space space, OverlayManager om) {
		super(-200, 0, 0);
		this.ship = ship;
		this.space = space;
		this.om = om;
		ship.addWatcher(this);
		labels = new ArrayList<ShipContents.PartLabel>();
		om.nonproductiveClick = new Runnable() {
			public void run() {
				setSelected(null);
			}
		};
		for (EditorShipPart p : ship.parts()) {
			partAdded(p);
		}
		
		XYTSource cmass = new XYTSource() {
			public float position_y() {
				return ship.massCenterY();
			}
			
			public float position_x() {
				return ship.massCenterX();
			}
			
			public float alignment_theta() {
				return 0;
			}
		};
		final XYTRenderNode centerMass = new XYTRenderNode(cmass) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(Color.white);
				g.setStroke(new BasicStroke(1));
				g.drawLine(-5, -5, 5, 5);
				g.drawLine(-5, 5, 5, -5);
			}
		};
		space.addEntity(new Entity() {
			public void tickPhysics(int msPerTick, WorldPauseHandler handler) {
			}
			
			public void tickInternals(int msPerTick, WorldPauseHandler handler) {

			}
			
			public RenderNode getVisuals() {
				return centerMass;
			}
		});
		final RenderNode structNode = new XYTRenderNode(0,0,0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				if (ship.editingStructure)
				Structure.draw(g, prefs, ship.structLocations());
				
				g.setColor(prefs.overlayTextColor());
				
				if (ship.editingStructure)
				for (StructureNode node : ship.structLocations()) {
					for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++)
						if ((dx ==0 ^ dy==0) ||( dx==0 && dy==0)) {
							g.fillRect(Structure.STRUCT_SIZE*(node.x+dx)-2, 
									Structure.STRUCT_SIZE*(node.y+dy)-2, 
									4, 4);
						}
				}
				
			}
			public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
				if (t!=MouseEventType.MOUSE_PRESS) return false;
				
				Point2D.Float pt = RenderNode.reverse(root, e);
				
				float nx = Math.round(pt.x/Structure.STRUCT_SIZE)*Structure.STRUCT_SIZE;
				float ny = Math.round(pt.y/Structure.STRUCT_SIZE)*Structure.STRUCT_SIZE;
				
				if (Math.abs(nx - pt.x)<5 && Math.abs(ny - pt.y)<5) {
					StructureNode node = new StructureNode(Math.round(pt.x/Structure.STRUCT_SIZE), Math.round(pt.y/Structure.STRUCT_SIZE));
					if (ship.structLocations().contains(node)) {
						ship.structLocations().remove(node);
						if (!ship.validateStructureContinuity()) ship.structLocations().add(node);
					} else {
						ship.structLocations().add(node);
						if (!ship.validateStructureContinuity()) ship.structLocations().remove(node);
					}
					return true;
				}
				
				return false;
			}
			public int layer() {
				return 2;
			}
		};
		space.addEntity(new Entity() {
			public void tickPhysics(int msPerTick, WorldPauseHandler handler) {
				
			}
			
			public void tickInternals(int msPerTick, WorldPauseHandler handler) {
				
			}
			
			public RenderNode getVisuals() {
				return structNode;
			}
		});
	}

	private void setSelected(EditorShipPart p) {
		selected = p;

		if (options != null) {
			om.topLeft().removeChild(options);
		}

		if (p == null && editor != null) {
			om.afterInteraction(space.removeEntityLater(editor));
			editor = null;
			return;
		}

		if (p != null && p.part.type().placeable()) {
			if (editor == null) {
				editor = new BlueprintPositionEditor(p.location);
				space.addEntity(editor);
			}
			editor.bpl(p.location);
			space.addEntity(p);
		} else if (editor != null) {
			space.removeEntity(editor);
			editor = null;
		}
		if (p != null) {
			options = p.part.getOptionsOverlay(om, p.location);
			om.topLeft().addChild(options);

		}

	}

	public void partAdded(final EditorShipPart p) {
		final PartLabel pl = new PartLabel(p);
		pl.index = labels.size();
		labels.add(pl);
		addChild(pl);
		setSelected(p);
		p.getVisuals().addChild(new RenderNode() {
			protected void transform(AffineTransform root) {
			}

			public boolean interacted(AffineTransform root, MouseEvent e,
					MouseEventType t) {
				if (t != MouseEventType.MOUSE_PRESS)
					return false;

				Point2D.Float pt = RenderNode.reverse(root, e);
				if (pt.distance(0, 0) < 20
						&& p != selected
						&& (selected == null || selected.location
								.distance(p.location) > 20)) {
					if (om.lowPriorityInteraction == null) {
						om.lowPriorityInteraction = new Runnable() {
							public void run() {
								setSelected(p);
							}

						};
					}
				}
				return false;
			}

			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(new Color(255, 255, 255, 20));
				g.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_MITER, 10, new float[] { 5 }, 0));
				g.drawArc(-p.part.partRadius(), -p.part.partRadius(), 
						2*p.part.partRadius(), 2*p.part.partRadius(), 0, 360);
			}
		});

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

			final PartLabel fpl = pl;
			om.afterInteraction(new CollectionRemoval<RenderNode>(children, pl));
			om.afterInteraction(space.removeEntityAction(fpl.part));

			if (fpl.part == selected) {
				setSelected(null);
			}

			for (int i = 0; i < labels.size(); i++) {
				labels.get(i).index = i;
			}
		}
	}

}
