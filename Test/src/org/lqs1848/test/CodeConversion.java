package org.lqs1848.test;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class CodeConversion {

	public static void main(String[] args) {
		// 系统的默认编码是GBK
		System.out.println("Default Charset=" + Charset.defaultCharset());
		String t = "一梦千尋丿";
		System.out.println(repGB2312(t));
	}

	final static String repGB2312(final String s) {
		StringBuffer sb = new StringBuffer();
		Charset cs = Charset.forName("GB2312");
		CharsetEncoder encode = cs.newEncoder();
		for (int i = 0; i < s.length(); i++) {
			char x = s.charAt(i);
			if(encode.canEncode(x)) {
				sb.append(x);
			}else {
				sb.append("□");
			}
		}
		return sb.toString();
	}

}
