package shipmaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import render.BlueprintPrefs;
import render.RenderPreferences;
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
		Space s = new Space();
		
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
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(Color.white);
				g.setFont(new Font("SansSerif", Font.BOLD, 14));
				g.drawString("X: "+String.format("%.04f", bpe.bpl.x), 30, 40);
				g.drawString("Y: "+String.format("%.04f", bpe.bpl.y), 30, 60);
				g.drawString("R: "+String.format("%.04f", bpe.bpl.r), 30, 80);
				g.drawString("T1: "+String.format("%.04f ",bpe.bpl.t1/Math.PI)+"\u03c0", 30, 100);
				g.drawString("T2: "+String.format("%.04f ",bpe.bpl.t2/Math.PI)+"\u03c0", 30, 120);
			}
		});
		overlay.addChild(new XYTRenderNode(15, 35, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 5, 5);
				PositionKnob.drawLeftright(g);
			}
		});
		overlay.addChild(new XYTRenderNode(15, 55, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 5, 5);
				PositionKnob.drawUpdown(g);
			}
		});
		overlay.addChild(new XYTRenderNode(15, 75, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 5, 5);
				RadiusKnob.drawRadius(g);
			}
		});
		overlay.addChild(new XYTRenderNode(15, 95, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 5, 5);
				T1Knob.drawT1(g);
			}
		});
		overlay.addChild(new XYTRenderNode(15, 115, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(Color.white);
				g.drawRoundRect(-10, -10, 20, 20, 5, 5);
				T2Knob.drawT2(g);
			}
		});
		svp.startGraphics();
	}
}
