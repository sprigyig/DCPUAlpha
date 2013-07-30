package dcpu;

public interface Hardware {
	public void plugged_in(Dcpu parent, char id);
	public void query(Dcpu parent);
	public void interrupted(Dcpu parent);
}
