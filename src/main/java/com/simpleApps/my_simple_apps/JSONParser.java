package com.simpleApps.my_simple_apps;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.eclipsesource.json.Json;

public class JSONParser {

	public JSONParser() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		parser.readPage(1);

	}
	
	void readPage(int page) throws IOException {
	    URL url = new URL("https://www.hurtowniakarm.pl/karma-dla-psow/sucha-karma/karma-wg-smaku/bez-kurczaka/page:" + page);

	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    connection.setDoOutput(true);
	    connection.setRequestMethod("POST");

	    try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
	        // no need to post any data for this page
	        writer.write("");
	    }

	    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        try (Reader reader = new InputStreamReader(connection.getInputStream())) {
	        	
	            String html = Json
	                .parse(reader)
	                .asObject()
	                .getString("name", "");

	            Elements links = Jsoup
	                .parse(html)
	                .body().select("a[href*=/psy-2/]");

	            for (Element link : links) 
	                System.out.println("Link: " + link.text());
	        }
	    } else {
	        // handle HTTP error code.
	    	System.out.println("Error");
	    }
	}

}
