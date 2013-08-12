package shipmaker.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import render.MouseEventType;
import render.OverlayManager;
import render.RenderNode;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.BlueprintLocation;
import shipmaker.partplacer.FloatTextControl;
import shipmaker.partplacer.TextInputControl;

public class PropertyTable extends XYTRenderNode {

	private int c2Width;
	private int c1Width;
	private ArrayList<TableRow> rows;
	private OverlayManager om;

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
			int c1Width, int c2Width, OverlayManager om) {
		super(x, y, theta);
		this.c1Width = c1Width;
		this.c2Width = c2Width;
		rows = new ArrayList<>();
		this.om = om;
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
	public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
		if (t!=MouseEventType.MOUSE_PRESS) return false;
		
		Point2D.Float pt = RenderNode.reverse(root, e);
		System.out.println(pt);
		if (pt.x > 0 && pt.x < c1Width + c2Width && pt.y > 0 && pt.y < rows.size() * ROW_HEIGHT) {
			System.out.println("inhibitor");
			om.lowPriorityInteraction = new Runnable() {
				public void run() {
					//inhibit de-selection by registering this
				}
			};
		}
		return false;
	}
}
