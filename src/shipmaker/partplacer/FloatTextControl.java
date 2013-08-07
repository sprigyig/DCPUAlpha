package shipmaker.partplacer;

import java.awt.event.KeyEvent;

public abstract class FloatTextControl implements TextInputControl {
	boolean editing;
	String content;
	String prefix;
	String suffix;
	
	public FloatTextControl(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
		editing = false;
	}
	
	public String content() {
		if (editing) {
			return String.format("%s %s\u258c %s", prefix, content, suffix);
		} else {
			return String.format("%s %.04f %s", prefix, get(), suffix);
		}
	}

	protected abstract float get();
	protected abstract void set(float f);

	public void startEdit() {
		content = "";
		editing = true;
	}

	public void typed(KeyEvent e) {
		if (editing) {
			System.out.println(e.getKeyCode());
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				endEdit(false);
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				endEdit(true);
			} else if (e.getKeyCode() >= KeyEvent.VK_0 &&
					e.getKeyCode() <= KeyEvent.VK_9) {
				content+=(e.getKeyChar());
			} else if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
				if (content.indexOf('.')==-1) {
					content+='.';
				}
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (content.length() > 0)
				content = content.substring(0,content.length()-1);
			} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
				if (content.length() == 0) {
					content+="-";
				}
			}
			
		}
	}

	public void endEdit(boolean escaped) {
		editing = false;
		if (escaped) return;
		if (content.length()>0) {
			set(Float.parseFloat(content));
		}
	}
	
	public boolean editing() {
		return editing;
	}
	
	public void lostFocus() {
		endEdit(false);
	}

}
