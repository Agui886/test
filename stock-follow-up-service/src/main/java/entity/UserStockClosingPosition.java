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
 * 用户股票平仓订单
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_stock_closing_position")
@ApiModel(value="UserStockClosingPosition对象", description="用户股票平仓订单")
public class UserStockClosingPosition extends Model<UserStockClosingPosition> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "平仓ID")
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

    @ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
    private Integer positionDirection;

    @ApiModelProperty(value = "买入(原先持有)股数")
    private Integer buyingShares;

    @ApiModelProperty(value = "买入价格")
    private BigDecimal buyingPrice;

    @ApiModelProperty(value = "杠杆倍数")
    private Integer lever;

    @ApiModelProperty(value = "买入手续费")
    private BigDecimal buyingFee;

    @ApiModelProperty(value = "点差费")
    private BigDecimal spreadFee;

    @ApiModelProperty(value = "买入时的印花税")
    private BigDecimal buyingStampDuty;

//    @ApiModelProperty(value = "是否为大宗交易")
//    private Boolean isBlockTrading;

    @ApiModelProperty(value = "卖出时的交易订单号")
    private String tradingOrderSn;

    @ApiModelProperty(value = "平仓时间")
    private Date closingTime;

    @ApiModelProperty(value = "成交时间")
    private Date transactionTime;

    @ApiModelProperty(value = "交易状态，0-交易中，1-成交")
    private Integer transactionStatus;

    @ApiModelProperty(value = "卖出价格")
    private BigDecimal sellingPrice;

    @ApiModelProperty(value = "卖出股数")
    private Integer sellingShares;

    @ApiModelProperty(value = "卖出手续费")
    private BigDecimal sellingFee;

    @ApiModelProperty(value = "卖出时的印花税")
    private BigDecimal sellingStampDuty;

    @ApiModelProperty(value = "到账金额")
    private BigDecimal amountReceived;

    @ApiModelProperty(value = "浮动盈利")
    private BigDecimal floatingProfit;

    @ApiModelProperty(value = "实际盈利")
    private BigDecimal actualProfit;

    @ApiModelProperty(value = "持仓ID")
    private Integer positionId;

    @ApiModelProperty(value = "建仓时间")
    private Date positionTime;

    @ApiModelProperty(value = "人民币兑换该流水币种的汇率")
    private BigDecimal exchangeRate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
