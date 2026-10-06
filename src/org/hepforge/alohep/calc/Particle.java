package org.hepforge.alohep.calc;

public class Particle {

	public static double e0 = 8.8541878128e-12;
	private static double c = 2.99792458e8;
	public static double Coulomb = 1.602176634e-19;
	public static double eVtoKg = 1.782661921e-36;
	
	private String name;
	private double charge;
	private double mass;
	private double lifetime;
	private double rad;
	
	public Particle(String name, double charge, double mass, double lifetime)
	{
		this.name = name;
		this.charge = charge;
		this.mass = mass;
		this.lifetime = lifetime;
		init();
	}
	public void init()
	{
		rad = 1 / (4*Math.PI * e0) * Math.pow(charge * Coulomb, 2)/ (mass * eVtoKg * c * c);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCharge() {
		return charge;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public double getRad() {
		return rad;
	}

	public void setRad(double rad) {
		this.rad = rad;
	}

	public double getLifetime() {
		return lifetime;
	}

	public void setLifetime(double lifetime) {
		this.lifetime = lifetime;
	}
}
