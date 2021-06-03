package org.lqs1848.ocr.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.lept;
import org.bytedeco.tesseract.TessBaseAPI;

public class HelloOCR2 {
	
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		getNumber();
		System.out.println("time:"+(System.currentTimeMillis() - startTime));
	}
	
	public static String getNumber() throws IOException {
		BytePointer outText;
        TessBaseAPI api = new TessBaseAPI();
    	String path = HelloOCR2.class.getClassLoader().getResource("").toExternalForm();
    	path = path.replaceFirst("file:", "");
		if ((path.indexOf(":") != -1 && path.startsWith("\\")) || path.startsWith("/")) 
			path = path.substring(1);
    	System.out.println("path:"+path);
        if (api.Init(path, "chi_sim") != 0) {
            System.err.println("Could not initialize tesseract.");
            return null;
        }
        api.SetVariable("tessedit_char_whitelist", "0123456789");
        
        
        File f = new File("E:\\ocr.jpg");
        byte[] bts = getFileByteArray(f);
        PIX image = lept.pixReadMem(bts, bts.length);
        api.SetImage(image);
        outText = api.GetUTF8Text();
        System.out.println("OCR output:\n" + outText.getString());
        outText.deallocate();
        outText.close();
        lept.pixDestroy(image);
        api.Clear();
        
        image = lept.pixReadMem(bts, bts.length);
        api.SetImage(image);
        outText = api.GetUTF8Text();
        System.out.println("OCR output:\n" + outText.getString());
        outText.deallocate();
        outText.close();
        lept.pixDestroy(image);
        api.Clear();
        
        image = lept.pixReadMem(bts, bts.length);
        api.SetImage(image);
        outText = api.GetUTF8Text();
        System.out.println("OCR output:\n" + outText.getString());
        outText.deallocate();
        outText.close();
        lept.pixDestroy(image);
        api.Clear();
        
        
        api.End();
        api.close();
        return "ok";
	}
	
	
	public static byte[] getFileByteArray(File file) {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        byte[] buffer = null;
        try (FileInputStream fi = new FileInputStream(file)) {
            buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
