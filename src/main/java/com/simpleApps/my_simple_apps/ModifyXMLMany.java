package com.simpleApps.my_simple_apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModifyXMLMany {
	
	private static int counter = 0;
	private static int listsSize = 0;

	public ModifyXMLMany() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String filePath = "c:\\Users\\Dell\\Documents\\KucharskiR_projects\\simpleApps\\my-simple-apps\\csv\\";
		
		String baseName = "koty-akcesoria-i-suplementy-drapaki-dla-kota";
		
		String fileNameEans = baseName
							+ "-Eans"
							+ ".csv";
		String fileNameSkus = baseName
							+ "-ProductCodes"
							+ ".csv";
		File inputFile = new File("c:\\Users\\Dell\\Downloads\\BL__Produkty__domylny_XML_2023-10-13_14_10.xml");
		// Values
		String category = "Drapaki dla kotów";
		
		LinkedList<String> listEans = CSVReaderList(filePath, fileNameEans);
		LinkedList<String> listSkus = CSVReaderList(filePath, fileNameSkus);
		
		listsSize = listEans.size() + listSkus.size();
		
		System.out.printf("All list size: %d \n", listsSize);
		
		for (String findByValue : listEans) {
			ModifyXMLEans(inputFile, findByValue, category);
		}
		for (String findByValue : listSkus) {
			ModifyXMLSkus(inputFile, findByValue, category);
		}

	}
	
	private static LinkedList<String> CSVReaderList(String filePath, String fileName) {
		// Specify the path to your CSV file

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
			
			return list;

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filePath);
			e.printStackTrace();
			return list;
		}
	}
	
	private static void ModifyXMLEans(File inputFile, String findByValue, String changedValue){
		try {
			// Load the XML file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			
			// Find products by SKU and update category_name
			updateCategoryName(doc, findByValue, changedValue, "ean");

			// Save the changes back to the XML file
			saveChanges(doc, inputFile);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private static void ModifyXMLSkus(File inputFile, String findByValue, String changedValue){
		try {
			// Load the XML file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			
			// Find products by SKU and update category_name
			updateCategoryName(doc, findByValue, changedValue, "sku");
			
			// Save the changes back to the XML file
			saveChanges(doc, inputFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void updateCategoryName(Document doc, String skuValue, String newCategoryName, String findBy) {
		NodeList productList = doc.getElementsByTagName("product");

		for (int i = 0; i < productList.getLength(); i++) {
			Node productNode = productList.item(i);

			if (productNode.getNodeType() == Node.ELEMENT_NODE) {
				Element productElement = (Element) productNode;

				// Get the sku value
				String sku = productElement.getElementsByTagName(findBy).item(0).getTextContent();

				// Check if the sku matches
				if (sku.equals(skuValue)) {
					// Update the category_name
					Element categoryNameElement = (Element) productElement.getElementsByTagName("category_name")
							.item(0);
					categoryNameElement.setTextContent(newCategoryName);
					break; // Break the loop since we found and updated the desired product
				}
			}
		}
	}

	private static void saveChanges(Document doc, File file) {
		try {
			// Write the updated document to a new XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);

			System.out.printf("XML file updated successfully! Number: %d \t Percent: %2.1f%% \n", ++counter, (double) 100*counter/listsSize);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
