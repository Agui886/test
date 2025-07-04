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
 * 用户-跟投-持仓信息表
 * </p>
 *
 * @author 
 * @since 2025-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_follow_up_position")
@ApiModel(value="UserFollowUpPosition对象", description="用户-跟投-持仓信息表")
public class UserFollowUpPosition extends Model<UserFollowUpPosition> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "持仓ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "导师id")
    private Integer tutorId;

    @ApiModelProperty(value = "跟投记录id")
    private Integer followRecordId;

    @ApiModelProperty(value = "建仓时间")
    private Date creatPositionTime;

    @ApiModelProperty(value = "平仓时间")
    private Date closePositionTime;

    @ApiModelProperty(value = "持仓状态，0-正式持仓，1-已平仓")
    private Integer positionStatus;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @ApiModelProperty(value = "买入成本价格（当前持仓的，所有委托价格*委托股，总额除总股数）")
    private BigDecimal buyingCostPrice;

    @ApiModelProperty(value = "当前持有-股数")
    private Integer holdShares;
    
    @ApiModelProperty(value = "买入-总金额(每次买入操作累加)")
    private BigDecimal buyingPriceTotal;

    @ApiModelProperty(value = "买入-总股数(每次买入操作累加)")
    private Integer buyingSharesTotal;
    
    @ApiModelProperty(value = "卖出-总金额(每次卖出操作累加)")
    private BigDecimal salePriceTotal;
    
    @ApiModelProperty(value = "当前-仓位数值比")
    private BigDecimal currentPositionValueRatio;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "导师名称")
    private String tutorName;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "会员等级id")
	private Integer levelId;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    private Integer itemType;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "最新价")
	private BigDecimal nowPrice = BigDecimal.ZERO;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "人民币兑换该股票币种汇率")
	private BigDecimal exchangeRate = BigDecimal.ONE;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "总市值")
	private BigDecimal marketValue;
    
//    @TableField(exist = false)
//    @ApiModelProperty(value = "浮动盈亏")
//	private BigDecimal floatingProfit;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
