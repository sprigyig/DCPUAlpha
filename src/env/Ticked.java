package env;

import dcpu.WorldPauseHandler;

public interface Ticked {
	public void tickInternals(int msPerTick, WorldPauseHandler handler);
	public void tickPhysics(int msPerTick, WorldPauseHandler handler);
}
