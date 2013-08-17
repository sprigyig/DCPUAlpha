package shipmaker;

import physics.Body;
import render.OverlayManager;
import render.RenderNode;
import ships.Ship;

public interface CatalogPart {
	public RenderNode getRenderRagdoll(Body base);
	public void applyToShip(BlueprintLocation location, Ship s, float centerMassX, float centerMassY);
	public RenderNode getOptionsOverlay(OverlayManager om, BlueprintLocation bpl);
	public CatalogPartType type();
	
}
