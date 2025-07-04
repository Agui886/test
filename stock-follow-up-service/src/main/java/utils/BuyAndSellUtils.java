package utils;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;


public class BuyAndSellUtils {
    /**
     * 判断是已收盘，午间休市，还是交易中
     * @param am_begin_time
     * @param am_end_time
     * @param pm_begin_time
     * @param pm_end_time
     * @param stockType
     * @param workDayVerify 工作日验证(true：验证，false：不验证)
     * @return 0-已收盘，1-午间休市，2-交易中
     */
    public static int getTradingStatus(String am_begin_time, String am_end_time, String pm_begin_time, String pm_end_time, String stockType,Boolean workDayVerify) {
        if (StringUtils.isBlank(am_begin_time) || StringUtils.isBlank(am_end_time) || StringUtils.isBlank(pm_begin_time) || StringUtils.isBlank(pm_end_time)) {
            return 0;
        }
        ZonedDateTime zonedDateTime;
		switch (stockType) {
		case "us": 
			zonedDateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
			break;
		default:
			zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
			break;
		}
		Timestamp timestamp = Timestamp.valueOf(zonedDateTime.toLocalDateTime());
		Date date = new Date(timestamp.getTime());
		if (workDayVerify) {
			if(!isWorkDay(date)) {
				return 0;
			}
		}
		SimpleDateFormat yyyyMMddDf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat yyyyMMddHHmmDf = new SimpleDateFormat("yyyyMMddHH:mm");
        Date amBeginTime = null;
        Date amEndTime = null;
        Date pmBeginTime = null;
        Date pmEndTime = null;
        String dateFmt = yyyyMMddDf.format(date);
        try {
        	amBeginTime =	yyyyMMddHHmmDf.parse(dateFmt + am_begin_time);
        	amEndTime =	yyyyMMddHHmmDf.parse(dateFmt + am_end_time);
        	pmBeginTime =	yyyyMMddHHmmDf.parse(dateFmt + pm_begin_time);
        	pmEndTime =	yyyyMMddHHmmDf.parse(dateFmt + pm_end_time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        if(date.compareTo(amBeginTime) == -1 || date.compareTo(pmEndTime) == 1) {
        	return 0;
        }
        if(date.compareTo(amEndTime) == 1 && date.compareTo(pmBeginTime) == -1) {
        	return 1;
        }
        return 2;
    }

    public static boolean isWorkDay(Calendar cal) {
        if (cal.get(7) == 7 || cal.get(7) == 1) {
            return false;
        }
        return true;
    }
    
    public static boolean isWorkDay(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        return isWorkDay(cal);
    }
    public static void main(String[] args) throws Exception {
    	int i = getTradingStatus("09:30", "12:00", "12:00", "16:00", "us",true);
    	System.err.println(i);
    }

}
