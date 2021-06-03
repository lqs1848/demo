package org.lqs1848.ocr.test;

import java.io.File;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class HelloOCR {
	public static void main(String[] args) throws Exception {
		getNumber("E:\\ocr.jpg");
		getNumber("E:\\test.jpg");
	}
	
	public static String getNumber(String picPath) {
        File file = new File(picPath);
        ITesseract instance = new Tesseract();
        
        String lagnguagePath = HelloOCR.class.getClassLoader().getResource("").toExternalForm();
        lagnguagePath = lagnguagePath.replaceFirst("file:", "");
		if ((lagnguagePath.indexOf(":") != -1 && lagnguagePath.startsWith("\\")) || lagnguagePath.startsWith("/")) 
			lagnguagePath = lagnguagePath.substring(1);
    	System.out.println("path:"+lagnguagePath);
        //设置训练库的位置
        instance.setDatapath(lagnguagePath);

        //chi_sim ：简体中文， eng    根据需求选择语言库
        instance.setLanguage("chi_sim");
        //只识别数字
        instance.setTessVariable("tessedit_char_whitelist", "0123456789");
        String result = null;
        try {
            long startTime = System.currentTimeMillis();
            result =  instance.doOCR(file);
            long endTime = System.currentTimeMillis();
            System.out.println("Time is：" + (endTime - startTime) + " 毫秒");
        } catch (TesseractException e) {
            e.printStackTrace();
        }

        System.out.println("result: ");
        System.out.println(result);
        return "ok";
	}
	

}
