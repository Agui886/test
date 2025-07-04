package entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * 用户股票委托订单表
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_stock_pending")
@ApiModel(value="UserStockPending对象", description="用户股票委托订单表")
public class UserStockPending extends Model<UserStockPending> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "委托ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "持仓类型，实盘-0，模拟-1")
    private Integer positionType;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @ApiModelProperty(value = "股票所属板块")
    private String stockPlate;

    @ApiModelProperty(value = "买入时的交易订单号")
    private String tradingOrderSn;

    @ApiModelProperty(value = "委托时间")
    private Date pendingTime;

    @ApiModelProperty(value = "转入持仓时间")
    private Date positionTime;

    @ApiModelProperty(value = "持仓状态，0-待提交，1-已委托，2-已成交，3-已撤销，4-已拒绝，5-已过期")
    private Integer positionStatus;

    @ApiModelProperty(value = "买入价格")
    private BigDecimal buyingPrice;

    @ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
    private Integer positionDirection;

    @ApiModelProperty(value = "买入股数")
    private Integer buyingShares;

    @ApiModelProperty(value = "杠杆倍数")
    private Integer lever;

    @ApiModelProperty(value = "买入手续费")
    private BigDecimal buyingFee;

    @ApiModelProperty(value = "买入手续费率")
    private BigDecimal buyingFeeRate;

    @ApiModelProperty(value = "买入时的印花税")
    private BigDecimal buyingStampDuty;

    @ApiModelProperty(value = "买入时的印花税率")
    private BigDecimal buyingStampDutyRate;

//    @ApiModelProperty(value = "是否为大宗交易")
//    private Boolean isBlockTrading;

    @ApiModelProperty(value = "持仓id，转入持仓后关联")
    private Integer positionId;

    @ApiModelProperty(value = "人民币兑换该流水币种的汇率")
    private BigDecimal exchangeRate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
