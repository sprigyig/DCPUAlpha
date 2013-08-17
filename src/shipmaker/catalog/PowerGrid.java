package shipmaker.catalog;

import java.awt.Graphics2D;

import com.google.gson.annotations.Expose;

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

public class PowerGrid implements CatalogPartType {

	@Expose private static final String name = "Power Grid";
	
	private static final class PowerGridPart implements CatalogPart {
		private PropertyTable table;
		@Expose int hwid;
		@Expose private PowerGrid type;

		public PowerGridPart(PowerGrid type) {
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
			s.power.setHwid((char)hwid);
		}
	}

	public CatalogPart create(final BlueprintLocation pbpl) {
		return new PowerGridPart(this);
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
	}

	public boolean deletable() {
		return false;
	}

}
