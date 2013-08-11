package shipmaker.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import render.OverlayManager;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.BlueprintLocation;
import shipmaker.partplacer.FloatTextControl;
import shipmaker.partplacer.TextInputControl;
import shipmaker.render.IconEditField;

public class PropertyTable extends XYTRenderNode {

	private int c2Width;
	private int c1Width;
	private ArrayList<TableRow> rows;

	private static final int ROW_HEIGHT = 20;
	
	private interface TableRow {
		void render(Graphics2D g, RenderPreferences prefs, int y);
	}
	
	public class TableName implements TableRow {
		private String name;

		public TableName(String name, Color textcolor) {
			this.name = name;
			rows.add(this);
		}
		
		public void render(Graphics2D g, RenderPreferences prefs, int y) {
			int mid = (c1Width + c2Width)/2;
			g.setColor(prefs.overlayInactiveTextColor());
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			g.drawString(name, mid - g.getFontMetrics().stringWidth(name)/2, 15+y);
			g.setColor(Color.white);
			g.drawLine(2, y+ROW_HEIGHT, c1Width+c2Width-2, y+ROW_HEIGHT);
		}
	}
	
	public class TableFixedProp implements TableRow {
		private String name;
		private String value;

		public TableFixedProp(String name, String value) {
			this.name = name;
			this.value = value;
			rows.add(this);
		}
		
		public void render(Graphics2D g, RenderPreferences prefs, int y) {
			g.setColor(prefs.overlayInactiveTextColor());
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			g.drawString(name,10, 15+y);
			g.drawString(value,10+c1Width, 15+y);
			g.setColor(Color.white);
			g.drawLine(2, y+ROW_HEIGHT, c1Width+c2Width-2, y+ROW_HEIGHT);
			g.drawLine(c1Width, y, c1Width, 20+y);
		}
	}

	public class TableSetProp implements TableRow {
		private String name;
		private IconEditField value;

		public TableSetProp(String name, TextInputControl value, OverlayManager om) {
			this.name = name;
			this.value = new IconEditField(10+c1Width, 10+ROW_HEIGHT*rows.size(), 0, om, value);
			addChild(this.value);
			rows.add(this);
		}
		
		public void render(Graphics2D g, RenderPreferences prefs, int y) {
			g.setColor(prefs.overlayInactiveTextColor());
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			g.drawString(name,10, 15+y);
			g.setColor(Color.white);
			g.drawLine(2, y+ROW_HEIGHT, c1Width+c2Width-2, y+ROW_HEIGHT);
			g.drawLine(c1Width, y, c1Width, 20+y);
		}
	}
	
	
	public PropertyTable(double x, double y, double theta,
			int c1Width, int c2Width) {
		super(x, y, theta);
		this.c1Width = c1Width;
		this.c2Width = c2Width;
		rows = new ArrayList<>();
	}
	
	public void draw(Graphics2D g, RenderPreferences prefs) {
		int y = 0;
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(2));
		for (TableRow row : rows) {
			row.render(g, prefs, y);
			y+=ROW_HEIGHT;
		}
		
		g.drawRoundRect(0, 0, c1Width+c2Width, ROW_HEIGHT*rows.size(), 8, 8);
	}
	
	public void addPosition(final BlueprintLocation bp, OverlayManager om) {
		new TableName("Position", Color.white);
		new TableSetProp("X", new FloatTextControl("","") {
			
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				return false;
			}
			
			protected void set(float f) {
				if (f <=1000)
					bp.x = f;
			}
			
			protected float get() {
				return bp.x;
			}
		}, om);
		new TableSetProp("Y", new FloatTextControl("","") {
			
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				return false;
			}
			
			protected void set(float f) {
				if (f <= 1000)
					bp.y = f;
			}
			
			protected float get() {
				return bp.y;
			}
		}, om);
		new TableSetProp("R", new FloatTextControl("","") {
			
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				return false;
			}
			
			protected void set(float f) {
				if (f <= 1000)
					bp.r = f;
			}
			
			protected float get() {
				return bp.r;
			}
		}, om);
		new TableSetProp("T1 (\u03c0)", new FloatTextControl("","") {
			
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				return false;
			}
			
			public void set(float f) {
				f = f % 2;
				if (f < 0) f+=2;
				bp.t1 = (float) (f*Math.PI);
			}

			public float get() {
				return (float) (bp.t1/Math.PI);
			}
		}, om);
		new TableSetProp("T2 (\u03c0)", new FloatTextControl("","") {
			
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				return false;
			}
			
			public void set(float f) {
				f = f % 2;
				if (f < 0) f+=2;
				bp.t2 = (float) (f*Math.PI);
			}

			public float get() {
				return (float) (bp.t2/Math.PI);
			}
		}, om);
	}
}
