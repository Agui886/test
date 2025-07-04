package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 跟投信号表
 * </p>
 *
 * @author 
 * @since 2025-05-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sign_info")
@ApiModel(value="SignInfo对象", description="跟投信号表")
public class SignInfo extends Model<SignInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "导师id")
    private Integer tutorId;

    @ApiModelProperty(value = "项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    private Integer itemType;

    @ApiModelProperty(value = "信号类型,0-买入,1-卖出")
    private Integer signType;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "交易股票代码")
    private String stockCode;

    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @ApiModelProperty(value = "仓位，比如0.25为1/4仓，0.5为1/2仓，1为全仓")
    private BigDecimal position;

    @ApiModelProperty(value = "指导价")
    private BigDecimal price1;

    @ApiModelProperty(value = "一手的指导价")
    private BigDecimal price2;

    @ApiModelProperty(value = "一手的股数（美股/港股：通过接口获取实时数据，A股：所有股票每手股数固定为100股）")
    private Integer sharesOfHand;

    @ApiModelProperty(value = "跟随人数")
    private Integer followers;

    @ApiModelProperty(value = "显示发布时间")
    private Date displayReleaseTime;

    @ApiModelProperty(value = "实际发布时间，创建时间")
    private Date realReleaseTime;

    @ApiModelProperty(value = "发布人")
    private String publisher;

    @ApiModelProperty(value = "是否已配置项目")
    private Boolean isConfigured;

    @ApiModelProperty(value = "买入信号id")
    private Integer buySignId;
    
    @ApiModelProperty(value = "跟上人数")
    private Integer followerUps;

    @TableField(exist = false)
    @ApiModelProperty(value = "导师名称")
    private String tutorName;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "买入信号-指导价")
    private BigDecimal buyPrice1;

    @TableField(exist = false)
    @ApiModelProperty(value = "收益率=买入信号-指导价/指导价(只有卖出数据才会有)")
    private BigDecimal yieldRate;
    
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    
    // 收益率计算逻辑
    public BigDecimal getYieldRate() {
        // 仅当 signType=1（卖出）时计算
        if (signType != null && signType == 1) {
            // 检查必要参数非空且非零
            if (buyPrice1 != null && price1 != null 
                && BigDecimal.ZERO.compareTo(buyPrice1) != 0) { // 避免除零
            	// 计算收益率： (price1 / buyPrice1) - 1
                // 1. 先计算除法（保留8位中间精度避免舍入误差）
                BigDecimal ratio = price1.divide(buyPrice1, 8, RoundingMode.HALF_UP);
                // 2. 减1得到收益率
                BigDecimal result = ratio.subtract(BigDecimal.ONE);
                // 3. 最终结果保留4位小数
                return result.setScale(4, RoundingMode.HALF_UP);
            }
        }
        return null; // 非卖出信号或数据不全时返回null
    }

}
