package shipmaker;

import java.awt.Graphics2D;

import render.RenderPreferences;

public interface CatalogPartType {
	public CatalogPart create(BlueprintLocation pbpl);
	public String name();
	public float mass();
	public float rotationalInertia();
	public boolean placeable();
	public void preview(Graphics2D g, RenderPreferences prefs);
	public boolean deletable();
}
