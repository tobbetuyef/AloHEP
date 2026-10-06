package org.hepforge.alohep.gfx;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Input implements KeyListener,MouseListener,MouseWheelListener{

	private boolean keyPressing[];
	private boolean keyUsed[];
	private boolean mousePressing[];
	private boolean mouseShiftPressing[];
	private boolean mouseUsed[];
	private boolean mouseShiftUsed[];
	
	private int wheel;
	public Input(){
		keyPressing = new boolean[256];
		keyUsed = new boolean[256];
		mousePressing = new boolean[5];
		mouseShiftPressing = new boolean[5];
		mouseUsed = new boolean[5];
		mouseShiftUsed = new boolean[5];
	}

	public boolean isKeyPressing(int key)
	{
		return keyPressing[key];
	}
	public boolean isKeyClicked(int key)
	{
		if(keyPressing[key]&&!keyUsed[key])
		{
			keyUsed[key] = true;
			return true;
		}
		return false;
	}
	public boolean isMousePressing(int button)
	{
		return mousePressing[button];
	}
	public boolean isMouseClicked(int button)
	{
		if(mousePressing[button]&&!mouseUsed[button])
		{
			mouseUsed[button] = true;
			return true;
		}
		return false;
	}
	public boolean isMouseShiftPressing(int button)
	{
		return mouseShiftPressing[button];
	}
	public boolean isMouseShiftClicked(int button)
	{
		if(mouseShiftPressing[button]&&!mouseShiftUsed[button])
		{
			mouseShiftUsed[button] = true;
			return true;
		}
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {

	}
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.isShiftDown())
			mouseShiftPressing[e.getButton()] = true;
		else
			mousePressing[e.getButton()] = true;
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseShiftPressing[e.getButton()] = false;
		mouseShiftUsed[e.getButton()] = false;
		mousePressing[e.getButton()] = false;
		mouseUsed[e.getButton()] = false;
	}
	@Override
	public void keyPressed(KeyEvent e) {
		keyPressing[e.getKeyCode()] = true;
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		keyPressing[e.getKeyCode()] = false;
		keyUsed[e.getKeyCode()] = false;
	}
	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		 wheel = (int)e.getPreciseWheelRotation();
	}
	
	public int getWheel() {
		return wheel;
	}

	public void setWheel(int wheel) {
		this.wheel = wheel;
	}

}
