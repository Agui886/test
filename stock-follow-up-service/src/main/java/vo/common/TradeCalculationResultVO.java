package vo.common;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TradeCalculationResultVO {

    @ApiModelProperty(value = "操作股数")
    private Integer buyingShares;
    
    @ApiModelProperty(value = "操作总金额")
    private BigDecimal operateSum;
    
    @ApiModelProperty(value = "操作手续费")
    private BigDecimal operateFee;
    
    @ApiModelProperty(value = "操作印花税")
    private BigDecimal operateStampDuty;
    
    @ApiModelProperty(value = "导师抽佣")
    private BigDecimal tutorCommission;
    
    @ApiModelProperty(value = "平台抽佣")
    private BigDecimal platformCommission;
    
    @ApiModelProperty(value = "手续费率")
    private BigDecimal buyingFeeRate;
    
    @ApiModelProperty(value = "印花税率")
    private BigDecimal buyingStampDutyRate;
    
    @ApiModelProperty(value = "导师抽佣比例")
    private BigDecimal tutorCommissionRatio;
    
    @ApiModelProperty(value = "平台抽佣比例")
    private BigDecimal platformCommissionRatio;
    
    @ApiModelProperty(value = "验证状态")
    private boolean valid = true;
    
    @ApiModelProperty(value = "错误说明")
    private String errorMessage;
    
}
