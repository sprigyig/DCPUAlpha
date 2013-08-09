package shipmaker;

import physics.Body;
import render.OverlayManager;
import render.RenderNode;
import ships.Equipment;
import shipmaker.BlueprintLocation;

public interface CatalogPart {
	public RenderNode getRenderRagdoll(Body base);
	public Equipment generateEquipment(float effectiveX, float effectiveY, float effectiveTheta);
	public RenderNode getOptionsOverlay(OverlayManager om, BlueprintLocation bpl);
	public CatalogPartType type();
}
