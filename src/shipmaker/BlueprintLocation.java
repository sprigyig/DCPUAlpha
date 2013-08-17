package shipmaker;

import java.awt.geom.Point2D;

import com.google.gson.annotations.Expose;

public class BlueprintLocation {
	@Expose public float x, y, t1, t2, r;

	public float effectiveX() {
		return (float) (x + Math.cos(t1) * r);
	}
	
	public float effectiveY() {
		return (float) (y + Math.sin(t1) * r);
	}
	
	public float distance(BlueprintLocation location) {
		return (float) new Point2D.Float(effectiveX(), effectiveY()).distance(location.effectiveX(), location.effectiveY());
	}
	
	public BlueprintLocation convertToXYT(float centerX, float centerY) {
		float nx = effectiveX()-centerX;
		float ny = effectiveY()-centerY;
		float t = t1 + t2;
		
		BlueprintLocation ret = new BlueprintLocation();
		
		ret.x = nx;
		ret.y = ny;
		ret.t2 = t;
		
		ret.r = 0;
		ret.t1 = 0;
		return ret;
	}
	
	public BlueprintLocation convertToRTT(float centerX, float centerY) {
		float nx = effectiveX() - centerX;
		float ny = effectiveY() - centerY;
		
		float nr = (float) Math.sqrt(nx * nx + ny * ny);
		float nt1 = (float) Math.atan2(ny, nx);
		float nt2 = t1 + t2 - nt1;
		
		BlueprintLocation ret = new BlueprintLocation();
		
		ret.x = 0;
		ret.y = 0;
		
		ret.t1 = nt1;
		
		if (nt2 < 0f) {
			nt2 += 2 * Math.PI;
		} else if (nt2 > 2*Math.PI) {
			nt2 -= 2* Math.PI;
		}
		
		ret.t2 = nt2;
		ret.r = nr;
		
		return ret;
	}
	
}
