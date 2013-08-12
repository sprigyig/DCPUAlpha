package render;

import java.util.Collection;

public class CollectionRemoval<T> implements Runnable {
	private T e;
	private Collection<T> c;

	public CollectionRemoval(Collection<T> c, T e) {
		this.c = c;
		this.e = e;
	}
	
	public void run() {
		c.remove(e);
	}

}
