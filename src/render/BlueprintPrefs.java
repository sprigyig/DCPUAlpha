package render;

import java.awt.Color;
import java.awt.Dimension;

public class BlueprintPrefs implements RenderPreferences {

	private Color bg;
	private Color trans;
	private Color fg;
	private Dimension window;

	public BlueprintPrefs() {
		this.bg = new Color(100,100,180);
		this.trans = new Color(0,0,0,0);
		this.fg = new Color(130, 110, 240);
	}
	
	public int borderThickness() {
		return 1;
	}

	public Color borderColor() {
		return fg;
	}

	public Color body1() {
		return trans;
	}

	public Color body2() {
		return trans;
	}

	public Color body3() {
		return bg;
	}

	public Color highlight1() {
		return fg;
	}

	public Color spaceColor() {
		return bg;
	}

	public Color overlayTextColor() {
		return fg;
	}

	public Dimension window() {
		return window;
	}

	public void setWindow(Dimension d) {
		window = d;
	}

	public Color overlayInactiveTextColor() {
		int g = 200;
		return new Color(g, g, g+10);
	}
}
