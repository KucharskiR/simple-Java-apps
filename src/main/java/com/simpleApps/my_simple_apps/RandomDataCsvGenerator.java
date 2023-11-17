package com.simpleApps.my_simple_apps;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomDataCsvGenerator {
	
	private static int rows = 10000;

	public RandomDataCsvGenerator() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

        String fileName = "generated_data.csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            // Generate header
//            writer.append("Column1,Column2,Column3,Column4,Column5,Column6,Column7,Column8,Column9,Column10,Column11,Column12,Column13,Column14");
//            writer.append("\n");

            Random random = new Random();

            // Generate data rows
            for (int i = 0; i < rows; i++) { // Change 100 to the number of rows you want
                for (int j = 0; j < 10; j++) {
                    double randomDouble = random.nextDouble() * 100; // Adjust the range if needed
                    writer.append(String.valueOf(randomDouble));
                    writer.append(",");
                }

                for (int k = 0; k < 4; k++) {
                    int randomInt = random.nextInt(2);
                    writer.append(String.valueOf(randomInt));
                    if (k < 3) {
                        writer.append(",");
                    }
                }

                writer.append("\n");
            }

            System.out.println("CSV file generated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }

	}

}
