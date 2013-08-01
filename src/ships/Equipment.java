package ships;

public interface Equipment {
	public void addedTo(Ship s);
	public void reset();
	public void physicsTickPreForce();
	public void physicsTickPostForce();
	public void triggerSynchronizedEvent(char id, int cyclesAgo);
}
