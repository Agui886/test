package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.util.Random;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户自选股票信息
 * </p>
 *
 * @author 
 * @since 2025-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_favorite_stock")
@ApiModel(value="UserFavoriteStock对象", description="用户自选股票信息")
public class UserFavoriteStock extends Model<UserFavoriteStock> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @ApiModelProperty(value = "板块名称，如：科创、创业")
    private String stockPlate;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;


    @ApiModelProperty(value = "最新价")
	@TableField(exist = false)
	//@JsonSerialize(using = BigDecimalSerizlize.class)
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "涨幅比例")
	@TableField(exist = false)
	//@JsonSerialize(using = BigDecimalSerizlize.class)
	private BigDecimal percentageIncrease;

	@ApiModelProperty(value = "昨日收盘价格")
	@TableField(exist = false)
	private BigDecimal prevClose;

	@ApiModelProperty(value = "走势")
	@TableField(exist = false)
	private BigDecimal[] trends;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
	
	public BigDecimal[] getTrends() {
		if(prevClose == null || nowPrice == null) {
			return trends;
		}
		trends = new BigDecimal[10];
		trends[0] = prevClose;
		trends[9] = nowPrice;
		Random random = new Random();
		double min, max;
		if(nowPrice.compareTo(prevClose) == 1) {
			min = this.prevClose.doubleValue();
	        max = this.nowPrice.doubleValue();
		} else {
			min = this.nowPrice.doubleValue();
	        max = this.prevClose.doubleValue();
		}
        for(int i = 1; i < 9; i++) {
        	trends[i] =  new BigDecimal( min + random.nextDouble() * (max - min)).setScale(2, RoundingMode.DOWN);
        }
		return trends;
	}

}
