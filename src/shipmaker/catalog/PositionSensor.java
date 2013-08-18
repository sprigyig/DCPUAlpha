package shipmaker.catalog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import demo.equipment.DemoSensor;
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

public class PositionSensor implements CatalogPartType {

	@Expose private final String name = "Debug Position Sensor";
	
	private static final class PositionSensorPart implements CatalogPart {
		private PropertyTable table;
		@Expose private int hwid;
		@Expose private PositionSensor type;

		public PositionSensorPart(PositionSensor type) {
			this.type = type;
		}

		public CatalogPartType type() {
			return type;
		}

		public RenderNode getRenderRagdoll(Body base) {
			return null;
		}

		public RenderNode getOptionsOverlay(OverlayManager om, BlueprintLocation bpl) {
			if (table == null) {
				table = new PropertyTable(0, 0, 0, 100, 100, om);
				table.new TableName(type.name());
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

		public void applyToShip(BlueprintLocation location, Ship s, float centerMassX, float centerMassY) {
			s.addEquipment(new DemoSensor((char)hwid));
		}
		
		public void loadOptions(JsonObject jobj) {
			hwid = jobj.get("hwid").getAsInt();
		}
	}

	public CatalogPart create(final BlueprintLocation pbpl) {
		return new PositionSensorPart(this);
	}

	public String name() {
		return name;
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
