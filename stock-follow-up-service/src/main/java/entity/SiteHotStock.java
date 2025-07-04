package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 站点热门股票
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("site_hot_stock")
@ApiModel(value="SiteHotStock对象", description="站点热门股票")
public class SiteHotStock extends Model<SiteHotStock> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @ApiModelProperty(value = "板块名称，如：科创、创业")
    private String stockPlate;

    @ApiModelProperty(value = "搜索次数")
    private Integer searchTimes;

    @ApiModelProperty(value = "最新价")
	@TableField(exist = false)
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "涨幅比例")
	@TableField(exist = false)
	private BigDecimal percentageIncrease;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
