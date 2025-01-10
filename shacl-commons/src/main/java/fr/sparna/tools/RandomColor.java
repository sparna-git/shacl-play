package fr.sparna.tools;

import java.awt.Color;
import java.util.Random;

public class RandomColor {
	
	
	public String getColor() {
		
		Random randomGenerator = new Random();
		
		// create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = randomGenerator.nextInt(0xffffff + 1);

        // format it as hexadecimal string (with hashtag and leading zeros)
        String colorCode = String.format("%06x", nextInt);
        
		return colorCode;
	}

}
