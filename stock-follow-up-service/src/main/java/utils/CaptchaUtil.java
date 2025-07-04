package utils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;
 public class CaptchaUtil {
    private static final String CAPTCHA_IMAGE_FORMAT = "jpeg";
    private static final int CAPTCHA_WIDTH = 150;
    private static final int CAPTCHA_HEIGHT = 40;
    //private static final int CAPTCHA_LENGTH = 5;
    private static final int CAPTCHA_FONT_SIZE = 28;
    private static final int CAPTCHA_X_OFFSET = 20;
    private static final int CAPTCHA_Y_OFFSET = 30;
    private static final String CAPTCHA_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
     public static String generateCaptcha(String captcha) throws Exception {
        // 生成随机验证码字符串
       // String captcha = generateRandomString(CAPTCHA_LENGTH);

         // 生成验证码图片
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, CAPTCHA_FONT_SIZE));
        g.drawString(captcha, CAPTCHA_X_OFFSET, CAPTCHA_Y_OFFSET);
        g.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, CAPTCHA_IMAGE_FORMAT, baos);
        byte[] imageBytes = baos.toByteArray();

        // 将字节数组转换为base64字符串
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        baos.close();
        return "data:image/jpg;base64," + base64Image;
    }

     public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CAPTCHA_CHARACTERS.length());
            sb.append(CAPTCHA_CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
     
     
     public static void main(String[] ss) {
    	 try {
    		 String s;
    		System.err.println(s= generateRandomString(5));
    		System.err.println( generateCaptcha(s));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
}