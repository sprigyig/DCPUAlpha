package shipmaker;

import env.Space;
import physics.Body;
import render.RenderNode;
import ships.Equipment;

public interface CatalogPart {
	public RenderNode getRenderRagdoll(Body base);
	public Equipment generateEquipment(float effectiveX, float effectiveY, float effectiveTheta);
	public RenderNode getOptionsOverlay(Space s);
	public CatalogPartType type();
}
