package shipmaker.catalog;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import shipmaker.CatalogPartType;

public class PartCatalog {
	private static ArrayList<CatalogPartType> types;
	public static void registerType(CatalogPartType t) {
		if (types!=null) {
			types = new ArrayList<CatalogPartType>();
		}
		types.add(t);
	}
	
	public static List<CatalogPartType> allTypes() {
		return Collections.unmodifiableList(types);
	}

}
