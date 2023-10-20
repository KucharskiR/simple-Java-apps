package com.simpleApps.my_simple_apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SaleVolume {

	// XML input file path/name
	private static String inputProductXMLFilePath = "c:\\Users\\Dell\\Downloads\\";
	private static String inputProductXMLFileName = "BL__Produkty__domylny_XML_2023-10-17_14_26.xml";
	
	public SaleVolume() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// CSV input file path/name
		String inputCSVFilePath = "c:\\Users\\Dell\\Documents\\KucharskiR_projects\\drop\\csv\\";
		String inputCSVFileName = "sprzedaz2.csv";
		
		// Generate linked list of CSV elements
		LinkedList<CSVRecordPOJO> csvList = CSVReaderListExport(inputCSVFilePath, inputCSVFileName);
		
		LinkedList<CSVRecordPOJO> csvListBig = searchBig(csvList);
		
		for (CSVRecordPOJO csvRecordPOJO : csvListBig) {
			System.out.println(csvRecordPOJO.toString());
		}
		
		double saleCumulate = 0;
		int saleVolumeCumulate = 0;
		
		for (CSVRecordPOJO salePOJO : csvList) {
			
			int saleVolume = Integer.parseInt(salePOJO.getBeforeVolume()) - Integer.parseInt(salePOJO.getAfterVolume());
			
			double sale = 0;
			
			if (saleVolume > 0) {
				sale = (double) saleVolume * getSkuPrice(salePOJO.getSku());
				saleCumulate += sale;
				saleVolumeCumulate += saleVolume;
			}
			System.out.printf("Sale volume: %d \t Sale: %.2f zł \t Price: %.2f zł \t SKU: %s \n", 
					saleVolume, 
					sale, 
					getSkuPrice(salePOJO.getSku()), 
					salePOJO.getSku());
		}
		System.out.printf("Sale volume cumulate: %d \t Sale cumulate: %.2f zł \n", saleVolumeCumulate, saleCumulate);

	}
	
	private static LinkedList<CSVRecordPOJO> searchBig(LinkedList<CSVRecordPOJO> csvList) {
		LinkedList<CSVRecordPOJO> outputList = new LinkedList<>();
		List<String> skus = new ArrayList<String>();
		
		for (CSVRecordPOJO csvRecordPOJO : csvList) {
			boolean set = true;
			if (!skus.isEmpty()) {
				for (String string : skus) {
					if (string.equals(csvRecordPOJO.getSku())){
						set = false;
						break;
					}
				}
				if (set)
					skus.add(csvRecordPOJO.getSku());
				
			} else
				skus.add(csvRecordPOJO.getSku());
		}
		
		for (String sku : skus) {
			CSVRecordPOJO newElement = new CSVRecordPOJO(null, null, "0", "0");
			for (CSVRecordPOJO csvRecordPOJO : csvList) {
				if (csvRecordPOJO.getSku().equals(sku)) {
					newElement.setDate(csvRecordPOJO.getDate());
					newElement.setSku(csvRecordPOJO.getSku());
					newElement.setResult(newElement.getResult() + csvRecordPOJO.getResult());
				}
			}
			outputList.add(newElement);
		}
		
		return outputList;
	}

	private static double getSkuPrice(String sku) {
		double outputPrice = 0;
		try {
			// Load the XML file
			File inputFile = new File(inputProductXMLFilePath + inputProductXMLFileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);

			// Get list of product nodes of XML file
			NodeList productList = doc.getElementsByTagName("product");

			for (int i = 0; i < productList.getLength(); i++) {
				Node productNode = productList.item(i);
				Element productElement = (Element) productNode;
				String skuRead = productElement.getElementsByTagName("sku").item(0).getTextContent();

				if (sku.equals(skuRead)) {

					String price = productElement.getElementsByTagName("price").item(0).getTextContent();
					outputPrice = Double.parseDouble(price);
//					System.out.printf("Price: %.2f zł \n", outputPrice);
					return outputPrice;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Price read error!");
		}
		return outputPrice;
	}

	private static LinkedList<CSVRecordPOJO> CSVReaderListExport(String filePath, String fileName) {
		// Specify the path to your CSV file
		
		LinkedList<CSVRecordPOJO> list = new LinkedList<CSVRecordPOJO>();
		
		try {
			// Create a Scanner object to read the file
			Scanner scanner = new Scanner(new File(filePath + fileName));
			
			while (scanner.hasNextLine()) {
				// Read the line
				String line = scanner.nextLine();
				
				// Split the line using ","
				String[] values = line.split(",");
				
				// Remove spaces from each value
				for (int i = 0; i < values.length; i += 4) {
					values[i] = values[i].trim();
					values[i+1] = values[i+1].trim();
					values[i+2] = values[i+2].trim();
					values[i+3] = values[i+3].trim();
					
					// Add to list. Remove empty values
					if (!values[i].equals(""))
						list.add(new CSVRecordPOJO(values[i], values[i+1], values[i+2], values[i+3]));
				}
				
			}
			// Print the values
			for (CSVRecordPOJO value : list) {
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

}

class CSVRecordPOJO {
	private String date;
	private String sku;
	private String beforeVolume;
	private String afterVolume;
	private int result;

	

	public CSVRecordPOJO(String date, String sku, String beforeVolume, String afterVolume) {
		super();
		this.date = date;
		this.sku = sku;
		this.beforeVolume = beforeVolume;
		this.afterVolume = afterVolume;
		this.result = Integer.parseInt(beforeVolume) - Integer.parseInt(afterVolume);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getBeforeVolume() {
		return beforeVolume;
	}

	public void setBeforeVolume(String beforeVolume) {
		this.beforeVolume = beforeVolume;
	}

	public String getAfterVolume() {
		return afterVolume;
	}

	public void setAfterVolume(String afterVolume) {
		this.afterVolume = afterVolume;
	}
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "CSVRecordPOJO [date=" + date + ", sku=" + sku + ", beforeVolume=" + beforeVolume + ", afterVolume="
				+ afterVolume + ", result=" + result + "]";
	}


}
