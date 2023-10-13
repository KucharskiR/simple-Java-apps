package com.simpleApps.my_simple_apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class CSVReader {

	public CSVReader() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// Specify the path to your CSV file
		String filePath = "c:\\Users\\Dell\\Documents\\KucharskiR_projects\\simpleApps\\my-simple-apps\\csv\\";
		String fileName = "koty-akcesoria-i-suplementy-drapaki-dla-kota-Eans — kopia.csv";

		LinkedList<String> list = new LinkedList<String>();

		try {
			// Create a Scanner object to read the file
			Scanner scanner = new Scanner(new File(filePath + fileName));

			while (scanner.hasNextLine()) {
				// Read the line
				String line = scanner.nextLine();

				// Split the line using ","
				String[] values = line.split(",");

				// Remove spaces from each value
				for (int i = 0; i < values.length; i++) {
					values[i] = values[i].trim();

					// Add to list. Remove empty values
					if (!values[i].equals(""))
						list.add(values[i]);
				}

			}
			// Print the values
			for (String value : list) {
				System.out.println(value);
			}

			// Print list size
			System.out.println("List size: " + list.size());

			// Close the scanner
			scanner.close();

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filePath);
			e.printStackTrace();
		}
	}
}
