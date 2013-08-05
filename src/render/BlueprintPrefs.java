package render;

import java.awt.Color;

public class BlueprintPrefs implements RenderPreferences {

	private Color bg;
	private Color trans;

	public BlueprintPrefs() {
		this.bg = new Color(70, 50, 170);
		this.trans = new Color(0,0,0,0);
	}
	
	public int borderThickness() {
		return 1;
	}

	public Color borderColor() {
		return Color.gray;
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
		return Color.gray;
	}

	public Color spaceColor() {
		return bg;
	}

	public Color overlayTextColor() {
		return Color.white;
	}

}
