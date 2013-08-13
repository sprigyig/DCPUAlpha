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
import ships.Equipment;

public class PowerGrid implements CatalogPartType {

	public CatalogPart create() {
		return new CatalogPart() {
			
			private PropertyTable table;
			int hwid;

			public CatalogPartType type() {
				return PowerGrid.this;
			}
			
			public RenderNode getRenderRagdoll(Body base) {
				return null;
			}
			
			public RenderNode getOptionsOverlay(OverlayManager om, BlueprintLocation bpl) {
				if (table == null) {
					table = new PropertyTable(0, 0, 0, 100, 100, om);
					table.new TableName(name());
					table.new TableSetProp("Hardware ID", new HexTextControl(4) {
						
						public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
							return false;
						}
						
						protected void set(int x) {
							hwid = x;
						}
						
						protected int get() {
							return hwid;
						}
					}, om);
				}
				return table;
			}
			
			public Equipment generateEquipment(float effectiveX, float effectiveY,
					float effectiveTheta) {
				return null;
			}
		};
	}

	public String name() {
		return "Power Grid";
	}

	public float mass() {
		return 0;
	}

	public float rotationalInertia() {
		return 0;
	}

	public boolean placeable() {
		return false;
	}

	public void preview(Graphics2D g, RenderPreferences prefs) {
	}

	public boolean deletable() {
		return false;
	}

}
