package shipmaker.partplacer;

import java.awt.Graphics2D;

import render.OverlayManager;
import render.RenderPreferences;
import render.XYTRenderNode;
import shipmaker.knobs.PositionKnob;
import shipmaker.knobs.RadiusKnob;
import shipmaker.knobs.T1Knob;
import shipmaker.knobs.T2Knob;
import shipmaker.render.IconEditField;

public class BlueprintPositionOverlay extends XYTRenderNode {
	public BlueprintPositionOverlay(final BlueprintPositionEditor bpe, OverlayManager om) {
		super(0,0,0);
		addChild(new IconEditField(35, 35, 0, om, new FloatTextControl(" X:", "") {
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				PositionKnob.drawLeftright(g);
				return true;
			}
			
			protected void set(float f) {
				bpe.bpl.x = f;
			}
			
			protected float get() {
				return bpe.bpl.x;
			}
		}));
		
		addChild(new IconEditField(35, 55, 0, om, new FloatTextControl(" Y:", "") {
			public void set(float f) {
				bpe.bpl.y = f;
			}

			public float get() {
				return bpe.bpl.y;
			}

			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				PositionKnob.drawUpdown(g);
				return true;
			}
		}));
		
		addChild(new IconEditField(35, 75, 0, om, new FloatTextControl(" R:", "") {
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				RadiusKnob.drawRadius(g);
				return true;
			}
			
			public void set(float f) {
				bpe.bpl.r = f;
			}

			public float get() {
				return bpe.bpl.r;
			}
		}));
		
		addChild(new IconEditField(35, 95, 0, om, new FloatTextControl("T1:", "\u03c0") {
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				T1Knob.drawT1(g);
				return true;
			}
			
			public void set(float f) {
				f = f % 2;
				if (f < 0) f+=2;
				bpe.bpl.t1 = (float) (f*Math.PI);
			}

			public float get() {
				return (float) (bpe.bpl.t1/Math.PI);
			}
		}));
		
		addChild(new IconEditField(35, 115, 0, om, new FloatTextControl("T2:", "\u03c0") {
			public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
				T2Knob.drawT2(g);
				return true;
			}
			public void set(float f) {
				f = f % 2;
				if (f < 0) f+=2;
				bpe.bpl.t2 = (float) (f*Math.PI);
			}

			public float get() {
				return (float) (bpe.bpl.t2/Math.PI);
			}
		}));
	}
}
