package equipment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import regret.GlobalHacks;
import render.XYTRenderNode;
import ships.Equipment;
import ships.Ship;

public class Structure implements Equipment {
	private ArrayList<Point> elements;
	
	public Structure() {
		elements = new ArrayList<Point>();
	}
	
	public void addLocation(int x, int y) {
		elements.add(new Point(x,y));
	}
	
	public void addedTo(Ship s) {
		s.addRenderNode(new XYTRenderNode(0,0,0){
			public void draw(Graphics2D g) {
				int scale = 25;
				int[] x = new int[]{-1,-1,1,1,-1,1,-1,1};
				int[] y = new int[]{-1,1,1,-1,1,1,-1,-1};
				
				for (int i=0; i<x.length; i++) {
					x[i]*=scale; y[i]*=scale;
				}
				
				g.setColor(GlobalHacks.getBorderColor());
				g.setStroke(new BasicStroke(5+GlobalHacks.borderThickness()*2, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				for (Point p : elements) {
					for (int i=0;i<8;i++) { x[i]+=p.x*scale*2;y[i]+=p.y*scale*2; }
					g.drawPolyline(x,y, 8);
					for (int i=0;i<8;i++) { x[i]-=p.x*scale*2;y[i]-=p.y*scale*2; }
				}
				
				g.setColor(new Color(100,100,110));
				g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				for (Point p : elements) {
					for (int i=0;i<8;i++) { x[i]+=p.x*scale*2;y[i]+=p.y*scale*2; }
					g.drawPolyline(x,y, 8);
					for (int i=0;i<8;i++) { x[i]-=p.x*scale*2;y[i]-=p.y*scale*2; }
				}
				
				g.setColor(new Color(150,150,110));
				g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_MITER));
				for (Point p : elements) {
					int offset = 4;
					g.drawLine(offset-scale+p.x*scale*2, offset-scale+p.y*scale*2, scale-offset+p.x*scale*2, scale-offset+p.y*scale*2);
					g.drawLine(scale-offset+p.x*scale*2, offset-scale+p.y*scale*2, offset-scale+p.x*scale*2, scale-offset+p.y*scale*2);
				}
				
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
		// TODO Auto-generated method stub
		
	}
	
}
