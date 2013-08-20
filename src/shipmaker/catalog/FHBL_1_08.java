package shipmaker.catalog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import physics.Body;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import equipment.BeaconTracker;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import shipmaker.BlueprintLocation;
import shipmaker.CatalogPart;
import shipmaker.CatalogPartType;
import shipmaker.partplacer.HexTextControl;
import shipmaker.render.PropertyTable;
import ships.Ship;

public class FHBL_1_08 implements CatalogPartType {

	public static class FHBL_1_08Part implements CatalogPart {
		@Expose private FHBL_1_08 type;
		@Expose private int hwid;
		private PropertyTable table;

		public FHBL_1_08Part(FHBL_1_08 type) {
			this.type = type;
		}

		public RenderNode getRenderRagdoll(Body base) {
			return null;
		}

		public void applyToShip(BlueprintLocation location, Ship s,
				float centerMassX, float centerMassY) {
			s.addEquipment(new BeaconTracker(8, 1, (char)hwid));
		}

		public RenderNode getOptionsOverlay(OverlayManager om,
				BlueprintLocation bpl) {
			if (table == null) {
				table = new PropertyTable(0, 0, 0, 100, 100, om);
				table.new TableName(type.name());
				table.new TableFixedProp("Strength", "8");
				table.new TableFixedProp("Tracking Slots", "1");
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

		public CatalogPartType type() {
			return type;
		}

		public void loadOptions(JsonObject jobj) {
			hwid = jobj.get("hwid").getAsInt();
		}

		public int partRadius() {
			return 0;
		}
		
	}
	
	public CatalogPart create(BlueprintLocation pbpl) {
		return new FHBL_1_08Part(this);
	}

	public String name() {
		return "FHBL-1-08";
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
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		
		g.setColor(Color.white);
		g.drawLine(0, -20, 0, 20);
		g.drawLine(-20, 0, 20, 0);
		
		g.drawArc(-15, -15, 30, 30, 20, 350);
		g.drawArc(-5, -5, 10, 10, 30, 330);
		g.drawArc(-10, -10, 20, 20, 40, 340);
		
	}

	public boolean deletable() {
		return true;
	}
}
