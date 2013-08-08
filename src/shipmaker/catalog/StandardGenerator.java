package shipmaker.catalog;

import physics.Body;
import render.OverlayManager;
import render.PropertyTable;
import render.RenderNode;
import shipmaker.CatalogPart;
import shipmaker.CatalogPartType;
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
			
			public RenderNode getOptionsOverlay(OverlayManager om) {
				if (this.table == null) {
					this.table = new PropertyTable(-199, 0, 0, 100, 100);
					this.table.new TableName("Standard Generator");
					this.table.new TableFixedProp("Power/Tick", "18");
					this.table.new TableFixedProp("Mass", ""+mass());
					this.table.new TableFixedProp("Rot Inertia", ""+rotationalInertia());
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

}
