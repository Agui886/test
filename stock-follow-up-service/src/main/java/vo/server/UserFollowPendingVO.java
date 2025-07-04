package vo.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import entity.UserFollowUpPending;
import enums.SignTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserFollowPendingVO extends UserFollowUpPending{

	 @ApiModelProperty(value = "是否展示")
	 private Boolean isShow;
	 
	 @ApiModelProperty(value = "显示发布时间+15分钟")
	 private Date displayReleaseTime15;
	 
	 @ApiModelProperty(value = "交易-价格")
	 private BigDecimal buyingPrice;
	 
	 @ApiModelProperty(value = "交易-股数")
	 private Integer buyingShares;
	 
     @ApiModelProperty(value = "流程-时间1（根据显示发布时间-完成时间，之间设置任意时间，小于flow_time2）")
     private Date flowTime1;

     @ApiModelProperty(value = "流程-时间2（根据显示发布时间-完成时间，之间设置任意时间，小于flow_time3）")
     private Date flowTime2;

     @ApiModelProperty(value = "流程-时间3（根据显示发布时间-完成时间，之间设置任意时间，小于flow_time4）")
     private Date flowTime3;

     @ApiModelProperty(value = "流程-时间4（根据显示发布时间-完成时间，之间设置任意时间，小于flow_time4完成时间）")
     private Date flowTime4;
     
     @ApiModelProperty(value = "变更前-当前-跟投资金")
     private BigDecimal beCurrentFollowSum;
     
     @ApiModelProperty(value = "变更后-当前-跟投资金")
     private BigDecimal afCurrentFollowSum;
     
     @ApiModelProperty(value = "变更前-当前-仓位数值比")
     private BigDecimal beCurrentPositionValueRatio;
     
     @ApiModelProperty(value = "变更后-当前-仓位数值比")
     private BigDecimal afCurrentPositionValueRatio;
     
     @ApiModelProperty(value = "变更前-当前-跟投总仓位数值比")
     private BigDecimal beCurrentFollowPositionValueRatio;
     
     @ApiModelProperty(value = "变更后-当前-跟投总仓位数值比")
     private BigDecimal afCurrentFollowPositionValueRatio;
     
     @ApiModelProperty(value = "交易仓位，比如0.25为1/4仓，0.5为1/2仓，1为全仓")
     private BigDecimal tradingPosition;
     
     @ApiModelProperty(value = "仓位变化前")
     private BigDecimal bePositionChanges;
     
     @ApiModelProperty(value = "仓位变化后")
     private BigDecimal afPositionChanges;
     
	 //是否显示，根据显示发布时间，24小时后才设置位true
	 public Boolean getIsShow() {
		 if (this.displayReleaseTime15 != null) {
			 Date displayReleaseTime24H = DateUtil.offsetHour(displayReleaseTime15, 24);
			 // 当前时间是否超过发布时间24小时后
			 if (DateUtil.date().after(displayReleaseTime24H)) {
				return true;
			}
		}
		 return false;
	 }
	 
	 public BigDecimal getBePositionChanges() {
		 if (this.getSignType() != null) {
			if (this.getSignType() == SignTypeEnum.BUY.getCode()) {//买入取（变更前-当前-跟投总仓位数值比）
				if (this.getBeCurrentFollowPositionValueRatio() != null) {
					return this.getBeCurrentFollowPositionValueRatio().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
				}
			}else {//卖出取（变更前-当前-跟投总仓位数值比）
				if (this.getBeCurrentPositionValueRatio() != null) {
					return this.getBeCurrentPositionValueRatio().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
				}
			}
		 }
		 return bePositionChanges;
	 }
	 
	 public BigDecimal getAfPositionChanges() {
		 if (this.getSignType() != null) {
			 if (this.getSignType() == SignTypeEnum.BUY.getCode()) {//买入取（变更后-当前-跟投总仓位数值比）
					if (this.getAfCurrentFollowPositionValueRatio() != null) {
						return this.getAfCurrentFollowPositionValueRatio().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
					}
				}else {//卖出取（变更后-当前-跟投总仓位数值比）
					if (this.getAfCurrentPositionValueRatio() != null) {
						return this.getAfCurrentPositionValueRatio().setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
					}
				}	
		 }
		 return afPositionChanges;
	 }
}
