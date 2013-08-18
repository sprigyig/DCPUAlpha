package shipmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import render.BlueprintPrefs;
import render.RenderNode;
import render.RenderPreferences;
import render.SpaceViewPanel;
import render.XYTRenderNode;
import shipmaker.catalog.PowerGrid;
import shipmaker.render.Button;
import shipmaker.render.CatalogSelector;
import shipmaker.render.ShipContents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import env.Entity;
import env.Space;
import equipment.StructureNode;

public class Editor {
	
	Space s;
	EditorShip es;
	SpaceViewPanel svp;
	RenderNode selector, contents;
	
	public Editor(EditorShip pes) {
		this.es = pes;
		s = new Space();
		svp = new SpaceViewPanel(s);
		
		this.setShip(pes);
		
		svp.prefs = new BlueprintPrefs();
		
		s.start();
		addSaveLoadUI();
	}
	
	private void addSaveLoadUI() {
		svp.overlays().topCenter().addChild(new Button(-10, 20, "Test Ship", new Runnable() {
			public void run() {
				demo.Main.main(es.makeShip());
			}
		}));
		svp.overlays().topCenter().addChild(new Button(-100, 20, "Save Ship", new Runnable() {
			public void run() {
				GsonBuilder gb = new GsonBuilder();
				gb.excludeFieldsWithoutExposeAnnotation();
				
				Gson g = gb.create();
				String content = g.toJson(es);
				JFileChooser jfc = new JFileChooser(new File("."));
				
				jfc.showSaveDialog(null);
				if (jfc.getSelectedFile()!=null) {
					try {
						PrintWriter pw = new PrintWriter(jfc.getSelectedFile());
						pw.write(content);
						pw.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}));
		svp.overlays().topCenter().addChild(new Button(-100, 54, "Load Ship", new Runnable() {
			public void run() {
				JFileChooser jfc = new JFileChooser(new File("."));
				
				jfc.showOpenDialog(null);
				if (jfc.getSelectedFile()!=null) {
					try {
						String json = "";
						Reader r = new InputStreamReader(new FileInputStream(jfc.getSelectedFile()));
						char[] buffer = new char[1000];
						int read = 0;
						while ((read = r.read(buffer)) > 0) {
							json += String.valueOf(buffer, 0, read);
						}
						setShip(EditorShip.fromJson(json));
						r.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}));
		svp.overlays().topCenter().addChild(new Button(-10, 54, "Structure Mode", new Runnable() {
			public void run() {
				es.editingStructure = !es.editingStructure;
			}
		}));
	}
	
	public SpaceViewPanel panel() {
		return svp;
	}
	
	public void setShip(EditorShip pes) {
		s.clearEntities();
		svp.overlays().clear();
		this.es = pes;
		selector = new CatalogSelector(svp.overlays(), pes);
		contents = new ShipContents(pes, s, svp.overlays());
		svp.overlays().topRight().addChild(contents);
		svp.overlays().bottomLeft().addChild(selector);
		svp.overlays().bottomRight().addChild(new XYTRenderNode(-200, -50, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setFont(new Font("SansSerif", Font.BOLD, 14));
				g.drawString("Mass:"+es.massTotal(), 10, 20);
				g.drawString("Rot Inertia:"+es.riTotal(), 10, 40);
			}
		});
		
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
		addSaveLoadUI();
	}
	
	public static void main(String[] args) {		
		final EditorShip es = new EditorShip();
		es.addPart(new PowerGrid());
		es.structLocations().add(new StructureNode());
		
		Editor e = new Editor(es);
		
		JFrame jf = new JFrame();
		jf.add(e.panel());
		jf.setSize(800, 600);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
}
