package utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class DESUtil {
	    
    public static String encrypt(String data, String password) {
    	try {
	        SecureRandom random = new SecureRandom();        
	        DESKeySpec desKey = new DESKeySpec(password.getBytes());
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	        SecretKey securekey = keyFactory.generateSecret(desKey);
	        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
	        byte[] bt =  cipher.doFinal(data.getBytes());
	        return HexUtil.encode(bt);
    	} catch(Exception ex) {
    		return null;
    	}
    }
   
    public static String decrypt(String hexStr, String key) {
    	try {
    		byte[] srcBytes = HexUtil.decode(hexStr);
	        SecureRandom random = new SecureRandom();
	        DESKeySpec desKey = new DESKeySpec(key.getBytes());
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	        SecretKey securekey = keyFactory.generateSecret(desKey);
	        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
	        return new String(cipher.doFinal(srcBytes));
    	} catch(Exception ex) {
    		return null;
    	}
    }
    
    public static String encrypts(String data, String password) {
    	try {
	        SecureRandom random = new SecureRandom();        
	        DESKeySpec desKey = new DESKeySpec(password.getBytes());
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	        SecretKey securekey = keyFactory.generateSecret(desKey);
	        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
	        byte[] bt =  cipher.doFinal(data.getBytes());
	        return Base64.getEncoder().encodeToString(bt);
    	} catch(Exception ex) {
    		return null;
    	}
    }

}