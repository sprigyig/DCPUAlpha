package shipmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import render.BlueprintPrefs;
import render.MouseEventType;
import render.RenderNode;
import render.RenderPreferences;
import render.SpaceViewPanel;
import render.XYTRenderNode;
import shipmaker.EditorShip.EditorShipPart;
import shipmaker.catalog.BasicCapacitor;
import shipmaker.catalog.PositionSensor;
import shipmaker.catalog.StandardEngine;
import shipmaker.catalog.StandardGenerator;
import shipmaker.catalog.Synchronizer;
import shipmaker.render.CatalogSelector;
import shipmaker.render.ShipContents;
import env.Entity;
import env.Space;

public class Editor {
	
	public static void main(String[] args) {		
		final EditorShip es = new EditorShip();
		
		
		EditorShipPart sp;
		sp = es.addPart(new StandardEngine());
		sp.location.r = 100;
		sp.location.t1 = (float) (Math.PI/8);
		sp.location.t2 = (float) (Math.PI/2);
		
		sp = es.addPart(new StandardEngine());
		sp.location.r = 100;
		sp.location.t1 = (float) -(Math.PI/8);
		sp.location.t2 = (float) -(Math.PI/2);
		
		sp = es.addPart(new StandardEngine());
		sp.location.r = 100;
		sp.location.t1 = (float) (9*Math.PI/8);
		sp.location.t2 = (float) (Math.PI/2);
		
		sp = es.addPart(new StandardEngine());
		sp.location.r = 100;
		sp.location.t1 = (float) (7*Math.PI/8);
		sp.location.t2 = (float) -(Math.PI/2);
		
		sp = es.addPart(new StandardGenerator());
		sp = es.addPart(new BasicCapacitor());
		sp.location.x = 50;
		sp = es.addPart(new BasicCapacitor());
		sp.location.x = -50;
		
		sp = es.addPart(new Synchronizer());
		sp = es.addPart(new PositionSensor());
		
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
		s.addEntity(new Entity() {
			private RenderNode vis;
			public void tickPhysics(int msPerTick) {
			}
			
			public void tickInternals(int msPerTick) {
			}
			
			public RenderNode getVisuals() {
				if (vis == null) {
					vis = new XYTRenderNode(0,0,0) {
						public void draw(Graphics2D g, RenderPreferences prefs) {
							g.setColor(new Color(255,255,255, 40));
							g.setStroke(new BasicStroke(1));
							g.drawLine(-40, 0, 40, 0);
							g.drawLine(0, -40, 0, 40);
							g.drawArc(-20, -20, 40, 40, 0, 360);
						}
					};
				}
				return vis;
			}
		});
		
		svp.overlays().topRight().addChild(new XYTRenderNode(-250, 0, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(Color.white);
				g.setFont(new Font("SansSerif", Font.BOLD, 12));
				g.drawString("Test", 0, 20);
			}

			public boolean interacted(AffineTransform root, MouseEvent e, MouseEventType t) {
				if (t!=MouseEventType.MOUSE_PRESS) return false;
				Point2D.Float pt = RenderNode.reverse(root, e);
				
				if (pt.x > 0 && pt.x < 40 && pt.y > 0 && pt.y < 40) {
					demo.Main.main(es.makeShip());
				}
				return false;
			}
		});
		
		
	}
}
