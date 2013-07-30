package physics;

public class FixPoint {
	
	final long value;
	
	public FixPoint(long val) {
		value = val;
	}
	
	public FixPoint(long whole, long frac) {
		value = (whole << 16) + frac; 
	}
	
	public long whole() {
		return value >> 16;
	}
	
	public long frac() {
		return value & 0xFFFF;
	}
	
	public FixPoint add(FixPoint f) {
		return new FixPoint(value+f.value);
	}
	
	public FixPoint sub(FixPoint f) {
		return new FixPoint(value-f.value);
	}
	
	public FixPoint mul(FixPoint f) {
		//TODO: better way of avoiding overflow?
		return new FixPoint(
			((whole() * f.whole())<<16) +
			((frac() * f.frac()>>16)) +
			((frac() * f.whole())) +
			((whole() * f.frac()))
		);
	}
	
	public FixPoint div(FixPoint f) {
		long w, p;
		
		w = value/f.value;
		p = value - (w*f.value);
		p >>=48;
		System.out.println(w+" "+value+" "+f.value);
		return new FixPoint(w, p);
	}
	
	public String toString() {
		return String.format("%x.%04x", whole(), frac());
	}
	
	public static void main(String[] args) {
		FixPoint one = new FixPoint(1,0);
		FixPoint three = new FixPoint(3,0);
		FixPoint half = new FixPoint(0, 0x8000);
		System.out.println(one.mul(half));
		System.out.println(half.mul(half));
		System.out.println(three.mul(half));
		System.out.println(three.mul(half).mul(three));
		System.out.println(new FixPoint(1<<50).div(three));
	}
}
