package shipmaker;

import java.awt.Graphics2D;

import render.RenderPreferences;

public interface CatalogPartType {
	public CatalogPart create();
	public String name();
	public float mass();
	public float rotationalInertia();
	public void preview(Graphics2D g, RenderPreferences prefs);
}
