package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class MCrypt {
	public static String encrypt(String data, String key, String iv) throws Exception {
		 Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		 int blockSize = cipher.getBlockSize();
		 byte[] dataBytes = data.getBytes("UTF-8");
		 int plainTextLength = dataBytes.length;
		 if (plainTextLength % blockSize != 0) {
		 plainTextLength = plainTextLength + (blockSize - plainTextLength % blockSize);
		 }
		 byte[] plaintext = new byte[plainTextLength];
		 System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
		 SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
		 IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
		 cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
		 byte[] encrypted = cipher.doFinal(plaintext);
		 return Base64.encodeBase64URLSafeString(encrypted);
		 }
}
