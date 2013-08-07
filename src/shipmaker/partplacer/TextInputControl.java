package shipmaker.partplacer;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import render.FocusableOverlay;
import render.RenderPreferences;

public interface TextInputControl extends FocusableOverlay {
	public String content();
	
	public void startEdit();
	public void typed(KeyEvent e);
	public void endEdit(boolean escaped);
	public boolean drawIcon(Graphics2D g, RenderPreferences prefs);
	public boolean editing();
}
