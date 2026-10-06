package org.hepforge.alohep.gfx;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.hepforge.alohep.AloHEP;
import org.hepforge.alohep.calc.Vector3d;

public class Camera {

	private Vector3d scale;
	private double scaleFactor = 1;
	private Point center;
	private Axis axis = Axis.X;
	private Region region = Region.OFF;
	public static enum Region{OFF, BORDER, ALL}
	private boolean centerPointActive = false;
	public static enum Axis{X, Y, Z}
	
	public Camera(AloHEP alohep)
	{

		scale = Vector3d.unitVec.copy().divide(alohep.getLuminosityCalc().getIPsize()).multiply(0.5);
		scale.multiply(new Vector3d(0.5,0.5,1.5));
		center = new Point(0,0);
	}
	public Point getCameraPos(Vector3d pos)
	{
		int x = 0, y = 0;

		switch(axis)
		{
		case X:
			x = center.x+(int)((pos.z*scale.z*scaleFactor+0.5)*Display.height+(Display.width-Display.height)/2);
			y = center.y+(int)((pos.y*scale.y*scaleFactor+0.5)*Display.height);
			break;
		case Y:
			x = center.x+(int)((pos.z*scale.z*scaleFactor+0.5)*Display.height+(Display.width-Display.height)/2);
			y = center.y+(int)((pos.x*scale.x*scaleFactor+0.5)*Display.height);

			break;
		case Z:
			x = center.x+(int)((pos.x*scale.x*scaleFactor+0.5)*Display.height+(Display.width-Display.height)/2);
			y = center.y+(int)((pos.y*scale.y*scaleFactor+0.5)*Display.height);
			break;
		}
		
		return new Point(x, y);
	}
	public Rectangle getRegionRect(Vector3d IPSize)
	{
		Point point = getCameraPos(new Vector3d(0,0,0));
		int width = 0;
		int height = 0;
		switch(axis)
		{
		case X:
			width = (int)(IPSize.z*scale.z*scaleFactor*Display.width);
			height = (int)(IPSize.y*scale.y*scaleFactor*Display.height);
			break;
		case Y:
			width = (int)(IPSize.z*scale.z*scaleFactor*Display.width);
			height = (int)(IPSize.x*scale.x*scaleFactor*Display.height);

			break;
		case Z:
			width = (int)(IPSize.x*scale.x*scaleFactor*Display.width);
			height = (int)(IPSize.y*scale.y*scaleFactor*Display.height);
			break;
		}
		int x = point.x-width/2;
		int y = point.y-height/2;
		return new Rectangle(x, y, width, height);
	}
	public ArrayList<Rectangle> getCellLines(Vector3d IPSize, Vector3d IPDiff)
	{
		Point point = getCameraPos(new Vector3d(0,0,0));
		Rectangle regionRect = getRegionRect(IPSize);
		point.x -=regionRect.width/2;
		point.y -=regionRect.height/2;
		
		double dx = 0;
		double dy = 0;
		switch(axis)
		{
		case X:
			dx = IPDiff.z*scale.z*scaleFactor*Display.width;
			dy = IPDiff.y*scale.y*scaleFactor*Display.height;
			break;
		case Y:
			dx = IPDiff.z*scale.z*scaleFactor*Display.width;
			dy = IPDiff.x*scale.x*scaleFactor*Display.height;

			break;
		case Z:
			dx = IPDiff.x*scale.x*scaleFactor*Display.width;
			dy = IPDiff.y*scale.y*scaleFactor*Display.height;
			break;
		}
		ArrayList<Rectangle> lineList = new ArrayList<Rectangle>();
		for(double id = dx; id < regionRect.width;id+=dx)
		{
			int i = (int)id;
			lineList.add(new Rectangle(point.x+i,point.y,point.x+i,point.y+regionRect.height));
		}
		for(double jd = dy; jd < regionRect.height;jd+=dy)
		{
			int j = (int)jd;
			lineList.add(new Rectangle(point.x,point.y+j,point.x+regionRect.width,point.y+j));
		}
		
		return lineList;
	}

	public Axis getAxis() {
		return axis;
	}
	public void setAxis(Axis axis) {
		this.axis = axis;
	}
	public Vector3d getScale() {
		return scale;
	}
	public void scaleChange(double k)
	{
		scaleFactor*=k;
	}
	public void setScale(Vector3d scale) {
		this.scale = scale;
	}
	public double getScaleFactor() {
		return scaleFactor;
	}
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
	public Point getCenter() {
		return center;
	}
	public void setCenter(Point center) {
		this.center.x = center.x;
		this.center.y = center.y;
	}
	public boolean isCenterPointActive() {
		return centerPointActive;
	}
	public void setCenterPointActive(boolean centerPointActive) {
		this.centerPointActive = centerPointActive;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	
}
