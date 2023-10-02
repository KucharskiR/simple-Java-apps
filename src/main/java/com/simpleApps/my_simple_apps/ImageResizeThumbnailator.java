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
		 File inputDirectory = new File("c:\\Users\\Dell\\Documents\\KucharskiR_projects\\Downloads\\AWICAM_LOGO\\");
	     File outputDirectory = new File("c:\\Users\\Dell\\Documents\\KucharskiR_projects\\Downloads\\AWICAM_LOGO\\resized\\");
//        BufferedImage originalImage = ImageIO.read(new File("c:\\Users\\Dell\\Documents\\KucharskiR_projects\\Downloads\\AWICAM_LOGO\\AWICAM_LOGO.jpg"));
//        BufferedImage outputImage = resizeImage(originalImage, 200, 200);
//        ImageIO.write(outputImage, "jpg", new File("c:\\Users\\Dell\\Documents\\KucharskiR_projects\\Downloads\\AWICAM_LOGO\\resized\\AWICAM_LOGO.jpg"));
	     double targetSizeInMB = 1.9;
	     int maxOutImageWidth = 8000;
	     int maxOutImgHeight = 8000;
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
		while (error > 0.001) {
			if (error < 0)
				factor -= 0.001;
			else
				factor += 0.001;

			error = compression - (imgWidth * factor * imgHeight * factor) / imageArea;
		}
		
		// Print out input file name and result resizing factor
		System.out.printf("Input File: %s \t Factor: %.3f \n", inputFile.getName(), factor);
		return (double) factor;
	}

}
