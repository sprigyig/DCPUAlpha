package shipmaker.catalog;

import java.awt.Color;
import java.awt.Graphics2D;

import physics.Body;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import shipmaker.BlueprintLocation;
import shipmaker.CatalogPart;
import shipmaker.CatalogPartType;
import shipmaker.render.PropertyTable;
import ships.Equipment;
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
					this.table.new TableName("Standard Generator", Color.gray.brighter());
					this.table.new TableFixedProp("Power/Tick", "18");
					this.table.new TableFixedProp("Mass", ""+mass());
					this.table.new TableFixedProp("Rot Inertia", ""+rotationalInertia());
					this.table.addPosition(bpl, om);
				}
				return this.table;
			}
			
			public Equipment generateEquipment(float effectiveX, float effectiveY,
					float effectiveTheta) {
				return null;
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
		Generator.draw(g, prefs, 255, false, 20);
	}

}
