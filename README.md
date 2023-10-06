
# My simple Java apps

This repository contains my simple applications written in Java. 


## Apps list

 - [ImageResizeThumbnailator](https://github.com/KucharskiR/simple-Java-apps/blob/main/src/main/java/com/simpleApps/my_simple_apps/ImageResizeThumbnailator.java) - resizes all images in a given folder so that they have an output size (in mb) as indicated on the input. Resized files are saved in a separate defined folder
 - [ImageQualityResizer](https://github.com/KucharskiR/simple-Java-apps/blob/main/src/main/java/com/simpleApps/my_simple_apps/ImageQualityResizer.java) - similar to ImageResizeThumbnailator but manipulate only the quality of image (not resize)
 - [PriceCalc](https://github.com/KucharskiR/simple-Java-apps/blob/main/src/main/java/com/simpleApps/my_simple_apps/PriceCalc.java) - an application that calculates the gross final price of a product by taking into account the expected return on sales (e.g., 20% of the sales revenue) and percentage costs
 - [PriceCalcMany](https://github.com/KucharskiR/simple-Java-apps/blob/main/src/main/java/com/simpleApps/my_simple_apps/PriceCalcMany.java) - other version of PriceCalc. Do not enter expected return percent. App will calculate many final prices for values from 0% to 20% with step 1%

## Usage/Examples

```java
package com.simpleApps.my_simple_apps;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

public class ImageResizeThumbnailator {

	public ImageResizeThumbnailator() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		
		// Initial variables
		double targetSizeInMB = 1.9;
		int maxOutImageWidth = 8000;
		int maxOutImgHeight = 8000;
		
		// Input/output directories paths
		String inputDir = "";
		String outputDir = inputDir + "resized";

		File inputDirectory = new File(inputDir);
		File outputDirectory = new File(outputDir);

		resizeImages(inputDirectory, outputDirectory, maxOutImageWidth, maxOutImgHeight, targetSizeInMB);

	}
	
	  private static void resizeImages(File inputDirectory, File outputDirectory, int maxOutImageWidth, int maxOutImgHeight, double targetSizeInMB) {
		  if (!outputDirectory.exists()) {
	            outputDirectory.mkdirs();
	        }

	        for (File file : inputDirectory.listFiles()) {
	            if (file.isFile() && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg"))) {
	                try {
	                	// Read width and height of file image
	                	int imgWidth = ImageIO.read(file).getWidth();
	                	int imgHeight = ImageIO.read(file).getHeight();
	                	
	                	// Calculate compression factor
	                	double factor = calculateCompressionQuality(file, imgWidth, imgHeight, targetSizeInMB);
	                	
	                	// Calculate output image size
	                	int imgResultWidth = (int) (imgWidth * factor);
	                	int imgResultHeight = (int) (imgHeight * factor);
	                	
	                	// Check results out of max values
	                	if(imgResultWidth > maxOutImageWidth) 
	                		imgResultWidth = maxOutImageWidth;
	                	if(imgResultHeight > maxOutImgHeight)
	                		imgResultHeight = maxOutImgHeight;
	                	
	                	// Write image to file
	                	BufferedImage outputImage = resizeImage(ImageIO.read(file), imgResultWidth, imgResultHeight);
	                	ImageIO.write(outputImage, "jpg", new File(outputDirectory.toString(),file.getName()));
	                	
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	}

	static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight)
			throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Thumbnails.of(originalImage)
				.size(targetWidth, targetHeight)
				.outputFormat("jpg")
				.outputQuality(0.95)
				.toOutputStream(outputStream);
		byte[] data = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
		return ImageIO.read(inputStream);
	}

	private static double calculateCompressionQuality(File inputFile, int imgWidth, int imgHeight, double targetSizeInMB) {
		long targetSizeInBytes = (long) (targetSizeInMB * 1024 * 1024);
		long currentSizeInBytes = inputFile.length();
		
		// Calculate compression 
		double compression = (double) targetSizeInBytes / currentSizeInBytes;
		
		// Image area in pixels
		double imageArea = imgWidth * imgWidth;
		
		// Initializing resizing factor
		double factor = 0.99;
		
		// Initializing error 
		double error = compression - ((double) imgWidth * factor * (double) imgHeight * factor) / imageArea;
		
		// Loop for calculating final factor
		while (Math.abs(error) > 0.001) {
			
			double increment = 0.001;
			
			if (Math.abs(error) > 3)
				increment = 0.2;
			else if (Math.abs(error) > 1)
				increment = 0.1;
			else if (Math.abs(error) > 0)
				increment = 0.001;
			
			if (error < 0)
				factor -= increment;
			else
				factor += increment;

			error = compression - (imgWidth * factor * imgHeight * factor) / imageArea;
			System.out.printf("Error: %.4f \n", error);
		}
		
		// Print out input file name and result resizing factor
		System.out.printf("Input File: %s \t Factor: %.3f \n", inputFile.getName(), factor);
		return (double) factor;
	}

}

```


## 🚀 About Me
I am a Junior Java developer.


## 🛠 Skills
Java, C/C++, Embedded C, HTML, CSS...

