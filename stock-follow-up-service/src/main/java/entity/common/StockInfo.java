package entity.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import enums.StockTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import utils.StringUtil;

/**
 * <p>
 * 股票产品信息表
 * </p>
 *
 * @author 
 * @since 2024-11-07
 */
@Data
@ApiModel(value="StockInfo对象", description="股票产品信息表")
public class StockInfo extends Model<StockInfo> {

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

    @ApiModelProperty(value = "是否锁定，锁定后无法交易")
    private Boolean isLock;

    @ApiModelProperty(value = "是否展示")
    private Boolean isShow;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @ApiModelProperty(value = "点差费率")
    private BigDecimal spreadRate;

    @ApiModelProperty(value = "大宗交易开关，false-关闭，true-开启")
    private Boolean isBlockTrading;

    @ApiModelProperty(value = "大宗交易增发价格")
    private BigDecimal blockTradingPrice;

    @ApiModelProperty(value = "大宗交易增发数量")
    private Integer blockTradingNum;

    @ApiModelProperty(value = "已出售的大宗交易股数")
    private Integer soldBlockTradingNum;

    @ApiModelProperty(value = "大宗交易最低买进股数")
    private Integer blockTradingBuyingMinNum;
    
    @ApiModelProperty(value = "锁仓T+N")
    private Integer lockInPeriod;

    @ApiModelProperty(value = "创建者")
    private String creator;
    
    @ApiModelProperty(value = "大宗交易验证口令")
    private String blockTradingPwd;
    
    @ApiModelProperty(value = "东财-股票搜索-股票market信息")
    private Integer market;
    
    @ApiModelProperty(value = "涨幅比例")
    @TableField(exist = false)
    private BigDecimal percentageIncrease;
    
//    @ApiModelProperty(value = "3天内涨幅比例")
//    @TableField(exist = false)
//    private BigDecimal percentageIncreaseIn3Days;
    
    @ApiModelProperty(value = "当前价格")
    @TableField(exist = false)
    private BigDecimal nowPrice;
    
    @ApiModelProperty(value = "当天最高价格")
    @TableField(exist = false)
    private BigDecimal heightPrice;
    
    @ApiModelProperty(value = "当天最低价格")
    @TableField(exist = false)
    private BigDecimal lowPrice;
    
    @ApiModelProperty(value = "开盘价格")
    @TableField(exist = false)
    private BigDecimal openPrice;
    
    @ApiModelProperty(value = "成交额")
    @TableField(exist = false)
    private BigDecimal volume;
    
    @ApiModelProperty(value = "成交量")
    @TableField(exist = false)
    private BigDecimal turnover;
    
    @ApiModelProperty(value = "昨日收盘价格")
    @TableField(exist = false)
    private BigDecimal prevClose;
    
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("股票id：").append(this.id);
    	sb.append("\n名称：").append(this.stockName ).append("(").append(this.stockCode).append(")");
    	sb.append("\n类型：").append(StockTypeEnum.getNameByCode(this.stockType));
    	if(!StringUtil.isEmpty(this.stockPlate)) {
    		sb.append("\n板块：").append(this.stockPlate);
    	}
    	if(this.isLock != null)
    		sb.append("\n是否锁定：").append(this.isLock ? "是" : "否");
    	if(this.isShow != null)
    		sb.append("\n是否显示：").append(this.isShow ? "是" : "否");
    	if(this.isBlockTrading != null)
    		sb.append("\n是否开启大宗交易：").append(this.isBlockTrading ? "是" : "否");
    	if (this.blockTradingPrice != null) {
			sb.append("\n增发价格：").append(this.blockTradingPrice);
		}
    	if (this.blockTradingNum != null) {
			sb.append("\n增发数量：").append(this.blockTradingNum);
		}
    	if(this.blockTradingBuyingMinNum != null) {
    		sb.append("\n大宗交易最低买进股数：").append(this.blockTradingBuyingMinNum);
    	}
    	if (this.lockInPeriod != null) {
			sb.append("\n锁仓天数：").append(this.lockInPeriod);
		}
    	if(this.spreadRate != null) {
    		sb.append("\n点差费率：").append(this.spreadRate);
    	}
    	if(!StringUtil.isEmpty(this.blockTradingPwd)) {
    		sb.append("\n大宗交易验证口令：").append(this.blockTradingPwd);
    	}
    	return sb.toString();
    }
    
    public String getStockPlate() {
    	if (StringUtil.isNotEmpty(stockCode)) {
    		if(stockCode.startsWith("688") && stockCode.equals("sh")) {
    			return "科创";
    		} else if(stockCode.equals("sz") && stockCode.startsWith("30")) {
    			return "创业";
    		}
		}
		return "";
    }
    
}
