package env;

import render.RenderNode;

public interface Entity extends Ticked{
	public RenderNode getVisuals();
}
