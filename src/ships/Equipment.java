package ships;

public interface Equipment {
	public void addedTo(Ship s);
	public void reset();
	public void physicsTick();
}
