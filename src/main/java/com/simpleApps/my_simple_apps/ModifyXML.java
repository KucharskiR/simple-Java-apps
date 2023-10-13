package com.simpleApps.my_simple_apps;

import java.io.File;

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

public class ModifyXML {

	public ModifyXML() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			// Load the XML file
			File inputFile = new File("c:\\Users\\Dell\\Downloads\\BL__Produkty__domylny_XML_2023-10-13_11_05.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			
			// Values
			String skuValue = "100911";
			String category = "Szampony i ochrona";

			// Find products by SKU and update category_name
			updateCategoryName(doc, skuValue, category);

			// Save the changes back to the XML file
			saveChanges(doc, "c:\\Users\\Dell\\Downloads\\BL__Produkty__domylny_XML_2023-10-13_11_05.xml");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void updateCategoryName(Document doc, String skuValue, String newCategoryName) {
		NodeList productList = doc.getElementsByTagName("product");

		for (int i = 0; i < productList.getLength(); i++) {
			Node productNode = productList.item(i);

			if (productNode.getNodeType() == Node.ELEMENT_NODE) {
				Element productElement = (Element) productNode;

				// Get the sku value
				String sku = productElement.getElementsByTagName("sku").item(0).getTextContent();

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

	private static void saveChanges(Document doc, String outputFile) {
		try {
			// Write the updated document to a new XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outputFile));
			transformer.transform(source, result);

			System.out.println("XML file updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
