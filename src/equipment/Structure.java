package equipment;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import render.RenderPreferences;
import render.XYTRenderNode;
import ships.Equipment;
import ships.Ship;

public class Structure implements Equipment {
	private ArrayList<StructureNode> elements;
	
	public static final int STRUCT_SIZE = 50;
	
	public Structure() {
		elements = new ArrayList<StructureNode>();
	}
	
	public void addLocation(int x, int y) {
		elements.add(new StructureNode(x,y));
	}
	
	public static void draw(Graphics2D g, RenderPreferences prefs, Collection<StructureNode> elements) {
		int scale = STRUCT_SIZE/2;
		int[] x = new int[]{-1,-1,1,1,-1,1,-1,1};
		int[] y = new int[]{-1,1,1,-1,1,1,-1,-1};
		
		for (int i=0; i<x.length; i++) {
			x[i]*=scale; y[i]*=scale;
		}
		
		g.setColor(prefs.borderColor());
		int t = prefs.borderThickness();
		g.setStroke(new BasicStroke(5+t*2, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		for (StructureNode p : elements) {
			for (int i=0;i<8;i++) { x[i]+=p.x*scale*2;y[i]+=p.y*scale*2; }
			g.drawPolyline(x,y, 8);
			for (int i=0;i<8;i++) { x[i]-=p.x*scale*2;y[i]-=p.y*scale*2; }
		}
		
		g.setColor(prefs.body3());
		g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		for (StructureNode p : elements) {
			for (int i=0;i<8;i++) { x[i]+=p.x*scale*2;y[i]+=p.y*scale*2; }
			g.drawPolyline(x,y, 8);
			for (int i=0;i<8;i++) { x[i]-=p.x*scale*2;y[i]-=p.y*scale*2; }
		}
		
		g.setColor(prefs.body2());
		g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_MITER));
		for (StructureNode p : elements) {
			int offset = 4;
			g.drawLine(offset-scale+p.x*scale*2, offset-scale+p.y*scale*2, scale-offset+p.x*scale*2, scale-offset+p.y*scale*2);
			g.drawLine(scale-offset+p.x*scale*2, offset-scale+p.y*scale*2, offset-scale+p.x*scale*2, scale-offset+p.y*scale*2);
		}
		
	}
	
	public void addedTo(Ship s) {
		s.addBgRenderNode(new XYTRenderNode(0,0,0){
			public void draw(Graphics2D g, RenderPreferences prefs) {
				Structure.draw(g, prefs, elements);
			}
		});
	}

	public void reset() {
		
	}

	public void physicsTickPreForce() {
		
	}

	public void physicsTickPostForce() {
		
	}

	public void triggerSynchronizedEvent(char id, int cyclesAgo) {
		
	}
	
}
