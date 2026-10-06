package org.hepforge.alohep.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.hepforge.alohep.AloHEP;
import org.hepforge.alohep.calc.Bunch;
import org.hepforge.alohep.calc.MathHelper;

public class SettingsPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private AloHEP alohep;
	private HashMap<String, VariablePanel> settingsVarPanels;
	private JButton matchedBeams;
	
	public SettingsPanel(AloHEP alohep)
	{
		this.alohep = alohep;
		settingsVarPanels = new HashMap<String, VariablePanel>();
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		
		setLayout(layout);
		JLabel settingsLabel = new JLabel("Simulation Settings");
		settingsLabel.setFont(MainPanel.STANDARD_FONT);
		add(settingsLabel);
		
		loadSettingsPanels();
		matchedBeams = new JButton("Matched Beams");
		matchedBeams.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				matchedBeamsSelected();
			}
		});
		matchedBeams.setFont(MainPanel.STANDARD_FONT);
		add(matchedBeams);
	}
	public void matchedBeamsSelected()
	{
		alohep.getMainPanel().getLeftPanel().setRadioButton("Custom");
		alohep.getMainPanel().getRightPanel().setRadioButton("Custom");
		
		alohep.getLuminosityCalc().init();
		Bunch bunchL = alohep.getLuminosityCalc().getBunchL();
		Bunch bunchR = alohep.getLuminosityCalc().getBunchR();

		double sigmaXmax = Math.max(bunchL.getSigma().x, bunchR.getSigma().x);
		double sigmaYmax = Math.max(bunchL.getSigma().y, bunchR.getSigma().y);
		double betaHorL = Math.pow(sigmaXmax, 2) / bunchL.getEmmitance().x;
		double betaVerL = Math.pow(sigmaYmax, 2) / bunchL.getEmmitance().y;
		
		double betaHorR = Math.pow(sigmaXmax, 2) / bunchR.getEmmitance().x;
		double betaVerR = Math.pow(sigmaYmax, 2) / bunchR.getEmmitance().y;
		alohep.getMainPanel().getLeftPanel().getParticleVarPanels().get("BetaHor").setValue(MathHelper.parseToScientificNotation(betaHorL));
		alohep.getMainPanel().getLeftPanel().getParticleVarPanels().get("BetaVer").setValue(MathHelper.parseToScientificNotation(betaVerL));
		alohep.getMainPanel().getRightPanel().getParticleVarPanels().get("BetaHor").setValue(MathHelper.parseToScientificNotation(betaHorR));
		alohep.getMainPanel().getRightPanel().getParticleVarPanels().get("BetaVer").setValue(MathHelper.parseToScientificNotation(betaVerR));

	}
	public void loadSettingsPanels()
	{
		for(Entry<String, Double> en: alohep.getDataManager().getSettingsData().entrySet())
		{
			VariablePanel panel = new VariablePanel(alohep, en.getKey());
			panel.setValue(en.getValue());
			settingsVarPanels.put(en.getKey(), panel);
			add(panel);
		}
	}
	public JButton getMatchedBeams() {
		return matchedBeams;
	}
	public void setMatchedBeams(JButton matchedBeams) {
		this.matchedBeams = matchedBeams;
	}
	public HashMap<String, VariablePanel> getSettingsVarPanels() {
		return settingsVarPanels;
	}
	public void setSettingsVarPanels(HashMap<String, VariablePanel> settingsVarPanels) {
		this.settingsVarPanels = settingsVarPanels;
	}
}
