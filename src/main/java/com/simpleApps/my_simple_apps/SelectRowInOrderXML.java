package com.simpleApps.my_simple_apps;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SelectRowInOrderXML {

	public SelectRowInOrderXML() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		 try {
	            // Load the XML file
	            File inputFile = new File("C:\\Users\\Dell\\Downloads\\XML__zamwienia_2023-10-20_09_27.xml");
	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	            Document doc = dBuilder.parse(inputFile);

	            // Specify the order id and row name
	            String orderId = "593937580";

	            // Select the specified row in the specified order
	            selectRowInOrder(doc, orderId);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	private static void selectRowInOrder(Document doc, String orderId) {
        NodeList orderList = doc.getElementsByTagName("order");

        for (int i = 0; i < orderList.getLength(); i++) {
            Node orderNode = orderList.item(i);

            if (orderNode.getNodeType() == Node.ELEMENT_NODE) {
                Element orderElement = (Element) orderNode;

                // Get the order id
                String currentOrderId = orderElement.getElementsByTagName("order_id").item(0).getTextContent();

                // Check if the current order matches the specified order id
                if (currentOrderId.equals(orderId)) {
                    // Select the specified row in the rows of the current order
                    NodeList rows = orderElement.getElementsByTagName("rows");
                    System.out.println(orderElement.getChildNodes());
                    System.out.printf("Debug rows: " +  rows.item(0));
                    if (rows.getLength() > 0) {
                        Element rowsElement = (Element) rows.item(0);

                        NodeList rowList = rowsElement.getElementsByTagName("row");
                        System.out.println(rowList.getLength());
                        System.out.println(rowList.item(0).toString());
                        for (int j = 0; j < rowList.getLength(); j++) {
                            Element rowElement = (Element) rowList.item(j);
                            System.out.println(rowElement.toString());

                            // Get the row name
//                            String currentRowName = rowElement.getAttribute("name");
                            String rowName = rowElement.getElementsByTagName("name").item(0).getTextContent();

                            // Check if the current row matches the specified row name
                                // Perform operations on the selected row
                                System.out.println("Selected Order ID: " + orderId);
                                System.out.println("Selected Row Name: " + rowName);
                                // Add your logic here
                        }
                    }
                }
            }
        }

        System.out.println("Order ID or Row Name not found.");
    }

}
