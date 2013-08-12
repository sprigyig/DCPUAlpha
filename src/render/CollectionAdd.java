package render;

import java.util.Collection;

public class CollectionAdd<T> implements Runnable {
	private T e;
	private Collection<T> c;

	public CollectionAdd(Collection<T> c, T e) {
		this.c = c;
		this.e = e;
	}
	
	public void run() {
		c.add(e);
	}

}
