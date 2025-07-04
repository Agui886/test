package utils;

import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AESUtil {
	static{
		try {
			Security.addProvider(new BouncyCastleProvider());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String encrypt(String key, String str) {
		String encryptValue = "";
		byte[] keyByte = key.getBytes(); 
    	byte[] plaintext = null;
		try {
			plaintext = str.getBytes("UTF-8");
		    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
    		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");

			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			
			byte[] ciphertext = cipher.doFinal(plaintext);
			encryptValue = Base64.getEncoder().encodeToString(ciphertext);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptValue;
	}
	
	public static String decrypt(String key, String str) {
		String encryptValue = "";
		byte[] keyByte = key.getBytes();
    	
    	byte[] plaintext = null;
		try {
			plaintext = str.getBytes("UTF-8");
    		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
    		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
	        cipher.init(Cipher.DECRYPT_MODE, keySpec);  
	        byte[] bys = cipher.doFinal(Base64.getDecoder().decode(plaintext));  
			encryptValue = new String(bys, "UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptValue;
	}
}
