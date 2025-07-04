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
 * 用户融资信息表
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_financing_interest_info")
@ApiModel(value="UserFinancingInterestInfo对象", description="用户融资信息表")
public class UserFinancingInterestInfo extends Model<UserFinancingInterestInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "年化利息开始计算日期")
    private Date annualizedInterestTime;

    @ApiModelProperty(value = "融资年化利率")
    private BigDecimal annualizedInterestRate;

    @ApiModelProperty(value = "融资金额")
    private BigDecimal financingAmount;

    @ApiModelProperty(value = "已结算融资本金")
    private BigDecimal settlementedFinancingAmount;

    @ApiModelProperty(value = "0-持仓；1-新股融资申购")
    private Integer source;

    @ApiModelProperty(value = "来源类型的对象id，比如持仓类型，就对应持仓表ID")
    private Integer sourceId;

    @ApiModelProperty(value = "是否已经全部结算")
    private Boolean settlement;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
