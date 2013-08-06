package render;

import java.awt.Color;

public class BlueprintPrefs implements RenderPreferences {

	private Color bg;
	private Color trans;
	private Color fg;

	public BlueprintPrefs() {
		this.bg = new Color(70, 50, 170);
		this.trans = new Color(0,0,0,0);
		this.fg = new Color(90, 70, 190);
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

}
