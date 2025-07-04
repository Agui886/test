package vo.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockData {
	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;
	
	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "最新价")
	//@JsonSerialize(using = BigDecimalSerizlize.class)
	private BigDecimal nowPrice = BigDecimal.ZERO;

	@ApiModelProperty(value = "涨幅比例")
	//@JsonSerialize(using = BigDecimalSerizlize.class)
	private BigDecimal percentageIncrease = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "昨日收盘价格")
    private BigDecimal prevClose = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "成交量")
	private BigDecimal turnover = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "走势")
	private BigDecimal[] trends;
	
	public BigDecimal[] getTrends() {
		if(prevClose == null || nowPrice == null) {
			return trends;
		}
		trends = new BigDecimal[10];
		Random r = new Random();
		trends[0] = this.prevClose;
		trends[9] = this.nowPrice;
		for(int i = 1; i < 9; i++) {
			int p = r.nextInt(6);
			if(p == 0) {
				trends[i] = trends[i-1];
				continue;
			}
			boolean b = r.nextInt(2) == 0 ? true : false;
			p = b ? p : -p;
			trends[i] = trends[i-1].subtract(trends[i-1].multiply(new BigDecimal(p)).divide(new BigDecimal(100), 3, RoundingMode.HALF_UP));
		}
		return trends;
	}
	
	public BigDecimal getPercentageIncrease() {
		if(prevClose == null || nowPrice == null || prevClose.equals(BigDecimal.ZERO) || nowPrice.equals( BigDecimal.ZERO)) {
			return percentageIncrease;
		}
		try {
			return (nowPrice.subtract(prevClose)).multiply(new BigDecimal("100")).divide(prevClose,2, RoundingMode.DOWN);//（最新价-昨日收盘价）*100/收盘价
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}
	
	 public String getStockPlate() {
    	if(stockCode.startsWith("688") && stockCode.equals("sh")) {
    		return "科创";
    	} else if(stockCode.equals("sz") && stockCode.startsWith("30")) {
    		return "创业";
    	}
    	return "";
	 }
	
}