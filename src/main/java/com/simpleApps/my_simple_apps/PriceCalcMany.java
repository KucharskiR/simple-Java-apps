package com.simpleApps.my_simple_apps;

import java.util.Scanner;

/**
 * PriceCalc
 *
 */
public class PriceCalcMany 
{
    public static void main( String[] args )
    {
    	while(true) {
    	// Create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);
        
        // Prompt the user to enter a double value
        System.out.print("Enter VAT (eg. 8%): ");
        
        // Read the user's input as a double
        double vat = scanner.nextDouble()/100;
        
     // Prompt the user to enter a double value
        System.out.print("Enter percent for gross costs (eg. 25%): ");
        
     // Read the user's input as a double
        double inputPercentGrossCosts = scanner.nextDouble()/100;
        
        System.out.print("Enter percent for net costs (eg. 25%): ");
        double inputPercentNetCosts = scanner.nextDouble()/100;
        
       // System.out.print("Enter percent profit (eg. 20%): ");
        double inputPercentProfit = 0;
        
        double inputNetPrice;
        while (true) {
        	System.out.print("Enter net price (eg. 1,46zł): ");
        	inputNetPrice = scanner.nextDouble();
        	if (inputNetPrice > 0)
        		break;
        }
        
        // Vat 
//        double vat = 0.08;
//        double vat = 0.23;
        
        System.out.printf("Vat: %2.0f%%  \n", vat * 100);
        
        // Value in % of increase profit percent every while step
        int profitPercentIncrease = 1;
        inputPercentProfit = -0.01;
        
        // Value in % of maximal profit percent to calculate in while loop
        int maxProfitPercentToCalculate = 15;
        
		while ((inputPercentProfit += (double) profitPercentIncrease / 100) < ((double) maxProfitPercentToCalculate / 100)) {

			double tempPrice;
			double error = 1;
			double finalPrice = inputNetPrice * 1.5;

			while (error > 0.001) {
				tempPrice = finalPrice;
				finalPrice = finalPrice * (inputPercentProfit + inputPercentGrossCosts / 1.23 + inputPercentNetCosts)
						+ inputNetPrice;
				finalPrice *= (1 + vat);
				error = Math.abs(finalPrice - tempPrice);

//				System.out.printf("Error: %.4f \t Price: %.2fzł\n", error, finalPrice);
			}

			System.out.printf("Profit percent: %.0f%% \t" 
								+ "Result gross: %.2fzł \t " 
								+ "Multiplier gross: %.3f\n",
								inputPercentProfit*100, finalPrice, finalPrice / (inputNetPrice * (1 + vat)));
		}
	}
}
}
