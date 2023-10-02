package com.simpleApps.my_simple_apps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

public class ImageQualityResizer {

	public ImageQualityResizer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		// Initial variables
		double targetSizeInMB = 1.9;
		String inputDir = "";
		String outputDir = "";

        File inputDirectory = new File(inputDir);
        File outputDirectory = new File(outputDir);

        resizeImages(inputDirectory, outputDirectory, targetSizeInMB);

	}
	
	 private static void resizeImages(File inputDirectory, File outputDirectory, double targetSizeInMB) {
	        if (!outputDirectory.exists()) {
	            outputDirectory.mkdirs();
	        }

	        for (File file : inputDirectory.listFiles()) {
	            if (file.isFile() && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg"))) {
	                try {
	                    resizeImage(file, outputDirectory, targetSizeInMB);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

	    private static void resizeImage(File inputFile, File outputDirectory, double targetSizeInMB)
	            throws IOException {
	        BufferedImage bufferedImage = ImageIO.read(inputFile);

	        double compressionQuality = calculateCompressionQuality(inputFile, targetSizeInMB);

	        File outputFile = new File(outputDirectory, inputFile.getName());
	        OutputStream os = new FileOutputStream(outputFile);
	        ImageWriter imageWriter = getImageWriter();

	        ImageWriteParam jpegParams = imageWriter.getDefaultWriteParam();
	        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	        jpegParams.setCompressionQuality((float) compressionQuality);

	        imageWriter.setOutput(ImageIO.createImageOutputStream(os));
	        imageWriter.write(null, new javax.imageio.IIOImage(bufferedImage, null, null), jpegParams);
	        imageWriter.dispose();
	    }

	    private static ImageWriter getImageWriter() {
	        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
	        if (writers.hasNext()) {
	            return writers.next();
	        } else {
	            throw new IllegalStateException("No writers found");
	        }
	    }

	    private static double calculateCompressionQuality(File inputFile, double targetSizeInMB) {
	        long targetSizeInBytes = (long) (targetSizeInMB * 1024 * 1024);
	        long currentSizeInBytes = inputFile.length();

	        return (double) (targetSizeInBytes / currentSizeInBytes) < 1 ? (targetSizeInBytes / currentSizeInBytes) : 1;
	    }
}
