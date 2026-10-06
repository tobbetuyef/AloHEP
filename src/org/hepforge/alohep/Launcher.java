package org.hepforge.alohep;

import org.hepforge.alohep.calc.LuminosityCalc;

public class Launcher 
{

    public static void main(final String[] args) 
    {
    	
        final AloHEP alohep = new AloHEP();
		alohep.setLuminosityCalc(new LuminosityCalc(alohep));
        alohep.pack();
        alohep.setLocationRelativeTo(null);
        alohep.setDefaultCloseOperation(3);
        alohep.setVisible(true);
    }
}
