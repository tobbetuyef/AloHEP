package org.hepforge.alohep.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

import org.hepforge.alohep.AloHEP;
import org.hepforge.alohep.calc.LuminosityCalc;
import org.hepforge.alohep.gfx.AnimationPanel;
import org.hepforge.alohep.gfx.MainState;

public class ControlPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private SettingsPanel settingsPanel;
	private CheckPanel checkPanel;
	private JPanel calculatePanel;
	private JButton calculateButton;
	private boolean isProcessing = false;
	
	private AloHEP alohep;
	
	public ControlPanel(AloHEP alohep)
	{
		this.alohep = alohep;
		settingsPanel = new SettingsPanel(alohep);
		checkPanel = new CheckPanel(alohep);
		calculatePanel = new JPanel();
		calculateButton = new JButton("Calculate");
		calculateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				calculateButtonPressed();
			}
		});
		calculateButton.setPreferredSize(new Dimension(250, 100));
		calculateButton.setFont(MainPanel.BUTTON_FONT);
		Border border = BorderFactory.createEmptyBorder(50,20,50,20);
		calculatePanel.setBorder(border);
		FlowLayout layout = new FlowLayout();
		
		calculatePanel.setLayout(layout);
		
		calculatePanel.add(calculateButton);

		setLayout(new BorderLayout());
		add(settingsPanel,BorderLayout.WEST);
		add(checkPanel, BorderLayout.CENTER);
		add(calculatePanel,BorderLayout.EAST);
	}
	public void calculateButtonPressed()
	{
		try {
			alohep.getDataManager().saveSettingsToJSON();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		new Thread() {
			@Override
			public void run()
			{
				JProgressBar pb = alohep.getMainPanel().getPbar();
				if(!isProcessing)
				{

					setPanelEnabled(checkPanel, false);
					
					isProcessing = true;
					calculateButton.setText("Cancel");
					pb.setVisible(true);
					alohep.getLuminosityCalc().start();
					if(alohep.getLuminosityCalc().getCheckbox("simulation"))
						startAnimation();
					alohep.getLuminosityCalc().run();
				}
				pb.setVisible(false);
				pb.setValue(0);
				alohep.getLuminosityCalc().setRunning(false);
				while(!alohep.getLuminosityCalc().isFinished())
				{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(alohep.getLuminosityCalc().getCheckbox("simulation"))
				{
					alohep.getAnimationPanel().getDisplay().getFrame().dispose();
					alohep.getAnimationPanel().setRunning(false);
						
				}
				calculateButton.setText("Calculate");
				isProcessing = false;
				alohep.setLuminosityCalc(new LuminosityCalc(alohep));
				
				setPanelEnabled(alohep.getMainPanel().getLeftPanel(), true);
				setPanelEnabled(alohep.getMainPanel().getRightPanel(), true);
				setPanelEnabled(checkPanel, true);
			}

		}.start();

	}
	public void startAnimation()
	{
		alohep.setAnimationPanel(new AnimationPanel(alohep));
		MainState mainState = new MainState(alohep);
		alohep.getAnimationPanel().putState("MainState", mainState);
		alohep.getAnimationPanel().setState("MainState");
		alohep.getAnimationPanel().start();
	}
	public void setPanelEnabled(JPanel panel, boolean isEnabled)
	{
		Component[] components = panel.getComponents();
		for(Component component: components)
		{
			component.setEnabled(isEnabled);
		}
	}
	
	public SettingsPanel getSettingsPanel() {
		return settingsPanel;
	}
	public void setSettingsPanel(SettingsPanel settingsPanel) {
		this.settingsPanel = settingsPanel;
	}
	public CheckPanel getCheckPanel() {
		return checkPanel;
	}
	public void setCheckPanel(CheckPanel checkPanel) {
		this.checkPanel = checkPanel;
	}
}
