package entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户-跟投-交易记录
 * </p>
 *
 * @author 
 * @since 2025-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_follow_up_trading")
@ApiModel(value="UserFollowUpTrading对象", description="用户-跟投-交易记录")
public class UserFollowUpTrading extends Model<UserFollowUpTrading> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "交易ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "导师id")
    private Integer tutorId;

    @ApiModelProperty(value = "信号id")
    private Integer signId;

    @ApiModelProperty(value = "跟投记录id")
    private Integer followRecordId;

    @ApiModelProperty(value = "持仓id")
    private Integer positionId;

    @ApiModelProperty(value = "委托id")
    private Integer pendingId;

    @ApiModelProperty(value = "交易订单号")
    private String tradingOrderSn;

    @ApiModelProperty(value = "交易时间")
    private Date tradingTime;
    
    @ApiModelProperty(value = "交易仓位，比如0.25为1/4仓，0.5为1/2仓，1为全仓")
    private BigDecimal tradingPosition;
    
    @ApiModelProperty(value = "买入-价格")
    private BigDecimal buyingPrice;

    @ApiModelProperty(value = "买入-股数")
    private Integer buyingShares;

    @ApiModelProperty(value = "买入-手续费")
    private BigDecimal buyingFee;

    @ApiModelProperty(value = "买入-手续费率")
    private BigDecimal buyingFeeRate;

    @ApiModelProperty(value = "买入-时的印花税")
    private BigDecimal buyingStampDuty;

    @ApiModelProperty(value = "买入-时的印花税率")
    private BigDecimal buyingStampDutyRate;

    @ApiModelProperty(value = "人民币兑换该流水币种的汇率")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "导师-抽成")
    private BigDecimal tutorCommission;
    
    @ApiModelProperty(value = "导师-抽成费率")
    private BigDecimal tutorCommissionRatio;
	
    @ApiModelProperty(value = "平台-抽成")
    private BigDecimal platformCommission;
    
    @ApiModelProperty(value = "平台-抽成费率")
    private BigDecimal platformCommissionRatio;
    
    @ApiModelProperty(value = "变更前-当前持有-股数")
    private Integer beHoldShares;
    
    @ApiModelProperty(value = "变更前-当前-跟投资金")
    private BigDecimal beCurrentFollowSum;
    
    @ApiModelProperty(value = "变更后-当前-跟投资金")
    private BigDecimal afCurrentFollowSum;
    
    @ApiModelProperty(value = "操作人")
    private String operator;
    
    @ApiModelProperty(value = "完成-时间")
    private Date finishTime;

    @ApiModelProperty(value = "流程-时间1（根据显示发布时间-完成时间，之间设置任意时间，小于flow_time2）")
    private Date flowTime1;

    @ApiModelProperty(value = "流程-时间2（根据显示发布时间-完成时间，之间设置任意时间，小于flow_time3）")
    private Date flowTime2;

    @ApiModelProperty(value = "流程-时间3（根据显示发布时间-完成时间，之间设置任意时间，小于flow_time4）")
    private Date flowTime3;

    @ApiModelProperty(value = "流程-时间4（根据显示发布时间-完成时间，之间设置任意时间，小于flow_time4完成时间）")
    private Date flowTime4;
    
    @ApiModelProperty(value = "信号类型,0-买入,1-卖出")
    private Integer signType;
    
    @ApiModelProperty(value = "变更前-当前-仓位数值比")
    private BigDecimal beCurrentPositionValueRatio;
    
    @ApiModelProperty(value = "变更后-当前-仓位数值比")
    private BigDecimal afCurrentPositionValueRatio;
    
    @ApiModelProperty(value = "变更前-当前-跟投总仓位数值比")
    private BigDecimal beCurrentFollowPositionValueRatio;
    
    @ApiModelProperty(value = "变更后-当前-跟投总仓位数值比")
    private BigDecimal afCurrentFollowPositionValueRatio;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "导师名称")
    private String tutorName;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "会员等级id")
	private Integer levelId;
    
    /**代理等级表的信息**/
    @ApiModelProperty(value = "等级名称")
    @TableField(exist = false)
    private String levelName;
    
    @ApiModelProperty(value = "等级值")
    @TableField(exist = false)
    private Integer levelVal;
    /**代理等级表的信息**/
    
    @TableField(exist = false)
    @ApiModelProperty(value = "项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    private Integer itemType;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @TableField(exist = false)
    @ApiModelProperty(value = "交易股票代码")
    private String stockCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
