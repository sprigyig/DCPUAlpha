package shipmaker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class EditorShip {
	
	public static class EditorShipPart {
		public EditorShipPart(CatalogPart part, BlueprintLocation location) {
			super();
			this.part = part;
			this.location = location;
		}
		public CatalogPart part;
		public BlueprintLocation location;
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
