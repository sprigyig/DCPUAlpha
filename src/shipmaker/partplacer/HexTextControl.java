package shipmaker.partplacer;

import java.awt.event.KeyEvent;

public abstract class HexTextControl implements TextInputControl {
	boolean editing;
	String content;
	private int len;

	public HexTextControl(int len) {
		this.len = len;
	}

	public String content() {
		if (editing) {
			return "0x"+(String.format("%s\u258c", content).toUpperCase());
		} else {
			return "0x"+(String.format("%0" + len + "x", get()).toUpperCase());
		}
	}

	protected abstract int get();

	protected abstract void set(int x);

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
			} else if (e.getKeyCode() >= KeyEvent.VK_0
					&& e.getKeyCode() <= KeyEvent.VK_9) {
				if (content.length() < len)
					content += (e.getKeyChar());
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (content.length() > 0)
					content = content.substring(0, content.length() - 1);
			} else if (e.getKeyCode() >= KeyEvent.VK_A
					&& e.getKeyCode() <= KeyEvent.VK_F) {
				if (content.length() < len)
					content += (char) ('A' + e.getKeyCode() - KeyEvent.VK_A);
			}

		}
	}

	public void endEdit(boolean escaped) {
		editing = false;
		if (escaped)
			return;
		if (content.length() > 0) {
			set(Integer.parseInt(content, 16));
		}
	}
	
	public boolean editing() {
		return editing;
	}
	
	public void lostFocus() {
		endEdit(false);
	}
}
