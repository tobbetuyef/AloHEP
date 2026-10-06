package org.hepforge.alohep.calc;

public class MacroParticle {

	private Vector3d pos;
	private Vector3d vel;
	private Vector3d acc;
	private double gamma;
	private boolean isCenter;
	private static double Const_uc = 3.0 / 2.0 * LuminosityCalc.hbar*LuminosityCalc.c;
	public MacroParticle(Vector3d pos, double gamma)
	{
		this.setPos(pos);
		vel = new Vector3d();
		acc = new Vector3d();
		this.gamma = gamma;
	}
	public MacroParticle(Vector3d pos, Vector3d vel, double gamma)
	{
		this.setPos(pos);
		this.setVel(vel);
		acc = new Vector3d();
		this.gamma = gamma;
	}

	public void update(double dt)
	{
		vel.add(acc.copy().multiply(dt));
		pos.add(vel.copy().multiply(dt));
		acc.set(0,0,0);
	}
	public void update(double dtKick, double dtDrift)
	{
		vel.add(acc.copy().multiply(dtKick));
		pos.add(vel.copy().multiply(dtDrift));
		acc.set(0,0,0);
	}
	public void updateGamma(Particle particle, double dt)
	{
		double ro = 1.0 / Math.sqrt(acc.x*acc.x+acc.y*acc.y);
		
		double uc = Const_uc * gamma*gamma*gamma / ro;
		double ksi = uc / (gamma*particle.getMass());
		double ksi2 = ksi*ksi;
		double ksi3 = ksi2*ksi;
		double ksi4 = ksi3*ksi;
		double U = (1.0 + 9.0267435*ksi + 14.0612755*ksi2 + 0.26367551*ksi3) / (1.0 + 12.9947460*ksi + 44.4076697*ksi2 + 38.3037583*ksi3 + 1.7430767*ksi4);
		gamma = gamma / (1 + 2.0 / 3.0 * particle.getRad() * gamma*gamma*gamma / (ro*ro) * dt * U);
	}
	
	public Vector3d getPos() {
		return pos;
	}

	public void setPos(Vector3d pos) {
		this.pos = pos;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public Vector3d getVel() {
		return vel;
	}

	public void setVel(Vector3d vel) {
		this.vel = vel;
	}

	public Vector3d getAcc() {
		return acc;
	}

	public void setAcc(Vector3d acc) {
		this.acc = acc;
	}
	public boolean isCenter() {
		return isCenter;
	}
	public void setCenter(boolean isCenter) {
		this.isCenter = isCenter;
	}
}
