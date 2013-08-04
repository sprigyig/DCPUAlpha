package ships;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import physics.Body;
import physics.BodyForce;
import render.BodyRenderNode;
import render.RenderNode;
import render.RenderPreferences;
import dcpu.Dcpu;
import dcpu.Hardware;
import env.Entity;
import equipment.PowerGrid;

public class Ship implements Entity {
	List<Equipment> equipment;
	List<BodyForce> forces;
	RenderNode renderParts;
	public Dcpu cpu;
	public Body me;
	public PowerGrid power;
	int cpu_freeze;
	
	public Ship(float mass, float ri) {
		me = new Body(0, 0, 0, mass, ri);
		cpu = new Dcpu();
		renderParts = new BodyRenderNode(me) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
			}
		};
		forces = new ArrayList<BodyForce>();
		equipment = new ArrayList<Equipment>();
	}
	
	public void addEquipment(Equipment e) {
		equipment.add(e);
		e.addedTo(this);
	}
	
	public RenderNode getVisuals() {
		return renderParts;
	}

	public void tickInternals(int msPerTick) {
		if (cpu_freeze <= 0) {
			cpu.step_cycles(msPerTick*50);
		} else {
			cpu_freeze -=1;
		}
	}

	public void tickPhysics(int msPerTick) {
		for (Equipment e : equipment) {
			e.physicsTickPreForce();
		}
		me.apply(forces, msPerTick);
		for (Equipment e : equipment) {
			e.physicsTickPostForce();
		}
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
	public void triggerSynchronizedEvent(char id, int cyclesAgo) {
		for (Equipment e:equipment) {
			e.triggerSynchronizedEvent(id, cyclesAgo);
		}
	}
	
	public void reset() {
		for (Equipment e:equipment) {
			e.reset();
		}
		me.reset();
		cpu.reset();
	}

	public void brownOut() {
		System.out.println("Brown Out");
		cpu.reset();
		for (Equipment e:equipment) {
			e.reset();
		}
		cpu_freeze = 500;
	}
	
	public int getCpuFreeze() {
		return cpu_freeze;
	}
}
