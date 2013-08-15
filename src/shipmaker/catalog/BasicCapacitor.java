package shipmaker.catalog;

import java.awt.Graphics2D;

import physics.Body;
import equipment.Capacitor;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import shipmaker.BlueprintLocation;
import shipmaker.CatalogPart;
import shipmaker.CatalogPartType;
import shipmaker.render.PropertyTable;
import ships.Ship;

public class BasicCapacitor implements CatalogPartType {

	public CatalogPart create() {
		return new CatalogPart() {
			
			private PropertyTable table;

			public CatalogPartType type() {
				return BasicCapacitor.this;
			}
			
			public RenderNode getRenderRagdoll(Body base) {
				return null;
			}
			
			public RenderNode getOptionsOverlay(OverlayManager om, BlueprintLocation bpl) {
				if (table == null) {
					table = new PropertyTable(0, 0, 0, 100, 100, om);
					table.new TableName(name());
					table.new TableFixedProp("Capacity", "400");
					table.new TableFixedProp("Rot Inertia", ""+rotationalInertia());
					table.new TableFixedProp("mass", ""+mass());
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
		};
	}

	public String name() {
		return "Basic Capacitor";
	}

	public float mass() {
		return 2000;
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
