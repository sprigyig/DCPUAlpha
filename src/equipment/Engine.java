package equipment;

import static dcpu.DcpuConstants.REG_A;
import static dcpu.DcpuConstants.REG_B;
import static dcpu.DcpuConstants.REG_C;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import physics.Body;
import physics.BodyForce;
import physics.ForceSource;
import render.BodyRenderNode;
import render.RTTRenderNode;
import render.RenderNode;
import render.RenderPreferences;
import ships.Equipment;
import ships.Ship;
import dcpu.CpuWatcher;
import dcpu.Dcpu;
import dcpu.Hardware;

public class Engine implements Equipment, ForceSource, CpuWatcher, Hardware {

	private float max;
	private float t2;
	private float t1;
	private float r;
	private long onRate;
	private long onMax;
	private char setpoint;
	private float renderOnness;
	
	private char[] commands;
	private char[] setpoints;
	private Ship ship;
	private int fullCost;
	private char hwid;

	public Engine(float r, float t1, float t2, float max, int fullCost, char hwid) {
		this.r = r;
		this.t1 = t1;
		this.t2 = t2;
		this.max = max;
		commands = new char[4];
		setpoints = new char[4];
		this.fullCost = fullCost;
		this.hwid = hwid;
	}
	
	public static RenderNode makeIndependantPart(Body base) {
		return new BodyRenderNode(base) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				Engine.draw(g, prefs, 1f);
			}
		};
	}
	
	public static void draw(Graphics2D g, RenderPreferences prefs, float renderOnness) {
		int t = prefs.borderThickness();
		g.translate(14, 0);
		
		g.setStroke(new BasicStroke(t));
		g.setColor(prefs.borderColor());
		g.drawRect(-25, -15, 20, 30);
		g.drawRect(-5, -10, 5, 20);
		
		g.setColor(prefs.body1());
		g.fillRect(-25, -15, 20, 30);
		g.setColor(prefs.highlight1());
		g.fillRect(-5, -10, 5, 20);
		
		g.setColor(prefs.borderColor());
		g.fillPolygon(new int[]{0,0,(int)(16*Math.sqrt(renderOnness)+t*2)}, new int[]{-8-t*2,8+t*2,0}, 3);
		g.fillPolygon(new int[]{0,0,(int)(22*Math.sqrt(renderOnness)+t*2)}, new int[]{-4-t*2,4+t*2,0}, 3);
		
		g.setColor(new Color(180,180,255));
		g.fillPolygon(new int[]{0,0,(int)(16*Math.sqrt(renderOnness))}, new int[]{-8,8,0}, 3);
		g.setColor(new Color(220,220,255));
		g.fillPolygon(new int[]{0,0,(int)(22*Math.sqrt(renderOnness))}, new int[]{-4,4,0}, 3);
	}
	
	public void addedTo(Ship s) {
		s.addBodyForce(new BodyForce(this));
		s.addRenderNode(new RTTRenderNode(this) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				Engine.draw(g, prefs, renderOnness);
			}
			
		});
		s.cpu.addWatcher(this);
		s.cpu.addHardware(hwid, this);
		ship = s;
	}

	public void reset() {
		onRate = 0;
		onMax = 0;
		setpoint = 0;
		
	}
	public void physicsTickPreForce() {
		if (onMax == 0) {
			renderOnness = 0f;
		} else {
			float mix = (float)onRate / onMax;
			int mixportion = 2;
			renderOnness = (renderOnness * (mixportion) + mix)/(mixportion+1);
		}
		if (onMax > 0) {
			ship.power.sink(fullCost*onRate/onMax);
		}
	}
	public void physicsTickPostForce() {
		this.onMax = 0;
		this.onRate = 0;
	}

	public void triggerSynchronizedEvent(char id, int cyclesAgo) {
		for (int i=0;i<commands.length;i++) {
			if (id == commands[i]) {
				setpoint = setpoints[i];
			}
		}
	} 

	public float position_radius() {
		return r;
	}

	public float position_theta() {
		return t1;
	}

	public float alignment_theta() {
		return t2;
	}

	public float force_magnitude() {
		if (onMax == 0) {
			return 0;
		}
		return max * onRate / onMax;
	}

	public void cpu_changed(Dcpu cpu, long cyclesAdvanved, boolean idle) {
		onRate += cyclesAdvanved * setpoint;
		onMax += cyclesAdvanved * 0xFFFF;
	}

	public void plugged_in(Dcpu parent, char id) {
		
	}

	public void query(Dcpu parent) {
		
	}

	public void interrupted(Dcpu parent) {
		parent.cyclecnt+=1;
		if (parent.regs.gp[REG_A]==0) {
			setpoint = parent.regs.gp[REG_B];
		} else if (parent.regs.gp[REG_A]-1 < commands.length) {
			commands[parent.regs.gp[REG_A]-1] = parent.regs.gp[REG_C];
			setpoints[parent.regs.gp[REG_A]-1] = parent.regs.gp[REG_B];
			System.out.printf("Engine %d cmd %d setpoint %d\n", (int)hwid, (int) parent.regs.gp[REG_C], (int)parent.regs.gp[REG_B]);
		}
	}

}
