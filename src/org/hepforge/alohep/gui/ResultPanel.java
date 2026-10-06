package org.hepforge.alohep.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.hepforge.alohep.AloHEP;
import org.hepforge.alohep.calc.MathHelper;
import org.hepforge.alohep.database.ParticleData;

public class ResultPanel extends JPanel{

	private static final long serialVersionUID = -8017066782971344586L;
	private AloHEP alohep;

	public ResultPanel(AloHEP alohep)
	{
		this.alohep = alohep;
	}
	public void finalResults() {
	    
        DecimalFormat format = new DecimalFormat("0.###");
		BorderLayout resultPanelLayout = new BorderLayout();

		setLayout(resultPanelLayout);

        JPanel luminosityPanel = new JPanel();  
		BoxLayout luminosityLayout = new BoxLayout(luminosityPanel, BoxLayout.Y_AXIS);
		luminosityPanel.setLayout(luminosityLayout);

		VariablePanel sqrtS = new VariablePanel(alohep, "sqrtS", false);
		sqrtS.setValue(MathHelper.parseToScientificNotation(alohep.getLuminosityCalc().getSqrtS()));	
    	luminosityPanel.add(sqrtS);
    	
    	VariablePanel noLum = new VariablePanel(alohep, "noLum", false);
    	noLum.setValue( MathHelper.parseToScientificNotation(alohep.getLuminosityCalc().getLuminosityRaw()));	
    	luminosityPanel.add(noLum);
    	
    	VariablePanel efLum = new VariablePanel(alohep, "efLum", false);
    	efLum.setValue( MathHelper.parseToScientificNotation(alohep.getLuminosityCalc().getLuminosity()));	
    	luminosityPanel.add(efLum);
    	
    	VariablePanel reFac = new VariablePanel(alohep, "reFac", false);
    	reFac.setValue( format.format(efLum.getValue() / noLum.getValue()));	
    	luminosityPanel.add(reFac);
    	
        add(luminosityPanel,BorderLayout.NORTH);
        
        JPanel particlePanel = new JPanel();
		GridLayout particleLayout = new GridLayout(1,2);
		particlePanel.setLayout(particleLayout);
		
        JPanel leftPanel = new JPanel();
		BoxLayout leftLayout = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);

        leftPanel.setLayout(leftLayout);
        JLabel leftParticleName = new JLabel(alohep.getMainPanel().getLeftPanel().getSelectedParticle());
        leftParticleName.setFont(MainPanel.TITLE_FONT);
        leftPanel.add(leftParticleName);
        
        VariablePanel sigmaXL = new VariablePanel(alohep, "sigmaX", false);
    	VariablePanel sigmaYL = new VariablePanel(alohep, "sigmaY", false);
    	sigmaXL.setValue(MathHelper.parseToScientificNotation(alohep.getLuminosityCalc().getBunchL().getSigma().x));
    	sigmaYL.setValue(MathHelper.parseToScientificNotation(alohep.getLuminosityCalc().getBunchL().getSigma().y));
    	leftPanel.add(sigmaXL);
    	leftPanel.add(sigmaYL);
    	
        if(alohep.getMainPanel().getLeftPanel().getParticleData().getType() == ParticleData.Type.LINEAR)
        {
        	VariablePanel disX = new VariablePanel(alohep, "disX", false);
        	VariablePanel disY = new VariablePanel(alohep, "disY", false);
        	
        	double[]dis = alohep.getLuminosityCalc().getDisruption(alohep.getLuminosityCalc().getBunchR(), alohep.getLuminosityCalc().getBunchL());
            
        	disX.setValue(MathHelper.parseToScientificNotation(dis[0]));
        	disY.setValue(MathHelper.parseToScientificNotation(dis[1]));
        	
        	leftPanel.add(disX);
        	leftPanel.add(disY);
        }
        else
        {
        	VariablePanel bbTunX = new VariablePanel(alohep, "bbTunX", false);
        	VariablePanel bbTunY = new VariablePanel(alohep, "bbTunY", false);
        	
            double[]bbTun = alohep.getLuminosityCalc().getBeamBeam(alohep.getLuminosityCalc().getBunchR(), alohep.getLuminosityCalc().getBunchL());
            
            bbTunX.setValue(MathHelper.parseToScientificNotation(bbTun[0]));
            bbTunY.setValue(MathHelper.parseToScientificNotation(bbTun[1]));
        	
        	leftPanel.add(bbTunX);
        	leftPanel.add(bbTunY);
        }
        particlePanel.add(leftPanel);
        
        JPanel rightPanel = new JPanel();
		BoxLayout rightLayout = new BoxLayout(rightPanel, BoxLayout.Y_AXIS);
        rightPanel.setLayout(rightLayout);
        
        JLabel rightParticleName = new JLabel(alohep.getMainPanel().getRightPanel().getSelectedParticle());
        rightParticleName.setFont(MainPanel.TITLE_FONT);
        rightPanel.add(rightParticleName); 
        
        VariablePanel sigmaXR = new VariablePanel(alohep, "sigmaX", false);
    	VariablePanel sigmaYR = new VariablePanel(alohep, "sigmaY", false);
    	sigmaXR.setValue(MathHelper.parseToScientificNotation(alohep.getLuminosityCalc().getBunchR().getSigma().x));
    	sigmaYR.setValue(MathHelper.parseToScientificNotation(alohep.getLuminosityCalc().getBunchR().getSigma().y));
    	rightPanel.add(sigmaXR);
    	rightPanel.add(sigmaYR);
    	
        if(alohep.getMainPanel().getRightPanel().getParticleData().getType() == ParticleData.Type.LINEAR)
        {
        	VariablePanel disX = new VariablePanel(alohep, "disX", false);
        	VariablePanel disY = new VariablePanel(alohep, "disY", false);
        	
        	double[]dis = alohep.getLuminosityCalc().getDisruption(alohep.getLuminosityCalc().getBunchL(), alohep.getLuminosityCalc().getBunchR());
            
        	disX.setValue(MathHelper.parseToScientificNotation(dis[0]));
        	disY.setValue(MathHelper.parseToScientificNotation(dis[1]));
        	
        	rightPanel.add(disX);
        	rightPanel.add(disY);
        }
        else
        {
        	VariablePanel bbTunX = new VariablePanel(alohep, "bbTunX", false);
        	VariablePanel bbTunY = new VariablePanel(alohep, "bbTunY", false);
        	
            double[]bbTun = alohep.getLuminosityCalc().getBeamBeam(alohep.getLuminosityCalc().getBunchL(), alohep.getLuminosityCalc().getBunchR());
            
            bbTunX.setValue(MathHelper.parseToScientificNotation(bbTun[0]));
            bbTunY.setValue(MathHelper.parseToScientificNotation(bbTun[1]));
        	
        	rightPanel.add(bbTunX);
        	rightPanel.add(bbTunY);
        }
        particlePanel.add(rightPanel);
        add(particlePanel, BorderLayout.SOUTH);
        
        JFrame frame = new JFrame();
        frame.setContentPane(this);
        frame.setTitle("FINAL RESULTS");
        frame.setSize(600,370);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);

	}
}
