package render;

import java.awt.Color;

public interface RenderPreferences {
	public int borderThickness();
	public Color borderColor();
	
	public Color body1();
	public Color body2();
	public Color body3();
	
	public Color highlight1();
	
	public Color spaceColor();
}
