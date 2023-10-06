package com.simpleApps.my_simple_apps;

import java.util.Scanner;

/**
 * PriceCalc
 *
 */
public class PriceCalc 
{
    public static void main( String[] args )
    {
    	// Create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);
        
     // Prompt the user to enter a double value
        System.out.print("Enter percent for gross costs (eg. 25%): ");
        
     // Read the user's input as a double
        double inputPercentGrossCosts = scanner.nextDouble()/100;
        
        System.out.print("Enter percent for net costs (eg. 25%): ");
        double inputPercentNetCosts = scanner.nextDouble()/100;
        
        System.out.print("Enter percent profit (eg. 20%): ");
        double inputPercentProfit = scanner.nextDouble()/100;
        
        double inputNetPrice;
        
		while (true) {
			System.out.print("Enter net price (eg. 1,46zł): ");
			inputNetPrice = scanner.nextDouble();
			if (inputNetPrice > 0)
				break;
		}

//        System.out.println("Args: cost profit netPrice (eg: 25 20 1,46");
//        
//        double inputPercentCosts = Double.parseDouble(args[1]);
//        double inputPercentProfit = Double.parseDouble(args[0]);
//        double inputNetPrice = Double.parseDouble(args[2]);
        
        double tempPrice;
        double error = 1;
        double finalPrice = inputNetPrice*2;
        
        while(error > 0.0001) {
            tempPrice = finalPrice;
        	finalPrice = finalPrice*(inputPercentProfit + inputPercentGrossCosts/1.23 + inputPercentNetCosts) + inputNetPrice;
        	finalPrice *= 1.23;
        	error = Math.abs(finalPrice - tempPrice);
        	
        	System.out.printf("Error: %.4f \t Price: %.2fzł\n", error, finalPrice);
        }
        
        System.out.printf( "Result gross: %.2fzł \t Multiplier gross: %.3f", finalPrice, finalPrice/(inputNetPrice*1.23) );
    }
}
