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
 * 会员资金变更记录表
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_amt_change_record")
@ApiModel(value="UserAmtChangeRecord对象", description="会员资金变更记录表")
public class UserAmtChangeRecord extends Model<UserAmtChangeRecord> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "会员ID")
    private Integer userId;

    @ApiModelProperty(value = "变更类型")
    private String deType;

    @ApiModelProperty(value = "变更类型名称")
    private String deTypeName;

    @ApiModelProperty(value = "变更前可用金额")
    private BigDecimal beAmt;

    @ApiModelProperty(value = "变更后可用金额")
    private BigDecimal afAmt;

    @ApiModelProperty(value = "变更前交易冻结金额")
    private BigDecimal beTradingFrozenAmt;

    @ApiModelProperty(value = "变更后交易冻结金额")
    private BigDecimal afTradingFrozenAmt;

    @ApiModelProperty(value = "变更前跟投资金")
    private BigDecimal beFollowAmt;

    @ApiModelProperty(value = "变更后跟投资金")
    private BigDecimal afFollowAmt;

    @ApiModelProperty(value = "变更人民币金额")
    private BigDecimal deCnyAmt;

    @ApiModelProperty(value = "变更外币金额")
    private BigDecimal deForeignAmt;

    @ApiModelProperty(value = "变更详细说明")
    private String deSummary;

    @ApiModelProperty(value = "流水时间")
    private Date addTime;

    @ApiModelProperty(value = "IP地址")
    private String addIp;

    @ApiModelProperty(value = "IP地址详情")
    private String addAddress;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "人民币兑换该流水币种的汇率")
    private BigDecimal exchangeRate;
    
    @ApiModelProperty(value = "业务订单号")
    private String businessOrderSn;

    @ApiModelProperty(value = "上级的ID，0表示没有上级")
    @TableField(exist = false)
    private Integer agentId;
    
    @ApiModelProperty(value = "上级代理名称")
    @TableField(exist = false)
    private String agentName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
