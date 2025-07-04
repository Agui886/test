package vo.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FundAnalysisStatisticsPositionResultVO {
	
	 @ApiModelProperty(value = "股票代码")
	    private String stockCode;

	    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	    private String stockType;
	    
	    @ApiModelProperty(value = "总股数")
	    private Integer totalShares;
	    
	    @ApiModelProperty(value = "最新价")
		private BigDecimal nowPrice = BigDecimal.ZERO;
	    
	    @ApiModelProperty(value = "人民币兑换该股票币种汇率")
		private BigDecimal exchangeRate = BigDecimal.ONE;
	    
	    @ApiModelProperty(value = "总市值")
		private BigDecimal marketValue;
	    
	    @ApiModelProperty(value = "总市值(人名币)=总市值/人民币兑换该股票币种汇率")
		private BigDecimal marketValueCny;
	    
	    public BigDecimal getMarketValueCny() {
	    	// 1. 检查marketValue是否为空
	        if (marketValue == null) {
	            return null;
	        }
	        
	        // 2. 检查exchangeRate是否为空或为零
	        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
	            return null; // 避免除零错误
	        }
	        
	        // 3. 计算并返回结果（保留2位小数，四舍五入）
	        return marketValue.divide(exchangeRate, 2, RoundingMode.HALF_UP);
	    }
}
