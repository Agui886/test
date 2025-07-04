package vo.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.json.JSONArray;
import enums.StockMarketTypeEnum;
import enums.StockTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import service.SysParamConfigService;
import utils.StringUtil;

@Data
public class StockDayTransactionVO {

	@ApiModelProperty("股票代码")
	private String stockCode;
	
	@ApiModelProperty("股票类型")
	private String stockType;
	
	@ApiModelProperty("股票名称")
	private String stockName;
	
	@ApiModelProperty("一手的股数（美股/港股：通过接口获取实时数据，A股：所有股票每手股数固定为100股）")
    private Integer sharesOfHand;
	
	@ApiModelProperty("数据获取时间")
	private Date dataGetTime;

	@ApiModelProperty("分时成交dtail-原始信息数据")
	private JSONArray details;
	
	@ApiModelProperty("分时成交-数据")
	private StockDayTransactionDataVO dayTransactionData;
	
	@ApiModelProperty("市场状态，0-已收盘，1-午间休市，2-交易中")
	private Integer marketStatus;
	
    @ApiModelProperty(value = "人民币兑换该股票币种汇率")
	private BigDecimal exchangeRate = BigDecimal.ONE;
    
    @ApiModelProperty(value = "人民币兑换该股票币种汇率")
	private BigDecimal buyingFeeRate = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "人民币兑换该股票币种汇率")
	private BigDecimal buyingStampDutyRate = BigDecimal.ZERO;
    
	/**
	 * 获取当日-分时成交-数据转换成page，list数据
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public StockDayTransactionDataVO getDayTransactionData(StockDayTransactionParamVO param) {
		StockDayTransactionDataVO stockDayTransactionDataVO = new StockDayTransactionDataVO();
		//Page<StockDayTransactionDataPageVO> page = new Page<>(pageNo,pageSize);
		Page<StockDayTransactionDataPageVO> page = new Page<>(param.getPageNo(),param.getPageSize());
		List<StockDayTransactionDataPageVO> voList = new ArrayList<>();
		
		 // 检查details是否为空
	    if (this.details == null || this.details.isEmpty()) {
	        page.setRecords(voList);
	        stockDayTransactionDataVO.setStockDayTransactionDataPage(page);
	        return stockDayTransactionDataVO;
	    }
	    
	    // 处理displayReleaseTime逻辑
	    List<Object> filteredDetails = new ArrayList<>();
	    int startIndex = 0;
	    
	    if (param.getDisplayReleaseTime() != null) {
	        // 1. 检查displayReleaseTime是否大于当前时间
	        if (param.getDisplayReleaseTime().after(new Date())) {
	            page.setRecords(voList);
	            page.setTotal(0);
	            stockDayTransactionDataVO.setStockDayTransactionDataPage(page);
		        return stockDayTransactionDataVO;
	        }
	        
	        // 2. 解析displayReleaseTime的时间部分
	        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	        String releaseTimeStr = timeFormat.format(param.getDisplayReleaseTime());
	        
            // 3. 美股特殊处理
            if (StockTypeEnum.US.getCode().equals(this.stockType)) {
                // 判断releaseTimeStr属于哪个时间段
                boolean isNightRelease = releaseTimeStr.compareTo("21:00:00") >= 0 && releaseTimeStr.compareTo("23:59:59") <= 0;
                
                // 找到白天段开始位置
                int dayStartIndex = -1;
                for (int idx = 0; idx < details.size(); idx++) {
                    Object data = details.get(idx);
                    if (data instanceof String) {
                        String[] parts = ((String) data).split(",");
                        if (parts.length > 0) {
                            String timeStr = parts[0];
                            // 白天段时间: 00:00:00 - 20:59:59
                            if (timeStr.compareTo("00:00:00") >= 0 && timeStr.compareTo("20:59:59") <= 0) {
                                dayStartIndex = idx;
                                break;
                            }
                        }
                    }
                }
                if (dayStartIndex == -1) {
                    dayStartIndex = details.size(); // 没有白天段
                }
                
                // 根据时间段处理过滤
                if (isNightRelease) {
                    // 晚上段处理: 21:00:00-23:59:59
                    for (int idx = 0; idx < dayStartIndex; idx++) {
                        Object data = details.get(idx);
                        if (data instanceof String) {
                            String[] parts = ((String) data).split(",");
                            if (parts.length > 0 && parts[0].compareTo(releaseTimeStr) >= 0) {
                                startIndex = idx;
                                break;
                            }
                        }
                    }
                    // 晚上段未找到则跳至白天段开始
                    if (startIndex == 0) { // 说明没有找到符合条件的晚上段数据
                        startIndex = dayStartIndex;
                    }
                } else {
                    // 白天段处理: 00:00:00-20:59:59
                    startIndex = details.size(); // 默认取末尾
                    for (int idx = dayStartIndex; idx < details.size(); idx++) {
                        Object data = details.get(idx);
                        if (data instanceof String) {
                            String[] parts = ((String) data).split(",");
                            if (parts.length > 0 && parts[0].compareTo(releaseTimeStr) >= 0) {
                                startIndex = idx;
                                break;
                            }
                        }
                    }
                }
            } else {
                // 非美股保持原逻辑
                for (int idx = 0; idx < details.size(); idx++) {
                    Object data = details.get(idx);
                    if (data instanceof String) {
                        String[] parts = ((String) data).split(",");
                        if (parts.length > 0 && parts[0].compareTo(releaseTimeStr) >= 0) {
                            startIndex = idx;
                            break;
                        }
                    }
                }
            }
	        
	        // 4. 截取displayReleaseTime之后的数据
	        if (startIndex < details.size()) {
	            filteredDetails = details.subList(startIndex, details.size());
	        }
	    } else {
	        // 未提供displayReleaseTime时使用全部数据
	        filteredDetails = details;
	    }
	    
	    //如果是市价
	    if(param.getMarketPrice() != null && param.getMarketPrice()) {
	    	if (filteredDetails.size() > 0) {
	    		Object lastDetail = filteredDetails.get(filteredDetails.size() - 1);
		         filteredDetails = new ArrayList<>();
		         filteredDetails.add(lastDetail);
			}
	    }else {//否则
	    	// 判断buyingPrice过滤
	        if (param.getBuyingPrice() != null) {
	            filteredDetails = filterByBuyingPrice(filteredDetails, param.getBuyingPrice(), startIndex);
	        }
	    }
	    
	    //随机一组数据
	    if (filteredDetails.size() > 0) {
	    	Integer random = ThreadLocalRandom.current().nextInt(filteredDetails.size());
		    Object obj = filteredDetails.get(random);
		    //存入，分时成交,随机数据
	        stockDayTransactionDataVO.setRandomStockDayTransactionDataPage(dataConversion(obj,startIndex+random));
		}
	    // 分页处理
	    int total = filteredDetails.size();
	    page.setTotal(total);
	    
	    if (total > 0) {
	        int offset = (param.getPageNo() - 1) * param.getPageSize();
	        int end = Math.min(offset + param.getPageSize(), total);
	        
	        List<Object> pageData = offset >= total ? 
	            Collections.emptyList() : 
	            filteredDetails.subList(offset, end);
	        
	        // 转换数据
	        for (int i = 0; i < pageData.size(); i++) {
	            Object data = pageData.get(i);
	            // 计算原始details中的全局索引
	            int originalIndex = startIndex + offset + i;
	            StockDayTransactionDataPageVO vo = dataConversion(data, originalIndex);
	            if (vo != null) {
	            	voList.add(vo);
				}
	        }
	    }
	    
	    page.setRecords(voList);
	    stockDayTransactionDataVO.setStockDayTransactionDataPage(page);
        return stockDayTransactionDataVO;
	    
	}
	
	//数据转换
    private StockDayTransactionDataPageVO dataConversion(Object data, Integer originalIndex) {
    	if (data instanceof String) {
            String detail = (String) data;
            String[] parts = detail.split(",");
            if (parts.length >= 3) {
                StockDayTransactionDataPageVO vo = new StockDayTransactionDataPageVO();
                vo.setSub(originalIndex); // 设置下标（全局索引）
                vo.setTradeTime(parts[0]);// 第一个字段：交易时间
                vo.setPrice(parts[1]); // 第二个字段：价格        
                vo.setVolume(parts[2]);//第三个字段：交易量
                // 处理交易日
                if(this.dataGetTime != null) {
                	String tradeDay = "";
                	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
                	// 美股的日期处理
                	if (this.stockType.equals(StockTypeEnum.US.getCode())) {
                		 // 创建 Calendar 实例并设置时间
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(this.dataGetTime);
                        
                        // 获取 dataGetTime 的小时
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        
                        // 解析交易时间字符串
                        LocalTime tradeLocalTime = LocalTime.parse(vo.getTradeTime());
                        LocalTime nineteen = LocalTime.of(21, 0); // 21:00 作为分界点

                        // 情况1：dataGetTime 在 21:00:00 - 23:59:59 之间
                        if (hour >= 21) {
                        	tradeDay = DATE_FORMAT.format(this.dataGetTime);
                        }else { // 情况2：dataGetTime 在 00:00:00 - 20:59:59 之间
                        	 // 判断交易时间是否在 21:00:00 之后（含）
                            if (tradeLocalTime.isAfter(nineteen) || tradeLocalTime.equals(nineteen)) {
                                // 交易时间在21:00-23:59:59之间，取前一天
                                calendar.add(Calendar.DAY_OF_MONTH, -1);
                                tradeDay =  DATE_FORMAT.format(calendar.getTime());
                            } else {
                                // 交易时间在00:00:00-20:59:59之间，取当天
                            	tradeDay =  DATE_FORMAT.format(dataGetTime);
                            }
                        }
                    }else {//其他市场直接取 dataGetTime 的日期部分
                    	tradeDay = DATE_FORMAT.format(this.dataGetTime);
                    }
                	vo.setTradeDay(tradeDay);
                }
                return vo;
            }
        }
		return null;
	}

	/**
     * 根据买入价格过滤记录
     */
    private List<Object> filterByBuyingPrice(List<Object> details, BigDecimal buyingPrice, int startIndex) {
        if (buyingPrice == null) return details;
        
        String pricePrefix = buyingPrice.stripTrailingZeros().toPlainString();
        return IntStream.range(0, details.size())
            .filter(i -> {
                Object data = details.get(i);
                if (data instanceof String) {
                    String[] parts = ((String) data).split(",");
                    if (parts.length >= 2) {
                        return parts[1].startsWith(pricePrefix);
                    }
                }
                return false;
            })
            .mapToObj(details::get)
            .collect(Collectors.toList());
    }

}
