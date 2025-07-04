package utils;

import java.util.Random;

public class OrderNumberGenerator {
	static String[] w = {
		"A","B","C","D","E",
		"H","I","J","K","L",
		"M","N","O","P","Q",
		"R","S","T","U","V",
		"W","X","Y","Z"
	};
	
	/**
	 * 0.买入
	 * 1.平仓
	 * 2.充值
	 * 3.提现
	 * 4.0元新股申购
	 * 5.现金新股申购
	 * 6.融资新股申购
	 * 7.跟投记录
	 * 8.跟投-交易记录
	 * 9.跟投申请
	 * 100.其他
	 * @param type
	 * @return
	 */
    public static String create(Integer type) {
    	long timestamp = System.currentTimeMillis();
    	String st;
    	Random r = new Random();
    	switch(type) {
    	case 0 :
    	default:
    		st = "B";
    		break;
    	case 1 :
    		st = "C";
    		break;
    	case 2 :
    		st = "R";
    		break;
    	case 3 :
    		st = "W";
    		break;
    	case 4 :
    		st = "NZ";
    		break;
    	case 5 :
    		st = "NC";
    		break;
    	case 6 :
    		st = "NF";
    		break;
    	case 7 :
    		st = "GT";
    		break;
    	case 8 :
    		st = "GT-JY";
    		break;
    	case 9 :
    		st = "GT-SQ";
    		break;
    	case 100 :
    		st = "OTHER";
    		break;
    	}
    	return st + timestamp + w[r.nextInt(24)] + w[r.nextInt(24)] + w[r.nextInt(24)];
    }
}
