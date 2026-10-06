package org.hepforge.alohep.gfx;

import java.awt.image.BufferedImage;

public abstract class State {

	protected AnimationPanel board;
	private boolean init = false;

	public State(AnimationPanel board)
	{
		this.board = board;
	}
	public abstract void init(AnimationPanel board);
	public abstract void update();

	public abstract void render(BufferedImage image);
	public Input getinput()
	{
		return board.getInput();
	}
	public Camera getCamera()
	{
		return board.getCamera();
	}

	public boolean isInit() {
		return init;
	}
	public void setInit(boolean init) {
		this.init = init;
	}
}
