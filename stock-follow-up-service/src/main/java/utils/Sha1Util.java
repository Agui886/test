package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Sha1Util {
	public static String encode(String s) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA-1");
			md.update(s.getBytes());
			byte[] digest = md.digest();
			return HexUtil.encode(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String encodeBase64(String s) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA-1");
			md.update(s.getBytes());
			byte[] digest = md.digest();
			return Base64.getEncoder().encodeToString(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
