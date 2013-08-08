package render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import shipmaker.partplacer.IconEditField;
import shipmaker.partplacer.TextInputControl;

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

		public TableName(String name) {
			this.name = name;
			rows.add(this);
		}
		
		public void render(Graphics2D g, RenderPreferences prefs, int y) {
			int mid = (c1Width + c2Width)/2;
			g.setColor(Color.gray.brighter());
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			g.drawString(name, mid - g.getFontMetrics().stringWidth(name)/2, 15+y);
			g.setColor(Color.white);
			g.drawLine(0, y+ROW_HEIGHT, c1Width+c2Width, y+ROW_HEIGHT);
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
			g.setColor(Color.gray.brighter());
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			g.drawString(name,10, 15+y);
			g.drawString(value,10+c1Width, 15+y);
			g.setColor(Color.white);
			g.drawLine(0, y+ROW_HEIGHT, c1Width+c2Width, y+ROW_HEIGHT);
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
			g.setColor(Color.gray.brighter());
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			g.drawString(name,10, 15+y);
			g.setColor(Color.white);
			g.drawLine(0, y+ROW_HEIGHT, c1Width+c2Width, y+ROW_HEIGHT);
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
		for (TableRow row : rows) {
			row.render(g, prefs, y);
			y+=ROW_HEIGHT;
		}
		g.setColor(Color.white);
		g.drawLine(0, ROW_HEIGHT*rows.size(), 0, 0);
		g.drawLine(c1Width+c2Width, ROW_HEIGHT*rows.size(), c1Width+c2Width, 0);
	}
	
}
