package env;

public interface Ticked {
	public void tickInternals(int msPerTick);
	public void tickPhysics(int msPerTick);
}
