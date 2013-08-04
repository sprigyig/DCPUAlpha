package render;

import java.awt.Color;

public class StandardPrefs implements RenderPreferences {

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
		return new Color(150, 150, 160);
	}

	public Color body3() {
		return new Color(80, 80, 90);
	}

	public Color highlight1() {
		return new Color(90, 90, 200);
	}

	public Color spaceColor() {
		return new Color(65, 65, 90);
	}

}
