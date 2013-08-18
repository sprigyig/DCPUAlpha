package shipmaker.catalog;

import java.awt.Graphics2D;

import physics.Body;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import shipmaker.BlueprintLocation;
import shipmaker.CatalogPart;
import shipmaker.CatalogPartType;
import shipmaker.render.PropertyTable;
import ships.Ship;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import equipment.Capacitor;

public class BasicCapacitor implements CatalogPartType {
	
	@Expose private final String name = "Basic Capacitor";

	private static final class BasicCapacitorPart implements CatalogPart {
		private PropertyTable table;
		@Expose private BasicCapacitor type;

		public BasicCapacitorPart(BlueprintLocation pbpl, BasicCapacitor type) {
			this.type = type;
		}

		public CatalogPartType type() {
			return type;
		}

		public RenderNode getRenderRagdoll(Body base) {
			return null;
		}

		public RenderNode getOptionsOverlay(OverlayManager om, BlueprintLocation bpl) {
			if (table == null) {
				table = new PropertyTable(0, 0, 0, 100, 100, om);
				table.new TableName(type.name());
				table.new TableFixedProp("Capacity", "400");
				table.new TableFixedProp("Rot Inertia", ""+type.rotationalInertia());
				table.new TableFixedProp("mass", ""+type.mass());
				table.addPosition(bpl, om);
				
			}
			return table;
		}

		public void applyToShip(BlueprintLocation location, Ship s,
				float centerMassX, float centerMassY) {
			s.power.capacityAdded(400);
			location.convertToXYT(centerMassX, centerMassY);
			s.addEquipment(new Capacitor(location.x, location.y, location.t2));
		}

		public void loadOptions(JsonObject jobj) {
			//nothing to load
		}
	}




	public CatalogPart create(final BlueprintLocation pbpl) {
		return new BasicCapacitorPart(pbpl, this);
	}

	public String name() {
		return name;
	}

	public float mass() {
		return 250;
	}

	public float rotationalInertia() {
		return 1000;
	}

	public boolean placeable() {
		return true;
	}

	public void preview(Graphics2D g, RenderPreferences prefs) {
		Capacitor.draw(g, prefs, prefs.spaceColor(), false);
	}

	public boolean deletable() {
		return true;
	}
}
