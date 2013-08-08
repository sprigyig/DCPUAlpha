package shipmaker;

import physics.Body;
import render.OverlayManager;
import render.RenderNode;
import ships.Equipment;

public interface CatalogPart {
	public RenderNode getRenderRagdoll(Body base);
	public Equipment generateEquipment(float effectiveX, float effectiveY, float effectiveTheta);
	public RenderNode getOptionsOverlay(OverlayManager om);
	public CatalogPartType type();
}
