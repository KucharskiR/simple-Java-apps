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

public class XMLModificator {

	public XMLModificator() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			// Load the XML file
			File inputFile = new File("c:\\Users\\Dell\\Downloads\\"
					+ "oferta-produktow-pelna.xml");
			File outputFile = new File("c:\\Users\\Dell\\Downloads\\"
					+ "BL__Produkty__domylny_XML_2023-10-19_10_48.xml");
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			
			DocumentBuilderFactory dbFactoryOut = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilderOut = dbFactoryOut.newDocumentBuilder();
			Document docOut = dBuilderOut.parse(outputFile);
			
			// Get list of product nodes of xml file
			NodeList productList = doc.getElementsByTagName("product");
			
			for (int i = 0; i < productList.getLength(); i++) {
				
				Node productNode = productList.item(i);
				Element productElement = (Element) productNode;
				String name = productElement.getElementsByTagName("name").item(0).getTextContent();
				
				
				String value = null;
//				if (productNode.getNodeType() == Node.ELEMENT_NODE) {
					value = productElement.getElementsByTagName("producer").item(0).getTextContent();
//				}

				// Values
				String find = name;
				String change = value;

				// Find products by SKU and update category_name
				updateXMLNodeValue(docOut, find, change, "name", "manufacturer_name");
				
				// Print progress in %
				System.out.printf("Progress: %.2f%% \n", (double) i * 100 / (double) productList.getLength() );
			}

				// Save the changes back to the XML file
				saveChanges(docOut, outputFile);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void updateXMLNodeValue(Document doc, String changedValue, String newValue, 
																	String searchBy, String change) {
		NodeList productList = doc.getElementsByTagName("product");

		for (int i = 0; i < productList.getLength(); i++) {
			Node productNode = productList.item(i);

			if (productNode.getNodeType() == Node.ELEMENT_NODE) {
				Element productElement = (Element) productNode;

				// Get the changedValue 
				String value = productElement.getElementsByTagName("name").item(0).getTextContent();

				// Check if the changedValue matches
				if (value.equals(changedValue)) {
					// Update the new value
					Element categoryNameElement = (Element) productElement.getElementsByTagName("manufacturer_name")
							.item(0);
					categoryNameElement.setTextContent(newValue);
					break; // Break the loop since we found and updated the desired product
				}
			}
		}
	}

	private static void saveChanges(Document doc, File outputFile) {
		try {
			// Write the updated document to a new XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outputFile);
			transformer.transform(source, result);

			System.out.println("XML file updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
