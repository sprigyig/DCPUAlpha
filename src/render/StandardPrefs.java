package render;

import java.awt.Color;
import java.awt.Dimension;

public class StandardPrefs implements RenderPreferences {

	private Dimension window;

	public int borderThickness() {
		return 3;
	}

	public Color borderColor() {
		return new Color(44,20,20);
	}

	public Color body1() {
		return new Color(240, 240, 255);
	}

	public Color body2() {
		return new Color(180, 180, 200);
	}

	public Color body3() {
		return new Color(100, 100, 120);
	}

	public Color highlight1() {
		return new Color(90, 90, 200);
	}

	public Color spaceColor() {
		return new Color(35, 35, 60);
	}

	public Color overlayTextColor() {
		return Color.yellow.darker();
	}

	public Dimension window() {
		return window;
	}

	public void setWindow(Dimension d) {
		window = d;
	}

}
