package org.hepforge.alohep.calc;

public class Vector2d {

	public double x,y;
	
	public static Vector2d unitVec = new Vector2d(1, 1);
	
	public Vector2d(double x, double y)
	{
		this.x = x;
		this.y = y; 
	}
	public Vector2d(Vector2d vec)
	{
		this.x = vec.x;
		this.y = vec.y; 
	}
	public Vector2d(Vector3d vec)
	{
		this.x = vec.x;
		this.y = vec.y;
	}
	public Vector2d copy()
	{
		return new Vector2d(this);
	}
	public double diagonal()
	{
		return Math.pow(x*x+y*y,0.5);
	}
	public double area()
	{
		return x*y;
	}
	public Vector2d set(Vector2d vec) 
	{
		x = vec.x;
		y = vec.y;
		return this;
	}
	public Vector2d set(double x, double y) 
	{
		this.x = x;
		this.y = y;
		return this;
	}
	public Vector2d add(Vector2d vec)
	{
		x += vec.x;
		y += vec.y;
		return this;
	}
	public Vector2d subtract(Vector2d vec)
	{
		x -= vec.x;
		y -= vec.y;
		return this;
	}

	public Vector2d multiply(Vector2d vec)
	{
		x = x*vec.x;
		y = y*vec.y;
		return this;
	}
	public Vector2d divide(Vector2d vec)
	{
		x = x/vec.x;
		y = y/vec.y;
		return this;
	}
	public Vector2d normalize()
	{
		double d = diagonal();
		x = x / d;
		y = y / d;
		return this;
	}
	public Vector2d multiply(double k)
	{
		x*=k;
		y*=k;
		return this;
	}
	public Vector2d add(double k)
	{
		x+=k;
		y+=k;
		return this;
	}
	public Vector2d pow(double k)
	{
		x = Math.pow(x, k);
		y = Math.pow(y, k);
		return this;
	}
	public Vector2d exp()
	{
		x = Math.pow(Math.E, x);
		y = Math.pow(Math.E, y);
		
		return this;
	}
	public Vector2d abs()
	{
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}
	public double scalarMultiply(Vector2d vec)
	{
		return x * vec.x + y * vec.y;	
	}
	public boolean isNaN()
	{
		return Double.isNaN(x) || Double.isNaN(y);
	}
	public boolean isInfinity()
	{
		return Double.isInfinite(x) || Double.isInfinite(y);
	}
	public String print()
	{
		return x+", "+y;
	}

}
