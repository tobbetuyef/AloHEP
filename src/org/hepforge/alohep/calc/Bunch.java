package org.hepforge.alohep.calc;

import java.util.ArrayList;
import java.util.Random;

import org.hepforge.alohep.AloHEP;

public class Bunch {

	private AloHEP alohep;
	private LuminosityCalc lumCalc;
	private Beam beam;
	private Slice slice[];
	private Vector3d sigma;
	private Vector2d emmitance;
	private Vector2d beta;
	private double gamma;
	private double velZ;
	private ArrayList<MacroParticle> MPs;
	private double angle;
	private Vector3d pos;
	
	private double numberP;

	private int posZ;
	private double forceConstant;
	
	public Bunch(AloHEP alohep, Beam beam)
	{
		this.alohep = alohep;
		this.beam = beam;
	}

	public Bunch(LuminosityCalc lumCalc, Beam beam)
	{
		this.lumCalc = lumCalc;
		this.beam = beam;
	}
	
	public void init(double angle)
	{
		
		emmitance = new Vector2d(0, 0);
		numberP = getData("NumParInBun");
		
		beta = new Vector2d(getData("BetaHor"), getData("BetaVer"));
		gamma = getData("EnBeam") * 1.0e9 / (beam.getParticle().getMass());
		if(hasData("EmNorHor"))
			emmitance.x = getData("EmNorHor") / gamma;
		else
			emmitance.x = getData("EmHor");
		
		if(hasData("EmNorVer"))
			emmitance.y = getData("EmNorVer") / gamma;
		else
			emmitance.y = getData("EmVer");
		double sigmaX = Math.sqrt(emmitance.x * beta.x);
		double sigmaY = Math.sqrt(emmitance.y * beta.y);
		sigma = new Vector3d(sigmaX, sigmaY, getData("BunLen"));
		this.angle = angle;
		pos = new Vector3d();
	}

	public void initMPs()
	{
		LuminosityCalc lc = lumCalc != null ? lumCalc : alohep.getLuminosityCalc();
		Vector3d IPScale = lc.getIPScale();
		Vector3d IPDiff = lc.getIPDiff();
		Vector2d PhaseSpaceScale = lc.getPhaseSpaceScale();

		MPs = new ArrayList<MacroParticle>();
		int MPcount = (int)lc.getSettingsData("NumMacPar");
		
		int sliceSize = (int)(sigma.z*IPScale.z/IPDiff.z)*2+4;
		slice = new Slice[sliceSize];
		for(int i = 0 ; i < sliceSize; i++)
		{
			slice[i] = new Slice(lc, i-sliceSize/2);
		}
		Random rand = new Random();
		for(int i = 0; i < MPcount; i++)
		{
			Vector3d pos = new Vector3d();
			Vector3d vel = new Vector3d();
			
			int index;
						
			do {
				pos.z = rand.nextGaussian();
				
			}while(Math.abs(pos.z) >= IPScale.z);
			
			index = (int)(pos.z*sigma.z/IPDiff.z+sliceSize/2);

			pos.z = pos.z*sigma.z;
			
			if(!lc.getCheckbox("hourglass"))
			{

				do {
					pos.x = rand.nextGaussian();
				} while(Math.abs(pos.x) >= IPScale.x);

				pos.x*=sigma.x;
				
				do {
					pos.y = rand.nextGaussian();
				}while(Math.abs(pos.y) >= IPScale.y);
				
				pos.y*=sigma.y;
			}			
			else
			{
				
				Vector2d psX = new Vector2d(0,0);
				Vector2d psY = new Vector2d(0,0);
				
				do {
					psX.x = rand.nextGaussian();
				} while(Math.abs(psX.x) >= PhaseSpaceScale.x);

				do {
					psX.y = rand.nextGaussian();
				}while(Math.abs(psX.y) >= PhaseSpaceScale.y);
				
				do {
					psY.x = rand.nextGaussian();
				} while(Math.abs(psY.x) >= PhaseSpaceScale.x);

				do {
					psY.y = rand.nextGaussian();
				}while(Math.abs(psY.y) >= PhaseSpaceScale.y);
				
				psX.x *= Math.sqrt(beta.x*emmitance.x);
				psX.y *= Math.sqrt(emmitance.x/beta.x);

				psY.x *= Math.sqrt(beta.y*emmitance.y);
				psY.y *= Math.sqrt(emmitance.y/beta.y);
				
				double L = pos.z+posZ*IPDiff.z;

				pos.x = psX.x-psX.y*Math.abs(L);
				vel.x = psX.y;
				
				pos.y = psY.x-psY.y*Math.abs(L);
				vel.y = psY.y;
			}
			pos.z += posZ*IPDiff.z;	
			vel.z = velZ;
			if(lc.hasCrossingAngle())
			{
				Vector2d posZY = rotate(new Vector2d(pos.z,pos.y), angle);
				pos.z = posZY.x;
				pos.y = posZY.y;
				vel.y = vel.y*Math.cos(angle)-Math.abs(Math.sin(angle));
			}
			MacroParticle MP = new MacroParticle(pos, vel, gamma);
			slice[index].addMP(MP);
			MPs.add(MP);
			
		}
		forceConstant = -4*getParticle().getRad()/(MPcount*IPDiff.z);
	}

