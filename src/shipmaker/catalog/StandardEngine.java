package shipmaker.catalog;

import java.awt.Color;
import java.awt.Graphics2D;

import env.Space;
import equipment.Engine;
import physics.Body;
import render.RenderNode;
import render.XYTRenderNode;
import shipmaker.CatalogPart;
import shipmaker.CatalogPartType;
import shipmaker.partplacer.HexTextControl;
import shipmaker.partplacer.IconEditField;
import ships.Equipment;
import render.RenderPreferences;

public class StandardEngine implements CatalogPartType {

	public CatalogPart create() {
		return new CatalogPart() {
			RenderNode ragdoll;
			char hwid;
			
			public CatalogPartType type() {
				return StandardEngine.this;
			}
			
			public RenderNode getRenderRagdoll(Body base) {
				if (ragdoll == null) {
					ragdoll = Engine.makeIndependantPart(base); 
				}
				return ragdoll;
			}
			
			public RenderNode getOptionsOverlay(final Space s) {
				return new XYTRenderNode(-200, 0, 0) {
					{
						addChild(new IconEditField(10, 40, 0, s, new HexTextControl(4) {
							
							public boolean drawIcon(Graphics2D g, RenderPreferences prefs) {
								return false;
							}
							
							protected void set(int x) {
								hwid = (char)x;
							}
							
							protected int get() {
								return (int)hwid;
							}
						}));
					}
					public void draw(Graphics2D g, RenderPreferences prefs) {
						g.setColor(Color.white);
						int w = g.getFontMetrics().stringWidth(name());
						g.drawString(name(), 100-w/2, 20);
						g.drawString("Hardware ID", 100, 45);
						g.drawRect(0, -1, 201, 101);
						g.drawLine(90, 25, 90, 100);
						g.drawLine(0, 25, 200, 25);
						g.drawLine(0, 50, 200, 50);
						g.drawLine(0, 75, 200, 75);
						g.setColor(Color.gray.brighter());
						g.drawString("100", 60, 70);
						g.drawString("Max Force", 100, 70);
						g.drawString("10", 60, 95);
						g.drawString("Power/Tick", 100, 95);
					}
				};
			}
			
			public Equipment generateEquipment(float effectiveX, float effectiveY,
					float effectiveTheta) {
				return null;
			}
		};
	}

	public String name() {
		return "Standard Engine";
	}

	public float mass() {
		return 25;
	}

	public float rotationalInertia() {
		return 2000;
	}

}
