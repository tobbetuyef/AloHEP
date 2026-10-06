package org.hepforge.alohep.gfx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import org.hepforge.alohep.AloHEP;
import org.hepforge.alohep.calc.Bunch;
import org.hepforge.alohep.calc.MacroParticle;

public class MainState extends State {

	private AloHEP alohep;
	private Bunch bunchL;
	private Bunch bunchR;
	private boolean mouseButtonPressed = false;
	private Point start;
	private Point prevCenter;
	private Point center;
	public MainState(AloHEP alohep) {
		super(alohep.getAnimationPanel());
		this.alohep = alohep;
	}

	@Override
	public void init(AnimationPanel animationPanel) {
		bunchL = alohep.getLuminosityCalc().getBunchL();
		bunchR = alohep.getLuminosityCalc().getBunchR();
		start = new Point(0,0);
		center = new Point(0,0);
		prevCenter = new Point(0,0);
	}

	@Override
	public void update() {
		if(getinput().isKeyPressing(KeyEvent.VK_RIGHT))
		{

		}
		if(getinput().isKeyPressing(KeyEvent.VK_LEFT))
		{

		}
		if(getinput().isKeyClicked(KeyEvent.VK_X))
		{
			cleanCenter();
			getCamera().setAxis(Camera.Axis.X);
		}
		
		if(getinput().isKeyClicked(KeyEvent.VK_Y))
		{
			cleanCenter();
			getCamera().setAxis(Camera.Axis.Y);
		}
		
		if(getinput().isKeyClicked(KeyEvent.VK_Z))
		{
			cleanCenter();
			getCamera().setAxis(Camera.Axis.Z);
		}
		if(getinput().isKeyClicked(KeyEvent.VK_R))
		{
			switch (getCamera().getRegion()) {
			case OFF:
				getCamera().setRegion(Camera.Region.BORDER);
				break;
			case BORDER:
				getCamera().setRegion(Camera.Region.ALL);
				break;
			case ALL:
				getCamera().setRegion(Camera.Region.OFF);
				break;
			}
		}
		if(getinput().isKeyClicked(KeyEvent.VK_C))
		{
			getCamera().setCenterPointActive(!getCamera().isCenterPointActive());
		}
		if(getinput().getWheel()>0)
		{

			center.x*=1.1;
			center.y*=1.1;
			getCamera().scaleChange(0.9);
			getinput().setWheel(0);
		}
		if(getinput().getWheel()<0)
		{

			center.x*=0.9;
			center.y*=0.9;
			getCamera().scaleChange(1.1);
			getinput().setWheel(0);

		}
		if(getinput().isMousePressing(MouseEvent.BUTTON1))
		{
			Point p = board.getDisplay().getMousePosition();
			if(p != null)
			{
				if(!mouseButtonPressed)
				{
					start = p;
					mouseButtonPressed = true;
				}
				else
				{
					center.x = prevCenter.x + (p.x - start.x);
					center.y = prevCenter.y + (p.y - start.y);
				}
			}
		}
		else
		{
			mouseButtonPressed = false;
			prevCenter.x = center.x;
			prevCenter.y = center.y;
		}
		getCamera().setCenter(center);
	}
	private void cleanCenter()
	{
		center.x = 0;
		center.y = 0;
		prevCenter.x = 0;
		prevCenter.y = 0;
		
	}
	
	@Override
	public void render(BufferedImage image) 
	{
		Graphics2D g = image.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.setColor(new Color(255,255,255,255));
		g.fillRect(0,0,image.getWidth(),image.getHeight());
		g.setColor(new Color(0x0070C0));
		for(MacroParticle MP:bunchL.getMPs())
		{
			Point p = getCamera().getCameraPos(MP.getPos());
			g.fillOval(p.x, p.y, 3, 3);
		}
		g.setColor(new Color(0xDE7F00));
		for(MacroParticle MP:bunchR.getMPs())
		{
			Point p = getCamera().getCameraPos(MP.getPos());
			g.fillOval(p.x, p.y, 3, 3);
		}
		if(getCamera().isCenterPointActive())
		{
			Point l1 = getCamera().getCameraPos(bunchL.getPos());
			g.setColor(Color.BLACK);
			g.fillOval(l1.x, l1.y, 5, 5);
			Point l2 = getCamera().getCameraPos(bunchR.getPos());
			g.fillOval(l2.x, l2.y, 5, 5);
			
		}
		if(getCamera().getRegion() != Camera.Region.OFF)
		{
			Rectangle rect = getCamera().getRegionRect(alohep.getLuminosityCalc().getIPsize());
			g.setColor(Color.RED);
			g.drawRect(rect.x,rect.y,rect.width, rect.height);
			if(getCamera().getRegion() == Camera.Region.ALL)
			{
				for(Rectangle line:getCamera().getCellLines(alohep.getLuminosityCalc().getIPsize(), alohep.getLuminosityCalc().getIPDiff()))
				{
					g.drawLine(line.x, line.y, line.width, line.height);
				}	
			}
		}
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif", 1, 12));
		g.drawString(getCamera().getAxis() + " Axis", Display.width-80, 20);
		g.drawString("Scale x"+new DecimalFormat("0.0").format(getCamera().getScaleFactor()), Display.width-80, 40);
		
		g.dispose();
	}

}
