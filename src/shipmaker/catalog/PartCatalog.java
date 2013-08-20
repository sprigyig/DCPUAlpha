package shipmaker.catalog;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import shipmaker.CatalogPartType;

public class PartCatalog {
	private static ArrayList<CatalogPartType> types = new ArrayList<CatalogPartType>();
	private static boolean ready = false;
	public static void registerType(CatalogPartType t) {
		types.add(t);
	}
	
	public static List<CatalogPartType> allTypes() {
		return Collections.unmodifiableList(types);
	}

	static {
		synchronized(types) {
			types.add(new BasicCapacitor());
			types.add(new FHBL_1_08());
			types.add(new PowerGrid());
			types.add(new StandardEngine());
			types.add(new StandardGenerator());
			types.add(new Synchronizer());
			types.add(new PositionSensor());
			ready = true;
		} 
	}

	private static void ready() {
		synchronized(types) {
			while (!ready)
				try {
					types.wait();
				} catch (InterruptedException e) {}
		}
	}
	
	public static CatalogPartType getTypeByName(String name) {
		ready();
		for (CatalogPartType t : types) {
			if (t.name().equals(name)) {
				return t;
			}
		}
		return null;
	}
	
}
