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
 * 会员充值记录表
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cashin_record")
@ApiModel(value="CashinRecord对象", description="会员充值记录表")
public class CashinRecord extends Model<CashinRecord> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "交易号，订单号")
    private String orderSn;

    @ApiModelProperty(value = "会员标识（取自user.id）")
    private Integer userId;

    @ApiModelProperty(value = "存款人姓名")
    private String depositor;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "到账金额")
    private BigDecimal finalAmount;

    @ApiModelProperty(value = "入款类型编码")
    private String cashinTypeCode;

    @ApiModelProperty(value = "入款类型名称")
    private String cashinTypeName;

    @ApiModelProperty(value = "申请时间")
    private Date requestTime;

    @ApiModelProperty(value = "订单状态（0：待审核，1：已通过，2：已取消）")
    private Integer orderStatus;

    @ApiModelProperty(value = "审核时间")
    private Date operateTime;

    @ApiModelProperty(value = "操作者")
    private String operator;

    @ApiModelProperty(value = "备注")
    private String remark;
    
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
