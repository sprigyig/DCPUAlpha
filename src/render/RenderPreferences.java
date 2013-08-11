package render;

import java.awt.Color;
import java.awt.Dimension;

public interface RenderPreferences {
	public int borderThickness();
	public Color borderColor();
	
	public Color body1();
	public Color body2();
	public Color body3();
	
	public Color highlight1();
	
	public Color spaceColor();
	
	public Color overlayTextColor();
	public Color overlayInactiveTextColor();
	
	public Dimension window();
	public void setWindow(Dimension d);
}
