package org.hepforge.alohep.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.hepforge.alohep.AloHEP;

public class CheckPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private HashMap<String, JRadioButton> checkboxes;  
	public CheckPanel(AloHEP alohep)
	{
		checkboxes = new HashMap<String, JRadioButton>();
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		
		setLayout(layout);
		JLabel settingsLabel = new JLabel("Checkboxes");
		settingsLabel.setFont(MainPanel.STANDARD_FONT);
		add(settingsLabel);
		loadCheckPanels();
	}
	private void loadCheckPanels() 
	{
		checkboxes.put("simulation", new JRadioButton("Simulation", false));
		checkboxes.put("decay", new JRadioButton("Particle Decay", false));
		checkboxes.put("hourglass", new JRadioButton("Hourglass Effect", false));
		checkboxes.put("pinch", new JRadioButton("Pinch Effect", false));
		checkboxes.put("beamstrahlung", new JRadioButton("Beamstrahlung", false));
		checkboxes.get("simulation").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!((JRadioButton)e.getSource()).isSelected())
				{
					checkboxes.get("pinch").setSelected(false);
					checkboxes.get("hourglass").setSelected(false);
					checkboxes.get("beamstrahlung").setSelected(false);

				}
			}	
		});
		checkboxes.get("hourglass").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected())
					checkboxes.get("simulation").setSelected(true);
			}
			
		});
		checkboxes.get("pinch").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected())
					checkboxes.get("simulation").setSelected(true);
				else
					checkboxes.get("beamstrahlung").setSelected(false);

			}
			
		});
		checkboxes.get("beamstrahlung").addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JRadioButton)e.getSource()).isSelected())
				{
					checkboxes.get("simulation").setSelected(true);
					checkboxes.get("pinch").setSelected(true);

				}

			}
			
		});
		
		for(Entry<String, JRadioButton> en: checkboxes.entrySet())
		{
			en.getValue().setFont(MainPanel.STANDARD_FONT);
			add(en.getValue());
		}
		
	}
	public HashMap<String, JRadioButton> getCheckboxes() {
		return checkboxes;
	}
	public void setCheckboxes(HashMap<String, JRadioButton> checkboxes) {
		this.checkboxes = checkboxes;
	}
	
}
