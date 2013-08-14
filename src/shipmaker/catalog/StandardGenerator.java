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
import equipment.Generator;

public class StandardGenerator implements CatalogPartType {

	public CatalogPart create() {
		return new CatalogPart() {
			
			private PropertyTable table;

			public CatalogPartType type() {
				return StandardGenerator.this;
			}
			
			public RenderNode getRenderRagdoll(Body base) {
				return Generator.makeIndependantPart(base);
			}
			
			public RenderNode getOptionsOverlay(OverlayManager om, BlueprintLocation bpl) {
				if (this.table == null) {
					this.table = new PropertyTable(2, 2, 0, 100, 100, om);
					this.table.new TableName(name());
					this.table.new TableFixedProp("Power/Tick", "17");
					this.table.new TableFixedProp("Power Capacity", "200");
					this.table.new TableFixedProp("Mass", ""+mass());
					this.table.new TableFixedProp("Rot Inertia", ""+rotationalInertia());
					this.table.addPosition(bpl, om);
				}
				return this.table;
			}
			
			public void applyToShip(BlueprintLocation location, Ship s, float centerMassX, float centerMassY) {
				location.convertToXYT(centerMassX, centerMassY);
				
				s.addEquipment(new Generator((int)location.x, (int)location.y, (int)location.t2, 17));
				s.power.capacityAdded(200);
			}
		};
	}

	public String name() {
		return "Generator";
	}

	public float mass() {
		return 800;
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
