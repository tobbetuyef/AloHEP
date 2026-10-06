package org.hepforge.alohep.calc;

import org.hepforge.alohep.AloHEP;
import org.hepforge.alohep.database.AcceleratorData;

public class Beam {

	private AloHEP alohep;
	private Particle particle;

	private AloHEP.PanelType panelType;
	private AcceleratorData accData;
	public Beam(AloHEP alohep, AloHEP.PanelType panelType)
	{
		this.alohep = alohep;
		this.panelType = panelType;
		initBeam();
	}

	public Beam(Particle particle, AcceleratorData accData)
	{
		this.particle = particle;
		this.accData = accData;
	}
	public void initBeam()
	{
		if(panelType == AloHEP.PanelType.LEFT)
		{
			particle = alohep.getLuminosityCalc().getParticles().get(alohep.getParticleName(AloHEP.PanelType.LEFT));
			accData = alohep.getAccelatorData(AloHEP.PanelType.LEFT);
		}
		else
		{
			particle = alohep.getLuminosityCalc().getParticles().get(alohep.getParticleName(AloHEP.PanelType.RIGHT));
			accData = alohep.getAccelatorData(AloHEP.PanelType.RIGHT);
		}
	}
	public Particle getParticle()
	{
		return particle;
	}
	public AcceleratorData getAccData()
	{
		return accData;
	}

	public double getData(String key)
	{
		if(accData == null)
		{
			if(panelType == AloHEP.PanelType.LEFT)
			{
				return alohep.getMainPanel().getLeftPanel().getParticleVarPanels().get(key).getValue();
			}
			else
			{
				return alohep.getMainPanel().getRightPanel().getParticleVarPanels().get(key).getValue();
			}
		}
		return accData.getVariables().get(key);
	}
	public boolean hasData(String key)
	{
		if(accData == null)
		{
			if(panelType == AloHEP.PanelType.LEFT)
			{
				return alohep.getMainPanel().getLeftPanel().hasParticleVarPanel(key);
			}
			else
			{
				return alohep.getMainPanel().getRightPanel().hasParticleVarPanel(key);
			}
		}
		return accData.getVariables().containsKey(key);
	}
}