	public Vector2d rotate(Vector2d vec, double angle)
	{
		double x = vec.x*Math.cos(angle)-vec.y*Math.sin(angle);
		double y = vec.x*Math.sin(angle)+vec.y*Math.cos(angle);
		vec.x = x;
		vec.y = y;
		return vec;
	}
	public void update(double dt)
	{
		LuminosityCalc lc = lumCalc != null ? lumCalc : alohep.getLuminosityCalc();
		if(lc.getCheckbox("beamstrahlung"))
		{
			for(MacroParticle MP: MPs)
			{
				MP.updateGamma(getParticle(), dt);
			}
		}
		
		pos.set(0,0,0);
		for(MacroParticle MP: MPs)
		{
			MP.update(dt);
			pos.add(MP.getPos());
		}
		pos.multiply(1.0/MPs.size());

	}
	public void update(double dtKick, double dtDrift)
	{
		LuminosityCalc lc = lumCalc != null ? lumCalc : alohep.getLuminosityCalc();
		if(lc.getCheckbox("beamstrahlung"))
		{
			for(MacroParticle MP: MPs)
			{
				MP.updateGamma(getParticle(), dtKick);
			}
		}
		pos.set(0,0,0);
		for(MacroParticle MP: MPs)
		{
			MP.update(dtKick, dtDrift);
			pos.add(MP.getPos());
		}
		pos.multiply(1.0/MPs.size());
	}
	public ArrayList<MacroParticle> getMPs()
	{
		return MPs;
	}
	public double minSliceIndex()
	{
		return posZ-slice.length/2;
	}
	public double maxSliceIndex()
	{
		return posZ+slice.length/2;
	}
	public double getSettings(String key)
	{
		return alohep.getDataManager().getSettingsData().get(key);
	}
	public void updatePosZ(int z)
	{
		posZ +=z;
	}
	public double getData(String key)
	{
		return beam.getData(key);
	}
	
	public Vector3d getSigma()
	{
		return sigma;
	}
	
	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public Vector2d getEmmitance() {
		return emmitance;
	}

	public void setEmmitance(Vector2d emmitance) {
		this.emmitance = emmitance;
	}

	public Vector2d getBeta() {
		return beta;
	}

	public void setBeta(Vector2d beta) {
		this.beta = beta;
	}

	public Beam getBeam() {
		return beam;
	}

	public void setBeam(Beam beam) {
		this.beam = beam;
	}

	public Slice[] getSlice() {
		return slice;
	}

	public void setSlice(Slice[] slice) {
		this.slice = slice;
	}

	public void setSigma(Vector3d sigma) {
		this.sigma = sigma;
	}

	public int getPosZ() {
		return posZ;
	}

	public void setPosZ(int posZ) {
		this.posZ = posZ;
	}

	public Particle getParticle() {
		
		return beam.getParticle();
	}

	public boolean hasData(String str) {
		return beam.hasData(str);
	}

	public double getNumberP() {
		return numberP;
	}

	public void setNumberP(double numberP) {
		this.numberP = numberP;
	}

	public double getForceConstant() {
		return forceConstant;
	}

	public void setForceConstant(double forceConstant) {
		this.forceConstant = forceConstant;
	}

	public double getVelZ() {
		return velZ;
	}

	public void setVelZ(double velZ) {
		this.velZ = velZ;
	}

	public Vector3d getPos() {
		return pos;
	}

	public void setPos(Vector3d pos) {
		this.pos = pos;
	}
}
