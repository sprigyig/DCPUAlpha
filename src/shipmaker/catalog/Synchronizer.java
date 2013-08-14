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
import shipmaker.partplacer.HexTextControl;
import shipmaker.render.PropertyTable;
import ships.Ship;

public class Synchronizer implements CatalogPartType {

	public CatalogPart create() {
		return new CatalogPart() {
			private char hwid;
			private PropertyTable table;

			public CatalogPartType type() {
				return Synchronizer.this;
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
							hwid = (char)x;
						}
						protected int get() {
							return (int)hwid;
						}
					}, om);
				}
				return table;
			}
			
			public void applyToShip(BlueprintLocation location, Ship s,
					float centerMassX, float centerMassY) {
				s.addEquipment(new equipment.Synchronizer(hwid));
			}
		};
	}

	public String name() {
		return "Synchronizer";
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
		g.setColor(Color.white);

		g.drawLine(0, -10, 0, 0);
		g.drawLine(12, -12, 0, 0);
		
		for (int i=0; i<12; i++) {
			float theta = (float) (Math.PI/6 * i);
			
			float xscale = (float) Math.cos(theta);
			float yscale = (float) Math.sin(theta);
			
			
			g.drawLine((int)(xscale*20), (int)(yscale*20), (int)(xscale*17), (int)(yscale*17));
		}
		
	}

	public boolean deletable() {
		return true;
	}

}
