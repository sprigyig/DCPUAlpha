package shipmaker;

import java.awt.Graphics2D;

import javax.swing.JFrame;

import render.BlueprintPrefs;
import render.SpaceViewPanel;
import render.XYTRenderNode;
import shipmaker.knobs.PositionKnob;
import shipmaker.knobs.RadiusKnob;
import shipmaker.knobs.T1Knob;
import shipmaker.knobs.T2Knob;
import ships.Ship;
import env.Space;
import equipment.Engine;
import equipment.PowerGrid;

public class Tester2 {
	
	public static void main(String[] args) {
		final Space s = new Space();
		
		final Ship ship = new Ship(1, 1);
		ship.addEquipment(new Engine(0, 0, 0, 1, 1, 'x'));
		s.addEntity(ship);
		final BlueprintPositionEditor bpe;
		s.addEntity(bpe = new BlueprintPositionEditor(ship.me));
		ship.addEquipment(new PowerGrid(1, 1, 1, 'x'));
		SpaceViewPanel svp = new SpaceViewPanel(s);
		svp.prefs = new BlueprintPrefs();
		JFrame jf = new JFrame();
		jf.add(svp);
		jf.setSize(400, 400);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.start();
		XYTRenderNode overlay;
		svp.addOverlay(overlay = new XYTRenderNode(0, 0, 0) {
		});
		overlay.addChild(new PositionTextField(15, 35, 0, s, "X: ", "") {
			public void set(float f) {
				bpe.bpl.x = f;
			}

			public float get() {
				return bpe.bpl.x;
			}

			public void drawIcon(Graphics2D g) {
				PositionKnob.drawLeftright(g);
			}
			
		});
		overlay.addChild(new PositionTextField(15, 55, 0, s, "Y: ", "") {
			public void set(float f) {
				bpe.bpl.y = f;
			}

			public float get() {
				return bpe.bpl.y;
			}

			public void drawIcon(Graphics2D g) {
				PositionKnob.drawUpdown(g);
			}
			
		});
		overlay.addChild(new PositionTextField(15, 75, 0, s, "R: ", "") {
			public void set(float f) {
				bpe.bpl.r = f;
			}

			public float get() {
				return bpe.bpl.r;
			}

			public void drawIcon(Graphics2D g) {
				RadiusKnob.drawRadius(g);
			}
			
		});
		overlay.addChild(new PositionTextField(15, 95, 0, s, "T1: ", " \u03c0") {
			public void set(float f) {
				f = f % 2;
				if (f < 0) f+=2;
				bpe.bpl.t1 = (float) (f*Math.PI);
			}

			public float get() {
				return (float) (bpe.bpl.t1/Math.PI);
			}

			public void drawIcon(Graphics2D g) {
				T1Knob.drawT1(g);
			}
			
		});
		overlay.addChild(new PositionTextField(15, 115, 0, s, "T2: ", " \u03c0") {
			public void set(float f) {
				f = f % 2;
				if (f < 0) f+=2;
				bpe.bpl.t2 = (float) (f*Math.PI);
			}

			public float get() {
				return (float) (bpe.bpl.t2/Math.PI);
			}

			public void drawIcon(Graphics2D g) {
				T2Knob.drawT2(g);
			}
			
		});
		svp.startGraphics();
	}
}
