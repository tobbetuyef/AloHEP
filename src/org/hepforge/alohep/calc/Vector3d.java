package org.hepforge.alohep.calc;

public class Vector3d {

	public double x,y,z;
	
	public static Vector3d unitVec = new Vector3d(1, 1, 1);

	public Vector3d()
	{
		this.x = 0;
		this.y = 0; 
		this.z = 0;
	}
	public Vector3d(double x, double y, double z)
	{
		this.x = x;
		this.y = y; 
		this.z = z;
	}
	public Vector3d(Vector3d vec)
	{
		this.x = vec.x;
		this.y = vec.y; 
		this.z = vec.z;
	}
	public Vector3d copy()
	{
		return new Vector3d(this);
	}
	public double diagonal()
	{
		return Math.pow(x*x+y*y+z*z,0.5);
	}
	public double volume()
	{
		return x*y*z;
	}
	public Vector3d set(Vector3d vec) 
	{
		x = vec.x;
		y = vec.y;
		z = vec.z;
		return this;
	}
	public Vector3d set(double x, double y, double z) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	public Vector3d add(Vector3d vec)
	{
		x += vec.x;
		y += vec.y;
		z += vec.z;
		return this;
	}
	public Vector3d subtract(Vector3d vec)
	{
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
		return this;
	}
	public Vector3d vectorMultiply(Vector3d vec)
	{
		x = y * vec.z - z * vec.y;
		y = z * vec.x - x * vec.z;
		z = x * vec.y - y * vec.x;
		return this;
	}
	public Vector3d multiply(Vector3d vec)
	{
		x = x*vec.x;
		y = y*vec.y;
		z = z*vec.z;
		return this;
	}
	public Vector3d divide(Vector3d vec)
	{
		x = x/vec.x;
		y = y/vec.y;
		z = z/vec.z;
		return this;
	}
	public Vector3d normalize()
	{
		double d = diagonal();
		x = x / d;
		y = y / d;
		z = z / d;
		return this;
	}
	public Vector3d multiply(double k)
	{
		x*=k;
		y*=k;
		z*=k;
		return this;
	}
	public Vector3d pow(double k)
	{
		x = Math.pow(x, k);
		y = Math.pow(y, k);
		z = Math.pow(z, k);
		return this;
	}
	public Vector3d exp()
	{
		x = Math.pow(Math.E, x);
		y = Math.pow(Math.E, y);
		z = Math.pow(Math.E, z);
		
		return this;
	}
	public Vector3d abs()
	{
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		return this;
	}

	public double scalarMultiply(Vector3d vec)
	{
		return x * vec.x + y * vec.y + z * vec.z;	
	}
	public boolean isNaN()
	{
		return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
	}
	public boolean isInfinity()
	{
		return Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z);
	}
	public String print()
	{
		return x+", "+y+", "+z;
	}

}
