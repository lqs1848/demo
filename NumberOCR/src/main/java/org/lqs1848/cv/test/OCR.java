package org.lqs1848.cv.test;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.lept;
import org.bytedeco.tesseract.TessBaseAPI;

public class OCR {
	TessBaseAPI api;
	Boolean isInit = false;

	public OCR() {
		api = new TessBaseAPI();
		String path = OCR.class.getClassLoader().getResource("").toExternalForm();
    	path = path.replaceFirst("file:", "");
		if ((path.indexOf(":") != -1 && path.startsWith("\\")) || path.startsWith("/")) 
			path = path.substring(1);
		if (api.Init(path, "chi_sim") != 0) {
			api = null;
		}
		api.SetVariable("tessedit_char_whitelist", "0123456789");
		//api.SetVariable("classify_bln_numeric_mode", "1");
	}

	public String getNumber(byte[] bts) {
		String result = "";
		synchronized (api) {
			try {
				PIX image = lept.pixReadMem(bts, bts.length);
				api.SetImage(image);
				try (BytePointer outText = api.GetUTF8Text()) {
					result = outText.getString().replaceAll(" ", "").replaceAll("\n", "");
					outText.deallocate();
				}
				lept.pixDestroy(image);
			} finally {
				api.Clear();
			}
		} // syn
		return result;
	}// method
	
	public String getNumber(String filepath) {
		String result = "";
		synchronized (api) {
			try {
				PIX image = lept.pixRead(filepath);
				api.SetImage(image);
				try (BytePointer outText = api.GetUTF8Text()) {
					result = outText.getString().replaceAll(" ", "").replaceAll("\n", "");
					outText.deallocate();
				}
				lept.pixDestroy(image);
			} finally {
				api.Clear();
			}
		} // syn
		return result;
	}// method

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (api != null) {
			api.End();
			api.close();
		}
	}

}
