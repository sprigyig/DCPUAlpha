package demo.equipment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import physics.XYTSource;

import render.XYTRenderNode;
import ships.Equipment;
import ships.Ship;

public class DemoStructure implements Equipment, XYTSource {
	private float t;
	private float y;
	private float x;
	public DemoStructure(float x, float y, float t) {
		this.x = x;
		this.y = y;
		this.t = t;
	}
	public void addedTo(Ship s) {
		s.addRenderNode(new XYTRenderNode(this) {
			public void draw(Graphics2D g) {
				int scale = 25;
				int[] x = new int[]{-1,-1,1,1,-1,1,-1,1};
				int[] y = new int[]{-1,1,1,-1,1,1,-1,-1};
				
				for (int i=0; i<x.length; i++) {
					x[i]*=scale; y[i]*=scale;
				}
				g.setColor(new Color(160,160,166));
				g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				g.drawPolyline(x,y, 8);
				g.setColor(new Color(220,220,220));
				
				g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_MITER));
				int offset = 4;
				g.drawLine(offset-scale, offset-scale, scale-offset, scale-offset);
				g.drawLine(scale-offset, offset-scale, offset-scale, scale-offset);
			}
		});
	}
	public float position_x() {
		return x;
	}
	public float position_y() {
		return y;
	}
	public float alignment_theta() {
		return t;
	}
	public void reset() {
	}
	public void physicsTick() {
	}
}
