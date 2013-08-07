package shipmaker;

public interface CatalogPartType {
	public CatalogPart create();
	public String name();
	public float mass();
	public float rotationalInertia();
}
