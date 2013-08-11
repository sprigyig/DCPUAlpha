package shipmaker;

import java.awt.geom.Point2D;

public class BlueprintLocation {
	public float x, y, t1, t2, r;

	public float effectiveX() {
		return (float) (x + Math.cos(t1) * r);
	}
	
	public float effectiveY() {
		return (float) (y + Math.sin(t1) * r);
	}
	
	public float distance(BlueprintLocation location) {
		return (float) new Point2D.Float(effectiveX(), effectiveY()).distance(location.effectiveX(), location.effectiveY());
	}
}
