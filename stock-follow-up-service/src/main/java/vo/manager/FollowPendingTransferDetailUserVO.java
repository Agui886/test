package vo.manager;

import java.math.BigDecimal;
import java.util.Date;

import entity.UserInfo;
import enums.UserFollowStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utils.StringUtil;

@Data
public class FollowPendingTransferDetailUserVO extends UserInfo{
	
	@ApiModelProperty(value = "累计转入金额")
	private BigDecimal totalCashinSum;
	
	@ApiModelProperty(value = "累计转出金额")
    private BigDecimal totalCashoutSum;
	
	@ApiModelProperty(value = "累计跟投资金（累计初始-跟投资金）")
    private BigDecimal totalInitialFollowSum;
	
	@ApiModelProperty(value = "初始-跟投资金")
    private BigDecimal initialFollowSum;

    @ApiModelProperty(value = "当前-跟投资金")
    private BigDecimal currentFollowSum;
    
    @ApiModelProperty(value = "跟投持仓市值")
	private BigDecimal positionFollowSum;
	
    @ApiModelProperty(value = "开始-跟投时间")
    private Date followStartTime;
    
    @ApiModelProperty(value = "结束-跟投时间")
    private Date followEndTime;
    
    @ApiModelProperty(value = "剩余天数")
    private Integer remainingDays;
    
    @ApiModelProperty(value = "用户跟投状态，0-进行中，1-顺延数据库不存储此状态，通过结束时间-当前时间，为负数时，显示顺延状态），2-已结束")
    private Integer followStatus;
    
    @ApiModelProperty(value = "最后-完成时间")
    private Date finallyFinishTime;
    
    @ApiModelProperty(value = "分时成交-随机-价格")
    private BigDecimal randomStockDayTransactionBuyingPrice;
    
    @ApiModelProperty(value = "分时成交-随机-完成时间")
    private Date randomStockDayTransactionFinishTime;
    
    @ApiModelProperty(value = "委托单id")
    private Integer pendingId;
    
    public Integer getRemainingDays() {
    	if (this.followEndTime != null) {
			return StringUtil.calculateDaysDifference(this.followEndTime);
		}
    	return null;
    }
    
    public Integer getFollowStatus() {
    	if (this.followStatus == UserFollowStatusEnum.UNDERWAY.getCode()) {
    		if (getRemainingDays() < 0) {
				return UserFollowStatusEnum.POSTPONE.getCode();
			}
		}
    	return  followStatus;
    }
}
