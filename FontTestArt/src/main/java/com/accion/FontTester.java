package com.accion;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.ITessAPI.TessBaseAPI;
import net.sourceforge.tess4j.ITessAPI.TessPageIterator;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;
import net.sourceforge.tess4j.ITessAPI.TessPageSegMode;
import net.sourceforge.tess4j.ITessAPI.TessResultIterator;
import net.sourceforge.tess4j.TessAPI1;
import static net.sourceforge.tess4j.ITessAPI.TRUE;
import com.sun.jna.Pointer;

public class FontTester {
	
	private Tesseract tess = null;
	private BufferedImage bi = null;
	private final String tessdata = "C:\\Program Files\\Tesseract-OCR\\tessdata\\";
	
	public static void main(String[] args) {
		
		String pdfFilePath = args[0];
		File imageFile = ImageExtracter.getImageFromPDF(pdfFilePath);
		FontTester tester = new FontTester();
		BufferedImage bi = tester.getImage(imageFile.getAbsolutePath());
		Set<String> imageFonts = tester.getFontsFromImage(bi);
		Set<String> physicalFonts = tester.getSystemFonts();
		compareFonts(imageFonts,physicalFonts);
			
	}
	
	public  static void compareFonts(Set<String> imageFonts,Set<String> physicalFonts) {
		
		System.out.println("#########Image fonts############");
		imageFonts.forEach(c -> System.out.println(c));
		
		System.out.println("##########Physical fonts###########");
		physicalFonts.forEach(f -> System.out.printf("%s, ",f));
		System.out.println();
		List<String>missingFonts = missingFonts(imageFonts,physicalFonts);
		System.out.println("#######Missing fonts#################");
		missingFonts.forEach(ft -> System.out.println(ft));
	
	}
	
	private static List<String>missingFonts(Set<String> imgFonts,Set<String>sysFonts){
		/*
		 * Return missing fonts.
		 */
		List<String> list = new ArrayList<>();
		for(String str:imgFonts) {
			if(!sysFonts.contains(str)) {
				list.add(str);
			}
		}
		return list;
	}
	
	
	
	public Set<String> getFontsFromImage(BufferedImage bi)  {
		/*
		 * Prints the font information. using TessbaseAPI
		 * */
		//handle
		
		int bpp = bi.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(bi.getWidth() * bpp / 8.0);
        System.out.println(bpp+"   "+bytespp+"  "+bytespl);
		TessBaseAPI api = null;
		api = TessAPI1.TessBaseAPICreate();
		
		ByteBuffer buff = ImageIOHelper.convertImageData(bi);
		TessAPI1.TessBaseAPIInit2(api, tessdata, "eng",0);
		TessAPI1.TessBaseAPISetPageSegMode(api, TessPageSegMode.PSM_AUTO);
		TessAPI1.TessBaseAPISetImage(api, buff,bi.getWidth(),bi.getHeight(),bytespp,bytespl);
		TessAPI1.TessBaseAPIRecognize(api, null);
				
		TessResultIterator tri = TessAPI1.TessBaseAPIGetIterator(api);
		TessPageIterator tpi = TessAPI1.TessResultIteratorGetPageIterator(tri);
	
		TessAPI1.TessPageIteratorBegin(tpi);
		int Level = TessPageIteratorLevel.RIL_WORD;
		int ctr = 0;
		Set<String> fontSet = new HashSet<String>();
		do {			
			Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(tri, Level);
			String word = ptr.getString(0);
			TessAPI1.TessDeleteText(ptr);
			float confidence = TessAPI1.TessResultIteratorConfidence(tri, Level);
									
			IntBuffer boldB = IntBuffer.allocate(1);
            IntBuffer italicB = IntBuffer.allocate(1);
            IntBuffer underlinedB = IntBuffer.allocate(1);
            IntBuffer monospaceB = IntBuffer.allocate(1);
            IntBuffer serifB = IntBuffer.allocate(1);
            IntBuffer smallcapsB = IntBuffer.allocate(1);
            IntBuffer pointSizeB = IntBuffer.allocate(1);
            IntBuffer fontIdB = IntBuffer.allocate(1);
			String fontName = TessAPI1.TessResultIteratorWordFontAttributes(tri,boldB, italicB, underlinedB,
            monospaceB, serifB, smallcapsB, pointSizeB, fontIdB);
			boolean bold = boldB.get() == TRUE;
            boolean italic = italicB.get() == TRUE;
            boolean underlined = underlinedB.get() == TRUE;
            boolean monospace = monospaceB.get() == TRUE;
            boolean serif = serifB.get() == TRUE;
            boolean smallcaps = smallcapsB.get() == TRUE;
            int pointSize = pointSizeB.get();
            int fontId = fontIdB.get();
            fontSet.add(fontName);
			//System.out.println(word+"  "+fontName+" bold "+bold+": Italic:"+italic+"  :Serif"+serif+": fontID"+fontId);
			
		}while(TessAPI1.TessPageIteratorNext(tpi, Level) == TRUE);
		return fontSet;
	}	
	private  BufferedImage getImage(String path) {
		
		File file  = new File(path);
		BufferedImage bi = null;
		try {
			 bi = ImageIO.read(file);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bi;
		
	}
	private  Set<String>getSystemFonts(){
	/*
	 * Return system fonts.	
	 */
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Set<String> fontSet = new HashSet<>(Arrays.asList(fonts));
		return fontSet;		
	}
		

}
