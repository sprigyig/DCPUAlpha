package shipmaker.catalog;

import java.awt.Graphics2D;

import physics.Body;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import shipmaker.BlueprintLocation;
import shipmaker.CatalogPart;
import shipmaker.CatalogPartType;
import shipmaker.partplacer.HexTextControl;
import shipmaker.render.PropertyTable;
import ships.Ship;
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
			
			public RenderNode getOptionsOverlay(final OverlayManager om, BlueprintLocation bpl) {
				if (this.table == null) {
					this.table = new PropertyTable(2, 2, 0, 100, 100, om);
					this.table.new TableName(name());
					this.table.new TableFixedProp("Power/Tick", "10 (Max)");
					this.table.new TableFixedProp("Force", "100 (Max)");
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
					this.table.addPosition(bpl, om);
				}
				return this.table;
			}
			
			public void applyToShip(BlueprintLocation location, Ship s, float centerMassX, float centerMassY) {
				location.convertToRTT(centerMassX, centerMassY);
				s.addEquipment(new Engine(location.r, location.t1, location.t2, 500, 10, hwid));
			}
		};
	}

	public String name() {
		return "Standard Engine";
	}

	public float mass() {
		return 750;
	}

	public float rotationalInertia() {
		return 8000;
	}

	public void preview(Graphics2D g, RenderPreferences prefs) {
		Engine.draw(g, prefs, 1f);
	}

	public boolean placeable() {
		return true;
	}
	public boolean deletable() {
		return true;
	}
}
