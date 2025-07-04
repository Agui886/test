package utils;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GoogleAuthUtil {


    /**
     * 生成key
     */
    public static String generaKey(){
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    /**
     * 生成二维码
     * @param key 生成的key
     * @param issuer app上title名字                服务名称 如: Google Github 印象笔记
     * @param account app上title名字(括号里的)       用户账户 如: example@domain.com 138XXXXXXXX
     */
    public static String createGoogleAuthQRCodeData(String key, String account,String issuer) throws UnsupportedEncodingException {
        return String.format("otpauth://totp/%s?secret=%s&issuer=%s",
                URLEncoder.encode(issuer + ":" + account, "UTF-8"),
                URLEncoder.encode(key, "UTF-8"),
                URLEncoder.encode(issuer, "UTF-8"));
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = generaKey();
        System.out.println(s);
        String googleAuthQRCodeData = createGoogleAuthQRCodeData(s, "aaa", "股票证券后台");
        System.out.println(googleAuthQRCodeData);
    }

    /**
     * 检查
     * @param key 生成的key
     * @param code 用户输入的面膜
     */
    public static boolean check(String key,Integer code){
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        // 根据用户密钥和用户输入的密码，验证是否一致。
        boolean isCodeValid = gAuth.authorize(key, code);
        return isCodeValid;
    }
}
