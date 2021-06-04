package org.lqs1848.test;

import java.util.Random;

/**
 * 加密数字
 */
public class NumberEncryption {

//	private static String baseSecret = "iIl";
	private static String baseSecret = "0aQ1bR2cS3dT4eU5fV6gW7hX8iY9jZAkBlCmDnEoFpGqHrIsJtKuLvMwNxOyPz";
	private static int len = baseSecret.length();
	private static Random ran = new Random();

	public static void main(String[] args) throws Exception {
		for (int i = 1; i < 999; i++) {
			int x = 123456 + i;
			String enc = encode(x);
			if (decode(enc) != x) {
				System.out.println("error");
			} else {
				System.out.println(x + " " + enc);
			}
		}
	}

	public static String encode(int id) {
		int x = id / len;
		int y = id % len;
		int r = ran.nextInt(baseSecret.length());
		String secret = getOffsetSecret(r);
		String code = encode(x, y, "", secret) + baseSecret.charAt(r);
		return code + getCheckCode(code);
	}

	public static String encode(int x, int y, String sb, String secret) {
		sb = secret.charAt(y) + sb;
		if (x == 0)
			return sb;
		return encode(x / len, x % len, sb, secret);
	}// mtehod

	public static int decode(String code) throws Exception {
		String checkStr = code.substring(code.length() - 1);
		code = code.substring(0, code.length() - 1);
		if(!checkStr.equals(getCheckCode(code)))
			throw new Exception("校验无法通过!");
		
		String offsetStr = code.substring(code.length() - 1);
		int offset = baseSecret.indexOf(offsetStr);
		String secret = getOffsetSecret(offset);

		code = code.substring(0, code.length() - 1);
		int r = 0;
		for (int i = 0; i < code.length(); i++) {
			int fd = code.length() - i - 1;
			int x = secret.indexOf(code.charAt(i));
			if (fd != 0) {
				int cur = x * len;
				for (int j = 1; j < fd; j++)
					cur *= len;
				r += cur;
			} else
				r += x;
		} // for
		return r;
	}// method

	public static String getOffsetSecret(int offset) {
		return baseSecret;
		/*
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= baseSecret.length(); i++) {
			int o = (i + offset) % baseSecret.length();
			sb.append(baseSecret.charAt(o));
		}
		return sb.toString();
		*/
	}// method

	public static String getCheckCode(String str) {
		str = str.substring(0, str.length() - 1);
		int code = 0;
		for (int i = 0; i < str.length(); i++)
			code += baseSecret.indexOf(str.charAt(i));
		return baseSecret.charAt(code % baseSecret.length()) + "";
	}//
}// class
