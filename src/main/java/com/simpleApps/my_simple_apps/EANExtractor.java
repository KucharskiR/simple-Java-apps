package com.simpleApps.my_simple_apps;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EANExtractor {

	private static Set<String> visitedUrls = new HashSet<>();
	private static int eanCounter = 0;
	private static int productCodeCounter = 0;

	public static void main(String[] args) throws InterruptedException {
//		String startingUrl = "https://www.hurtowniakarm.pl/psy-2/canun-brio-activ-dla-psow-aktywnych-15kg";
		String baseUrl = "https://www.hurtowniakarm.pl"; // Replace with the base URL of the HTML page
		
		String baseName = "podklady-higieniczne-dla-psa";
		
		String csvEanOutputFile = "psy-akcesoria-i-suplementy-"
					+ baseName
					+ "-Eans.csv";
		String csvProductCodeOutputFile = "psy-akcesoria-i-suplementy-"
					+ baseName
					+ "-ProductCodes.csv";
		String htmlAddress = "https://www.hurtowniakarm.pl/karma-dla-psow/akcesoria-i-suplementy/"
				+ baseName
				+ "/page:"; // Replace with the actual HTML page address
		int pages = 1;
		
		LinkedHashSet<String> matchingLinks = getLinks(pages, baseUrl, htmlAddress);

//		for (String link : matchingLinks) {
//			System.out.println(link.toString());
//		}
		findAndExportEANs(csvEanOutputFile, csvProductCodeOutputFile, matchingLinks);

	}
	
	public static LinkedHashSet<String> getLinks(int pages, String baseUrl, String htmlAddress) throws InterruptedException {

		LinkedHashSet<String> matchingLinks = new LinkedHashSet<>();
		
		for (int page = 1; page <= pages; page++) {

		String htmlLink = htmlAddress + String.valueOf(page);
			// ArrayList to store the matching links
//		ArrayList<String> matchingLinks = new ArrayList<>();

			try {
				// Connect to the HTML page and retrieve its content
				Document document = Jsoup.connect(htmlLink).userAgent(
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
						.maxBodySize(0).get();

				// Select all links in the HTML page
//				Elements links = document.select("a[href*=/koty-2/]");
				Elements links = document.select("a[data-type=product-url]");
//				Elements links = document.select("a[href]");

//			System.out.println(document.toString());

				// Search for links containing the specified fragment
				for (Element link : links) {
					String href = link.attr("href");
//				if (href.contains("psy-2")) {
					// Add the matching link to the ArrayList
					matchingLinks.add(baseUrl + href);
//				}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Print or process the matching links
//		for (String link : matchingLinks) {
//			System.out.println("Matching Link: " + link);
//		}
//		
		System.out.println(matchingLinks.size());
		TimeUnit.SECONDS.sleep(2);
		}
		return matchingLinks;
	}

	private static void findAndExportEANs(String csvEansFilename, String csvProductCodesFilename,
			LinkedHashSet<String> links) throws InterruptedException {

		for (String link : links) {

			if (!visitedUrls.contains(link)) {
				visitedUrls.add(link);

				try {
					Document document = Jsoup.connect(link).get();
					Elements productCodesElements = document.select("strong[data-type=product-code]");
					Elements eanElements = document.getElementsMatchingOwnText("\\b\\d{13,}\\b");

					if (eanElements.isEmpty()) {
						writeProductCodesToCSV(productCodesElements, csvProductCodesFilename);
						System.out.println(productCodesElements.toString() + "\t" + link.toString());
					} else {
						writeEANsToCSV(eanElements, csvEansFilename);
						System.out.println(eanElements);
					}

				} catch (IOException e) {
					System.err.println("Error: Unable to fetch or parse the HTML content from " + link);
				}
			}
			TimeUnit.SECONDS.sleep(5);
		}
	}

	private static void writeEANsToCSV(Elements eanElements, String csvFilename) {
		try (FileWriter csvWriter = new FileWriter(csvFilename, true)) {
			for (Element element : eanElements) {
				String ean = element.text();
				csvWriter.append(ean).append(", ");
				eanCounter++;
				System.out.println(eanCounter);
			}
		} catch (IOException e) {
			System.err.println("Error writing EANs to CSV: " + e.getMessage());
		}
	}
	
	private static void writeProductCodesToCSV(Elements productCodeElements, String csvFilename) {
		try (FileWriter csvWriter = new FileWriter(csvFilename, true)) {
			for (Element element : productCodeElements) {
				String productCode = element.text();
				csvWriter.append(productCode).append(", ");
				productCodeCounter++;
				System.out.println(productCodeCounter);
			}
		} catch (IOException e) {
			System.err.println("Error writing ProductCodess to CSV: " + e.getMessage());
		}
	}

}
