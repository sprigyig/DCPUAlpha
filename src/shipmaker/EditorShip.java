package shipmaker;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import env.Entity;
import physics.XYTSource;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.catalog.PowerGrid;

public class EditorShip {
	
	private static class BPLPreview extends XYTRenderNode implements XYTSource {

		private BlueprintLocation bpl;
		private CatalogPartType type;

		public BPLPreview(BlueprintLocation bpl, CatalogPartType type) {
			super(null);
			this.bpl = bpl;
			this.type = type;
			src = this;
		}

		public float position_x() {
			return (float) (bpl.x + Math.cos(bpl.t1) * bpl.r);
		}

		public float position_y() {
			return (float) (bpl.y + Math.sin(bpl.t1) * bpl.r);
		}

		public float alignment_theta() {
			return bpl.t1 + bpl.t2;
		}
		
		public void draw(Graphics2D g, RenderPreferences prefs) {
			type.preview(g, prefs);
		}
		
	}
	
	public static class EditorShipPart implements Entity {
		public EditorShipPart(CatalogPart part, BlueprintLocation location) {
			super();
			this.part = part;
			this.location = location;
			this.visuals = new BPLPreview(location, part.type());
		}
		private BPLPreview visuals;
		public CatalogPart part;
		public BlueprintLocation location;
		public void tickInternals(int msPerTick) {
		}
		public void tickPhysics(int msPerTick) {
		}
		public RenderNode getVisuals() {
			return visuals;
		}
	}
	
	public static interface ShipWatcher {
		public void partAdded(EditorShipPart p);
		public void partRemoved(EditorShipPart p);
	}
	
	private ArrayList<EditorShipPart> parts;
	private ArrayList<ShipWatcher> watchers;
	
	public EditorShip() {
		parts = new ArrayList<EditorShip.EditorShipPart>();
		watchers = new ArrayList<ShipWatcher>();
		addPart(new PowerGrid());
	}
	
	public EditorShipPart addPart(CatalogPartType type) {
		CatalogPart part = type.create();
		EditorShipPart ret = new EditorShipPart(part, new BlueprintLocation());
		parts.add(ret);
		
		for (ShipWatcher w : watchers) {
			w.partAdded(ret);
		}
		
		return ret;
	}
	
	public void removePart(EditorShipPart e) {
		parts.remove(e);
		for (ShipWatcher w : watchers) {
			w.partRemoved(e);
		}
	}
	
	public Collection<EditorShipPart> parts() {
		return Collections.unmodifiableList(parts);
	}
	
	public void addWatcher(ShipWatcher w) {
		watchers.add(w);
	}
	
	public void removeWatcher(ShipWatcher w) {
		watchers.remove(w);
	}
}
