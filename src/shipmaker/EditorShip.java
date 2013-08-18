package shipmaker;

import java.awt.Graphics2D;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import physics.XYTSource;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.catalog.PartCatalog;
import ships.Ship;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;

import env.Entity;
import equipment.Structure;
import equipment.StructureNode;

public class EditorShip {

	private static final float RI_DISTANCE_DIVIDER = 10f;

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
		@Expose
		public CatalogPart part;
		@Expose
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

	@Expose
	private ArrayList<EditorShipPart> parts;
	@Expose private HashSet<StructureNode> structParts;
	private ArrayList<ShipWatcher> watchers;

	public boolean editingStructure = false;
	
	public EditorShip() {
		parts = new ArrayList<EditorShip.EditorShipPart>();
		structParts = new HashSet<StructureNode>();
		watchers = new ArrayList<ShipWatcher>();
	}

	public EditorShipPart addPart(CatalogPartType type) {
		BlueprintLocation bpl = new BlueprintLocation();
		CatalogPart part = type.create(bpl);
		EditorShipPart ret = new EditorShipPart(part, bpl);
		parts.add(ret);

		for (ShipWatcher w : watchers) {
			w.partAdded(ret);
		}

		return ret;
	}

	public float massCenterX() {
		float massX = 0f, massTotal = 0f;
		for (EditorShipPart pt : parts) {
			massX += pt.part.type().mass() * pt.location.effectiveX();
			massTotal += pt.part.type().mass();
		}
		return massX / massTotal;
	}

	public float massCenterY() {
		float massY = 0f, massTotal = 0f;
		for (EditorShipPart pt : parts) {
			massY += pt.part.type().mass() * pt.location.effectiveY();
			massTotal += pt.part.type().mass();
		}
		return massY / massTotal;
	}
	
	public float massTotal() {
		float total = 0f;
		
		for (EditorShipPart pt : parts) {
			total += pt.part.type().mass();
		}
		
		return total;
	}

	public float riTotal() {
		float massX = 0f, massY = 0f, massTotal = 0f;

		for (EditorShipPart pt : parts) {
			massX += pt.part.type().mass() * pt.location.effectiveX();
			massY += pt.part.type().mass() * pt.location.effectiveY();
			massTotal += pt.part.type().mass();

		}

		float massCenterX = massX / massTotal;
		float massCenterY = massY / massTotal;

		float ri = 0;
		for (EditorShipPart pt : parts) {
			if (pt.part.type().placeable()) {
				ri += pt.part.type().rotationalInertia();
				float dx = massCenterX - pt.location.effectiveX();
				float dy = massCenterY - pt.location.effectiveY();
	
				dx /= RI_DISTANCE_DIVIDER;
				dy /= RI_DISTANCE_DIVIDER;
	
				ri += pt.part.type().mass() * (dy * dy + dx * dx);
			}
		}
		return ri;
	}
	public Ship makeShip() {
		float massX = 0f, massY = 0f, massTotal = 0f;

		for (EditorShipPart pt : parts) {
			massX += pt.part.type().mass() * pt.location.effectiveX();
			massY += pt.part.type().mass() * pt.location.effectiveY();
			massTotal += pt.part.type().mass();

		}

		float massCenterX = massX / massTotal;
		float massCenterY = massY / massTotal;

		float ri = 0;
		for (EditorShipPart pt : parts) {
			ri += pt.part.type().rotationalInertia();
			float dx = massCenterX - pt.location.effectiveX();
			float dy = massCenterY - pt.location.effectiveY();

			dx /= RI_DISTANCE_DIVIDER;
			dy /= RI_DISTANCE_DIVIDER;

			ri += pt.part.type().mass() * (dy * dy + dx * dx);
		}

		Ship s = new Ship(massTotal, ri);

		for (EditorShipPart pt : parts) {
			pt.part.applyToShip(pt.location, s, massCenterX, massCenterY);
		}
		
		Structure st = new Structure();
		for (StructureNode sn : structParts) {
			st.addLocation(sn.x, sn.y);
		}
		s.addEquipment(st);
		return s;

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

	public Collection<StructureNode> structLocations() {
		return structParts;
	}
	
	public void addWatcher(ShipWatcher w) {
		watchers.add(w);
	}

	public void removeWatcher(ShipWatcher w) {
		watchers.remove(w);
	}

	public static EditorShip fromJson(String json) {
		EditorShip ship;
		
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(EditorShipPart.class,
				new JsonDeserializer<EditorShipPart>() {

					public EditorShipPart deserialize(JsonElement obj,
							Type type, JsonDeserializationContext ctx)
							throws JsonParseException {
						BlueprintLocation bpl = ctx.deserialize(obj.getAsJsonObject().get("location"), BlueprintLocation.class);
						System.out.println(obj.toString());
						String types = obj.getAsJsonObject().get("part").getAsJsonObject().get("type").getAsJsonObject().get("name").getAsString();
						try {
							CatalogPartType tp = PartCatalog.getTypeByName(types);
							CatalogPart part = tp.create(bpl);
							part.loadOptions(obj.getAsJsonObject().get("part").getAsJsonObject());
							return new EditorShipPart(part, bpl);
						} catch (Throwable e) {
							e.printStackTrace();
						}
						
						return null;
					}
				});
		ship = gb.create().fromJson(json, EditorShip.class);
		
		return ship;
	}

	public boolean validateStructureContinuity() {
		if (structParts.isEmpty()) return false;
		
		HashSet<StructureNode> visited = new HashSet<>();
		LinkedList<StructureNode> queue = new LinkedList<>();

		queue.add(structParts.iterator().next());
		
		while (!queue.isEmpty()) {
			StructureNode node = queue.poll();
			if (!visited.contains(node))
			for (int dx=-1;dx<=1;dx++) for (int dy=-1;dy<=1;dy++)
				if (dx == 0 ^ dy==0) {
					StructureNode newNode = new StructureNode(node.x+dx, node.y+dy);
					if (structParts.contains(newNode)) {
						queue.add(newNode);
					}
				}
			visited.add(node);
		}
		
		return visited.size() == structParts.size();
	}
	
}