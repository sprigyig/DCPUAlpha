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

import equipment.Generator;

public class StandardGenerator implements CatalogPartType {

	@Expose private final String name = "Generator";
	
	private static final class StandardGeneratorPart implements CatalogPart {
		private PropertyTable table;
		@Expose private StandardGenerator type;

		public StandardGeneratorPart(StandardGenerator type) {
			this.type = type;
		}

		public CatalogPartType type() {
			return type;
		}

		public RenderNode getRenderRagdoll(Body base) {
			return Generator.makeIndependantPart(base);
		}

		public RenderNode getOptionsOverlay(OverlayManager om, BlueprintLocation bpl) {
			if (this.table == null) {
				this.table = new PropertyTable(2, 2, 0, 100, 100, om);
				this.table.new TableName(type.name());
				this.table.new TableFixedProp("Power/Tick", "17");
				this.table.new TableFixedProp("Power Capacity", "200");
				this.table.new TableFixedProp("Mass", ""+type.mass());
				this.table.new TableFixedProp("Rot Inertia", ""+type.rotationalInertia());
				this.table.addPosition(bpl, om);
			}
			return this.table;
		}

		public void applyToShip(BlueprintLocation location, Ship s, float centerMassX, float centerMassY) {
			location.convertToXYT(centerMassX, centerMassY);
			
			s.addEquipment(new Generator((int)location.x, (int)location.y, (int)location.t2, 17));
			s.power.capacityAdded(200);
		}
		
		public void loadOptions(JsonObject jobj) {
			//nothing to load
		}
	}

	public CatalogPart create(final BlueprintLocation pbpl) {
		return new StandardGeneratorPart(this);
	}

	public String name() {
		return name ;
	}

	public float mass() {
		return 2000;
	}

	public float rotationalInertia() {
		return 4000;
	}

	public void preview(Graphics2D g, RenderPreferences prefs) {
		Generator.draw(g, prefs, prefs.spaceColor(), false, 20);
	}

	public boolean placeable() {
		return true;
	}
	public boolean deletable() {
		return true;
	}
}
