package shipmaker;

import javax.swing.JFrame;

import render.BlueprintPrefs;
import render.SpaceViewPanel;
import shipmaker.render.CatalogSelector;
import shipmaker.render.ShipContents;
import env.Space;

public class Editor {
	
	public static void main(String[] args) {		
		EditorShip es = new EditorShip();
		
		final Space s = new Space();
		
		SpaceViewPanel svp = new SpaceViewPanel(s);

		
		svp.overlays().bottomLeft().addChild(new CatalogSelector(svp.overlays(), es));
		svp.overlays().topRight().addChild(new ShipContents(es, s, svp.overlays()));
		
		svp.prefs = new BlueprintPrefs();
		JFrame jf = new JFrame();
		jf.add(svp);
		jf.setSize(600, 600);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.start();
		
	}
}
