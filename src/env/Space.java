package env;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import render.RenderNode;
import shipmaker.EditorShip.EditorShipPart;

public class Space {
	HashSet<Entity> entities;
	TreeSet<RenderNode> rendities;
	private boolean die;
	
	boolean block;
	boolean canBlock;
	Object blockLock;
	private ArrayList<KeyListener> keylisteners;
	private ArrayList<Beacon> beacons;
	
	public static final int MS_PER_TICK = 20;
	
	public Space() {
		entities = new HashSet<>();
		rendities = new TreeSet<RenderNode>(new Comparator<RenderNode>() {
			public int compare(RenderNode a, RenderNode b) {
				if (a.layer() != b.layer()) {
					return a.layer() - b.layer();
				}
				return a.hashCode() - b.hashCode();
			}
		});
		keylisteners = new ArrayList<>();
		die = false;
		block = false;
		canBlock = false;
		blockLock = new Object();
		beacons = new ArrayList<Beacon>();
	}
	public Set<RenderNode> rendities() {
		return rendities;
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
		rendities.add(e.getVisuals());
	}
	
	public void removeEntity(Entity e) {
		entities.remove(e);
		rendities.remove(e.getVisuals());
	}
	
	public Runnable removeEntityLater(final Entity e) {
		return new Runnable() {
			public void run() {
				removeEntity(e);
			}
		};
	}
	
	public void tickFrame(int ms) {
		//TODO: make all of this stuff multi-threaded later
		for (Entity e: entities) {
			e.tickInternals(ms);
		}
		
		for (Entity e: entities) {
			e.tickPhysics(ms);
		}
	}
	
	public void blockRunning(boolean blk) {
		synchronized(blockLock) {
			if (blk) {
				while (!canBlock)
					try { blockLock.wait(); } catch (InterruptedException e) {}
				block = true;
				blockLock.notifyAll();
			} else {
				block = false;
				blockLock.notifyAll();
			}
		}
	}
	
	public void start() {
		new Thread() {
			public void run() {
				long last = System.currentTimeMillis();
				
				while(!die) {
					try {
						synchronized(blockLock) {
							while (block) blockLock.wait();
						}
						long time = System.currentTimeMillis();
						long delay = MS_PER_TICK-(time-last);
						last = time;
						if (delay > 0) {
							sleep(delay);
						}
					} catch (InterruptedException e) {}
					synchronized(blockLock) {
						canBlock = false;
					}
					tickFrame(MS_PER_TICK);
					synchronized(blockLock) {
						canBlock = true;
						blockLock.notifyAll();
					}
				}
			}
		}.start();
	}
	
	public void keytyped(KeyEvent k) {
		for (KeyListener kl : keylisteners) {
			kl.keyTyped(k);
		}
	}
	
	public void addKeyListener(KeyListener kl) {
		keylisteners.add(kl);
	}
	
	public void removeKeyListener(KeyListener kl) {
		keylisteners.remove(kl);
	}
	public Runnable removeEntityAction(final EditorShipPart part) {
		return new Runnable() {
			public void run() {
				entities.remove(part);
				rendities.remove(part.getVisuals());
			}
		};
	}
	public void stop() {
		die=true;
	}
	
	public void clearEntities() {
		entities.clear();
		rendities.clear();
	}
	
	public Collection<Beacon>beacons() {
		return beacons;
	}
}
