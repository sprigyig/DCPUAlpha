package shipmaker.catalog;

import java.awt.Graphics2D;

import physics.Body;
import render.OverlayManager;
import render.PropertyTable;
import render.RenderNode;
import render.RenderPreferences;
import shipmaker.CatalogPart;
import shipmaker.CatalogPartType;
import shipmaker.partplacer.HexTextControl;
import ships.Equipment;
import equipment.Engine;

public class StandardEngine implements CatalogPartType {

	public CatalogPart create() {
		return new CatalogPart() {
			RenderNode ragdoll;
			char hwid;
			private PropertyTable table;
			
			public CatalogPartType type() {
				return StandardEngine.this;
			}
			
			public RenderNode getRenderRagdoll(Body base) {
				if (ragdoll == null) {
					ragdoll = Engine.makeIndependantPart(base); 
				}
				return ragdoll;
			}
			
			public RenderNode getOptionsOverlay(final OverlayManager om) {
				if (this.table == null) {
					this.table = new PropertyTable(-199, 0, 0, 100, 100);
					this.table.new TableName("Standard Engine");
					this.table.new TableFixedProp("Power/Tick", "10");
					this.table.new TableFixedProp("Force", "100");
					this.table.new TableFixedProp("Mass", ""+mass());
					this.table.new TableFixedProp("Rot Inertia", ""+rotationalInertia());
					this.table.new TableSetProp("Hardware ID", new HexTextControl(4	) {
						public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
							return false;
						}
						
						protected void set(int x) {
							hwid = (char)x;
						}
						
						protected int get() {
							return (int)hwid;
						}
					}, om);
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
		return "Standard Engine";
	}

	public float mass() {
		return 25;
	}

	public float rotationalInertia() {
		return 2000;
	}

}
