package env;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import render.FocusableOverlay;
import render.RenderNode;

public class Space {
	HashSet<Entity> entities;
	TreeSet<RenderNode> rendities;
	private boolean die;
	
	boolean block;
	boolean canBlock;
	Object blockLock;
	private ArrayList<KeyListener> keylisteners;
	private FocusableOverlay focused;
	
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
						long delay = 20-(time-last);
						last = time;
						if (delay > 0) {
							sleep(delay);
						}
					} catch (InterruptedException e) {}
					synchronized(blockLock) {
						canBlock = false;
					}
					tickFrame(20);
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
	
	public void setFocused(FocusableOverlay tic) {
		if (focused !=null && focused != tic) {
			focused.lostFocus();
		}
		this.focused = tic;
	}
}
