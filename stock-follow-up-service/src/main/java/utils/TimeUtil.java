package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static String format(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date old = simpleDateFormat.parse(str);
        String newDate = simpleDateFormat.format(old);
        return newDate;
    }
    
    /**
     * 格式化时间
     * @param date
     * @param i
     * @param d
     * @return
     */
    public static String getEndTime(Date date,int i, int d) {
    	SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmm");
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(i, d);
        date = calendar.getTime();
        return s.format(date);
    }

    /**
     *
     * @param time 10位long时间类型
     * @return
     */
    public static String longtoStr(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time * 1000));
    }

    public static String longtoStrWithT(long time) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date(time * 1000));
    }
    
    /**
     *  比较两个时间大小
     * 当time1 > time2 返回1
     * 当time1 < time2 返回-1
     * 当time1 = time2 返回0
     * @param time1
     * @param time2
     * @return
     */
	public static int compareToDate(String time1, String time2) {
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		try {
			Date a = s.parse(time1);
			Date b = s.parse(time2);
			return a.compareTo(b);
		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		}
	}
}
