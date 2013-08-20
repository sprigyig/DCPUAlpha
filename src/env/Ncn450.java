package env;

public class Ncn450 {
	public char upper, lower;
	
	public Ncn450(float theta) {
		theta = (float) (theta*2/Math.PI);
		
		int rot = (int)((double)theta * 0x10000);
		upper = (char)(rot>>16);
		lower = (char)rot;
	}
	
	public Ncn450(char upper, char lower) {
		this.upper = upper;
		this.lower = lower;
	}
	
	public float angle() {
		return (float) ((double)(((int)upper << 16) | lower)/0x10000 * Math.PI/2);
	}
}