package com.accion;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class ImageExtracter {
	
	
	
	public static File getImageFromPDF(String path) {
		/*
		 * Read PDF file and returns
		 */
		File pngFile = null;
		File pdfFile = new File(path);
		BufferedImage bi = null;
		
		try {
			Random rand = new Random();
			PDDocument doc = PDDocument.load(pdfFile);
			PDFRenderer render = new PDFRenderer(doc);
			 bi = render.renderImage(0);
			String diskPath = getFileName(rand);
			System.out.println(diskPath);
			pngFile = new File(FontTester.tempPath+diskPath);
			System.out.println(pngFile.createNewFile());
			String tPath = pngFile.getAbsolutePath();
			 //ImageIO.wri
			//FileOutputStream fos = new FileOutputStream(tPath);
			 ImageIO.write(bi, "png",pngFile);
			 System.out.println("Image extracted");
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return pngFile;
		
	}
	private static String getFileName(Random rand) {
        //String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        //return "PNG_" + timeStamp + "_.png";
		return rand.nextInt(1000)+"temp.png";
        //return timeStamp + ".png";
    }
	
	}

