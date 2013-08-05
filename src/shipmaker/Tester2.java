package shipmaker;

import javax.swing.JFrame;

import render.BlueprintPrefs;
import render.SpaceViewPanel;
import ships.Ship;
import env.Space;
import equipment.Capacitor;
import equipment.PowerGrid;

public class Tester2 {
	public static void main(String[] args) {
		Space s = new Space();
		
		Ship ship = new Ship(1, 1);
		ship.addEquipment(new Capacitor(0, 0, 0));
		//s.addEntity(ship);
		s.addEntity(new BlueprintPositionEditor());
		ship.addEquipment(new PowerGrid(1, 1, 1, 'x'));
		SpaceViewPanel svp = new SpaceViewPanel(s);
		svp.prefs = new BlueprintPrefs();
		JFrame jf = new JFrame();
		jf.add(svp);
		jf.setSize(400, 400);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.start();
		svp.startGraphics();
	}
}
