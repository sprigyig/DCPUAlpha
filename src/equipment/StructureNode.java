package equipment;

import com.google.gson.annotations.Expose;

public class StructureNode {
	@Expose public int x, y;
	
	public StructureNode() {
		x = y = 0;
	}
	
	public StructureNode(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object o) {
		if (o instanceof StructureNode) {
			StructureNode on = (StructureNode) o;
			return on.x == x && on.y == y;
		}
		return false;
	}
	
	public int hashCode() {
		return ((x + y) * 7) ^ ((x - y) * 11);
	}
}