package shipmaker;

import javax.swing.JFrame;

import physics.Body;
import render.BlueprintPrefs;
import render.RenderNode;
import render.SpaceViewPanel;
import shipmaker.catalog.StandardEngine;
import shipmaker.partplacer.BlueprintPositionEditor;
import env.Asteroid;
import env.Entity;
import env.Space;

public class Editor {
	
	public static void main(String[] args) {
		CatalogPartType type = new StandardEngine();
		
		CatalogPart part = type.create();
		Body partRagdoll = new Body(0, 0, 0, 1, 1);
		
		
		
		final Space s = new Space();
		
		
		final BlueprintPositionEditor bpe;
		s.addEntity(bpe = new BlueprintPositionEditor(partRagdoll));
		
		final RenderNode visuals = part.getRenderRagdoll(partRagdoll);
		
		SpaceViewPanel svp = new SpaceViewPanel(s);
		s.addEntity(new Entity() {
			public void tickPhysics(int msPerTick) {
				
			}
			
			public void tickInternals(int msPerTick) {
				
			}
			
			public RenderNode getVisuals() {
				return visuals;
			}
		});
		
		svp.overlays().addLeft(part.getOptionsOverlay(svp.overlays(), bpe.bpl()));
		svp.prefs = new BlueprintPrefs();
		JFrame jf = new JFrame();
		jf.add(svp);
		jf.setSize(600, 600);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.start();
		
		svp.startGraphics();
	}
}
