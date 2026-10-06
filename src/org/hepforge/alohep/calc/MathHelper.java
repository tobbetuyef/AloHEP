package org.hepforge.alohep.calc;

import java.text.DecimalFormat;

public class MathHelper {

    public static String parseToScientificNotation(double value) {
        double temp = value;
        int cont = 0;
        final DecimalFormat DECIMAL_FORMATER = new DecimalFormat("0.000");
        if(temp > 1)
        {
	        while ((int)value >= 10 && value !=0) 
	        {
	        	value /= 10.0;
	            ++cont;
	        }
	        return String.valueOf(DECIMAL_FORMATER.format(value).replace(",", ".")) + (cont != 0 ? "E+" + cont : "");
        }
        else
        {
	        while ((int)value < 1 && value !=0) 
	        {
	        	value *= 10.0;
	            ++cont;
	        }
	        return String.valueOf(DECIMAL_FORMATER.format(value).replace(",", ".")) + (cont != 0 ? "E-" + cont : "");
        }
    }
}
