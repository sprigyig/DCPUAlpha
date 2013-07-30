package ships;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Body;
import physics.BodyForce;
import render.BodyRenderNode;
import render.RenderNode;
import dcpu.Dcpu;
import dcpu.Hardware;

public class Ship {
	List<Equipment> equipment;
	List<BodyForce> forces;
	RenderNode renderParts;
	public Dcpu cpu;
	public Body me;
	
	public Ship(float mass, float ri) {
		me = new Body(0, 0, 0, mass, ri);
		cpu = new Dcpu();
		renderParts = new BodyRenderNode(me) {
			public void draw(Graphics2D g) {
				
			}
		};
		forces = new ArrayList<BodyForce>();
		equipment = new ArrayList<Equipment>();
	}
	
	public void stepCpu(int cycles) {
		cpu.step_cycles(cycles);
	}
	
	public void stepPhysics() {
		me.apply(forces);
	}
	
	public void addEquipment(Equipment e) {
		equipment.add(e);
		e.addedTo(this);
	}
	
	public RenderNode getVisuals() {
		return renderParts;
	}
	
	//Only equipment should call these:
	public void addRenderNode(RenderNode rn) {
		renderParts.addChild(rn);
	}
	public void addPluginHardware(char id, Hardware hw) {
		cpu.addHardware(id, hw);
	}
	public void addBodyForce(BodyForce bf) {
		forces.add(bf);
	}
}
