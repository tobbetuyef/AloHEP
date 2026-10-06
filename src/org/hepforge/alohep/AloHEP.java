package org.hepforge.alohep;

import javax.swing.JFrame;

import org.hepforge.alohep.calc.LuminosityCalc;
import org.hepforge.alohep.database.AcceleratorData;
import org.hepforge.alohep.database.DataManager;
import org.hepforge.alohep.gfx.AnimationPanel;
import org.hepforge.alohep.gui.MainPanel;

public class AloHEP extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private DataManager dataManager;
	private MainPanel mainPanel;
	private LuminosityCalc luminosityCalc;
	private AnimationPanel animationPanel;
	public enum PanelType{LEFT, RIGHT}
	
	public AloHEP()
	{
		setDataManager(new DataManager());
		mainPanel = new MainPanel(this);
		setTitle("AloHEP");
		setContentPane(mainPanel);
		setVisible(true);
	}

	public AcceleratorData getAccelatorData(PanelType panelType)
	{
		if(panelType == PanelType.LEFT)
		{
			return mainPanel.getLeftPanel().getAccData();
		}
		else
			return mainPanel.getRightPanel().getAccData();
	}
	public String getParticleName(PanelType panelType)
	{
		if(panelType == PanelType.LEFT)
			return mainPanel.getLeftPanel().getSelectedParticleType();
		else
			return mainPanel.getRightPanel().getSelectedParticleType();
	}
	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}
	
	public LuminosityCalc getLuminosityCalc() {
		return luminosityCalc;
	}

	public void setLuminosityCalc(LuminosityCalc luminosityCalc) {
		this.luminosityCalc = luminosityCalc;
	}

	public AnimationPanel getAnimationPanel() {
		return animationPanel;
	}

	public void setAnimationPanel(AnimationPanel animationPanel) {
		this.animationPanel = animationPanel;
	}
}
