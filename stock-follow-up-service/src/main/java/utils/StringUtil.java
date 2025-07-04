package utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;

public class StringUtil {
	public static boolean isNotEmpty(String s) {
		return s != null && !s.equals("");
	}
	
	public static boolean isEmpty(String s) {
		return s == null || s.equals("");
	}
	
	public static String getVerificationCode(int len) {
		String vCode = "";
  		Random ran = new Random();
  		for(int i = 0; i < len; i++) {
  			vCode += ran.nextInt(10);
  		}
  		vCode = "000000";	//TODO
  		return vCode;
	}
	
	public static boolean isMobileNumber(String s) {
		 String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0-2]|[5-9]))\\d{8}$";
		 Pattern p = Pattern.compile(regex);
		 Matcher m = p.matcher(s);
		 return m.matches();		 
	}
	
	public static String number2Ip(long number) {
    	long l = number >> 24 & 0xFF;
    	long m = number >> 16 & 0xFF;
    	long n = number >> 8 & 0xFF;
    	long o = number & 0xFF;
    	return l + "." + m + "."  +  n + "." +  o;
    }
    
    public static long ip2Number(String ip) {
    	String[] ipSplit = ip.split("\\.");
    	
    	return Long.parseLong(ipSplit[0]) * (int)Math.pow(256, 3) + 
    			Long.parseLong(ipSplit[1]) * (int)Math.pow(256, 2) + 
    			Long.parseLong(ipSplit[2]) * 256 + 
    			Long.parseLong(ipSplit[3]);
    }    
    
    public static String delBom(String s) {
    	char[] bomChar = s.toCharArray();
    	if(bomChar[0] != 65279) {
    		return s;
    	}
    	char[] noneBomchar = new char[bomChar.length - 1];
    	for (int j = 0; j < noneBomchar .length; j++) {
    	noneBomchar [j] = bomChar[j + 1];
    	}
    	String first = String.valueOf(noneBomchar );
    	return first;
    }
    
    public static boolean isMoney(String s) {
    	String regex = "^\\d+(\\.\\d+)?$";
    	return isMatcher(regex, s);
    }
    
    public static boolean isNumber(String s) {
    	String regex = "^\\+?[1-9][0-9]*$";
    	return isMatcher(regex, s);
    }
    
    public static boolean isMatcher(String regex, String s) {
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(s);
    	return m.find();
    }  
    
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
    
    public static String waterMarkString(String str, int bIndex, int len) {
    	if(isEmpty(str)) {
    		return str;
    	}
    	char[] cardNoCharArray = str.toCharArray();
		for(int i = bIndex; i < cardNoCharArray.length && i < bIndex + len; i++) {
			cardNoCharArray[i] = '*';
		}
		return String.valueOf(cardNoCharArray);
    }
    
    /**
     * 验证是否只包含数字和字母
     * @param s
     * @return
     */
    public static boolean isNumberOrEnglish(String s) {
    	String regex="^[a-zA-Z0-9]+$";
    	return isMatcher(regex, s);
    }
    
    public static boolean isNumberAndEnglish(String s) {
    	String regex=".*[a-zA-Z].*";
    	boolean a = isMatcher(regex, s);
    	String regex2=".*[0-9].*";
    	boolean b = isMatcher(regex2, s);
    	if (a && b) {
			return true;
		}
    	return false;
    }
    public static boolean isEmail(String s) {
    	String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    	return isMatcher(regex, s);
    }
    
    public static boolean isIpV4(String s) {
    	String regex = "((1[0-9][0-9]\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)|([1-9][0-9]\\.)|([0-9]\\.)){3}((1[0-9][0-9])|(2[0-4][0-9])|(25[0-5])|([1-9][0-9])|([0-9]))";
    	return isMatcher(regex, s);
    }
    
    public static String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定義script的正則表達式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定義style的正則表達式
        String regEx_html="<[^>]+>"; //定義HTML標籤的正則表達式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //過濾script標籤

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //過濾style標籤

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //過濾html標籤

        return htmlStr.trim(); //返迴文本字符串
    }
    
    public static String newUserNickname() {
    	Random r= new Random();
    	return "NewUser_" + (char)(r.nextInt(26) + 65) 
    			+ (char)(r.nextInt(26) + 65) 
    			+ (char)(r.nextInt(26) + 97)
    			+ (char)(r.nextInt(26) + 97)
    			+ (char)(r.nextInt(26) + 65)
    			+ (char)(r.nextInt(26) + 65);
    }

    /**
     * 32位uuid
     * @return
     */
	public static String uuid32() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 8位uuid
	 * @return
	 */
	public static String uuid8(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0];
    }
	
	public static boolean limitLength(String str,int least,int max) {
		if (str.length() >= least && str.length() <= max) {
			return true;
		}
		return false;
	}
	
	/**
	 * 范围随机值，返回BigDecimal
	 * @return
	 */
	public static BigDecimal scopeRandomValue() {
		 // 定义最小和最大整数值（扩大100倍）
        int min = 7000;
        int max = 10000;

        // 生成7000到10000（含）之间的随机整数
        int randomInt = ThreadLocalRandom.current().nextInt(min, max + 1);

        // 转换为BigDecimal并除以100，保留两位小数
        BigDecimal randomValue = new BigDecimal(randomInt)
                .divide(new BigDecimal(100), 2, RoundingMode.UNNECESSARY);
        return randomValue;
	}
	
	/**
	 * 延迟超过天数
	 */
	public static int calculateDaysDifference(Date date) {
		long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;
		if (date == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        
        // 1. 获取当前日期（去除时分秒）
        DateTime today = DateUtil.beginOfDay(DateUtil.date());
        
        // 2. 处理目标日期（去除时分秒）
        DateTime targetDate = DateUtil.beginOfDay(date);
        
        // 3. 直接计算毫秒差
        long diffMillis = targetDate.getTime() - today.getTime();
        
        // 4. 转换为天数（保留符号）
        long days = diffMillis / ONE_DAY_MILLIS;
        
        // 5. 处理特殊边界情况
        if (days == 0) {
            // 同一天：总是返回1（剩余天数）
            return 1;
        } else if (diffMillis % ONE_DAY_MILLIS != 0) {
            // 处理非整数天的情况
            if (diffMillis > 0) {
                days++; // 未来日期不足一天按一天算
            } else {
                days--; // 过去日期不足一天按一天算
            }
        }
        
        return (int) days;
    }
	
	/**
     * 在指定时间范围内生成有序的时间点数组
     * @param startTime 开始时间
     * @param finishTime 结束时间
     * @param numPoints 需要生成的时间点数量
     * @return 有序的时间点数组 (从小到大)
	 * @throws Exception 
     */
    public static Date[] generateOrderedTimeline(Date startTime, Date finishTime, int numPoints) throws Exception {
        // 1. 验证输入参数
        if (startTime == null || finishTime == null) {
        	throw new Exception("信号-显示开始时间 和 操作-结束时间，不能为空");
        }
        if (numPoints < 1) {
            return new Date[0];
        }
        if (startTime.after(finishTime)) {
            throw new Exception("信号-显示开始时间，不能晚于，操作-结束时间");
        }

        // 转换时间戳
        long startMillis = startTime.getTime();
        long finishMillis = finishTime.getTime();
        long duration = finishMillis - startMillis;

        // 处理极小时间范围（修改条件为 duration < numPoints-1）
        if (duration < numPoints - 1) {
            Date[] dates = new Date[numPoints];
            Arrays.fill(dates, new Date(startMillis));
            return dates;
        }

        // 生成均匀分布的时间点
        long[] timestamps = new long[numPoints];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // 核心修复：基于时间范围等分生成随机点
        for (int i = 0; i < numPoints; i++) {
            // 计算当前点在时间轴上的理论位置
            double position = (double) i / (numPoints - 1);
            // 当前分段的实际范围
            long segmentStart = startMillis + (long) (position * duration);
            long segmentEnd = startMillis + (long) (((double) (i + 1) / numPoints) * duration);
            
            // 末位点特殊处理
            if (i == numPoints - 1) {
                timestamps[i] = segmentStart; // 最后一个点取段首保证不超界
            } else {
                timestamps[i] = random.nextLong(segmentStart, segmentEnd);
            }
        }

        // 转换为Date数组
        Date[] result = new Date[numPoints];
        for (int i = 0; i < numPoints; i++) {
            result[i] = new Date(timestamps[i]);
        }
        
        return result;
    }
    
    /**
     * 生成两个时间点之间的随机时间
     */
    private static Date generateRandomDate(Date start, Date end) {
        long startMillis = start.getTime();
        long endMillis = end.getTime();
        long randomMillis = ThreadLocalRandom.current().nextLong(startMillis, endMillis + 1);
        return new Date(randomMillis);
    }
	
	
	
	public static void main(String[] args) throws Exception {
		//System.out.println(isNotEmpty("1fd"));
		//System.out.println(limitLength("131111111", 3, 8));
//		Date exactTime = DateUtil.parse("2025-04-30 10:20:59");
//		System.out.println(calculateDaysDifference(exactTime));
		
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        
	        // 定义测试参数
	        Date startTime = sdf.parse("2025-07-01 09:55:00");
	        Date finishTime = sdf.parse("2025-07-01 13:57:42");
	        int numPoints = 4;
	        int testRuns = 100;
	        
	        System.out.println("===== 开始测试 =====");
	        System.out.println("时间范围: " + sdf.format(startTime) + " 到 " + sdf.format(finishTime));
	        System.out.println("生成点数: " + numPoints);
	        System.out.println("测试次数: " + testRuns);
	        System.out.println("==================");

	        for (int i = 1; i <= testRuns; i++) {
	            System.out.println("\n--- 测试 #" + i + " ---");
	            
	            // 生成时间线
	            Date[] timeline = generateOrderedTimeline(startTime, finishTime, numPoints);
	            
	            // 检查并打印结果
	            validateAndPrintTimeline(timeline, startTime, finishTime, sdf);
	        }
	}
	
	  private static void validateAndPrintTimeline(Date[] timeline, Date start, Date end, SimpleDateFormat sdf) {
	        if (timeline.length == 0) {
	            System.out.println("错误: 生成了空数组");
	            return;
	        }

	        // 打印所有时间点
	        System.out.println("生成的时间点:");
	        for (int j = 0; j < timeline.length; j++) {
	            String timeStr = sdf.format(timeline[j]);
	            System.out.println((j + 1) + ". " + timeStr);
	        }

	        // 验证时间范围
	        for (Date date : timeline) {
	            if (date.before(start)) {
	                System.out.println("错误: 时间点早于开始时间 - " + sdf.format(date));
	            }
	            if (date.after(end)) {
	                System.out.println("错误: 时间点晚于结束时间 - " + sdf.format(date));
	            }
	        }

	        // 验证顺序性
	        for (int j = 1; j < timeline.length; j++) {
	            if (timeline[j].before(timeline[j - 1])) {
	                System.out.println("错误: 时间点无序 - " + 
	                                  sdf.format(timeline[j - 1]) + " 和 " + 
	                                  sdf.format(timeline[j]));
	            }
	        }
	    }
}
