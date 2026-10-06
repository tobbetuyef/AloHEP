package org.hepforge.alohep.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.hepforge.alohep.AloHEP;

public class MainPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	public static Font STANDARD_FONT = new Font("SansSerif", 0, 15);
	public static Font TITLE_FONT = new Font("SansSerif", 0, 22);
	public static Font BUTTON_FONT = new Font("SansSerif", 0, 16);
	public static Font UNIT_FONT = new Font("Monospaced", Font.BOLD, 16);

	private JPanel particlesPanel;
	private ParticlePanel leftPanel;
	private ParticlePanel rightPanel;
	private ControlPanel controlPanel;
	private JProgressBar pbar = null;
	public MainPanel(AloHEP alohep)
	{
		particlesPanel = new JPanel();
		particlesPanel.setLayout(new GridLayout(1,2));
				
		leftPanel = new ParticlePanel(alohep);
		rightPanel = new ParticlePanel(alohep);

		Border blackline = BorderFactory.createMatteBorder(1,1,2,1,Color.BLACK);
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		setBorder(blackline);
		particlesPanel.add(leftPanel);
		JScrollPane scroller = new JScrollPane(leftPanel);
		particlesPanel.add("Editor", scroller);
		this.setSize(this.getWidth()+1, this.getHeight());
		
		particlesPanel.add(rightPanel);		
		scroller = new JScrollPane(rightPanel);
		particlesPanel.add("Editor", scroller);
		this.setSize(this.getWidth()+1, this.getHeight());
		
		add(particlesPanel,BorderLayout.NORTH);
		
		controlPanel = new ControlPanel(alohep);
		controlPanel.setSize(this.getWidth()+1, this.getHeight());
		
		add(controlPanel,BorderLayout.CENTER);

		pbar = new JProgressBar();
		pbar.setMinimum(0);
		pbar.setMaximum(100);
		pbar.setVisible(false);
		add(pbar,BorderLayout.SOUTH);
	}
	
	  public void updateBar(int newValue) 
	  {
		  pbar.setValue(newValue);
	  }

	public JProgressBar getPbar() {
		return pbar;
	}

	public void setPbar(JProgressBar pbar) {
		this.pbar = pbar;
	}

	public ParticlePanel getLeftPanel() {
		return leftPanel;
	}

	public void setLeftPanel(ParticlePanel leftPanel) {
		this.leftPanel = leftPanel;
	}

	public ParticlePanel getRightPanel() {
		return rightPanel;
	}

	public void setRightPanel(ParticlePanel rightPanel) {
		this.rightPanel = rightPanel;
	}

	public ControlPanel getControlPanel() {
		return controlPanel;
	}

	public void setControlPanel(ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}
}
