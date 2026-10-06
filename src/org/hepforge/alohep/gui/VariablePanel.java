package org.hepforge.alohep.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.hepforge.alohep.AloHEP;

public class VariablePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private JTextField varText;
	private JLabel titleLabel;
	private JLabel space = new JLabel("    ");
	public VariablePanel(AloHEP alohep,String key, boolean editable)
	{
		this(alohep,key);
		varText.setEditable(editable);
	}
	public VariablePanel(AloHEP alohep,String key)
	{	
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.X_AXIS));

		titleLabel = new JLabel(alohep.getDataManager().getTitle(key));
		labelPanel.add(space);
		labelPanel.add(titleLabel);
		setLayout(new GridLayout(1,2));
		add(labelPanel, BorderLayout.EAST);
		
		JPanel valuePanel = new JPanel();
		
		varText = new JTextField(8);
		varText.setEditable(false);
		titleLabel.setFont(MainPanel.STANDARD_FONT);
		varText.setEditable(true);
		varText.setFont(MainPanel.STANDARD_FONT);
		valuePanel.add(varText);
		
		JLabel unitLabel = new JLabel(String.format("%1$-5s",alohep.getDataManager().getUnit(key)));
		unitLabel.setFont(MainPanel.UNIT_FONT);
		valuePanel.add(unitLabel);
		add(valuePanel, BorderLayout.WEST);
	}
	public void setValue(double value)
	{
		varText.setText(value+"");
	}
	public void setValue(String value)
	{
		varText.setText(value);
	}
	public double getValue()
	{
		double d = 0;
		try {
		d = Double.parseDouble(varText.getText());
		}catch(Exception e) {};
		return d;
	}
	public JTextField getVarText() {
		return varText;
	}
	public void setVarText(JTextField varText) {
		this.varText = varText;
	}
}
