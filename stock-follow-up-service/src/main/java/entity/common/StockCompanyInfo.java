package entity.common;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 股票公司信息表
 * </p>
 *
 * @author 
 * @since 2025-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_company_info")
@ApiModel(value="StockCompanyInfo对象", description="股票公司信息表")
public class StockCompanyInfo extends Model<StockCompanyInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "股票类型")
    private String stockType;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "公司名称")
    private String orgName;

    @ApiModelProperty(value = "公司英文名称")
    private String orgEnAbbr;

    @ApiModelProperty(value = "所属行业")
    private String belongIndustry;

    @ApiModelProperty(value = "主席")
    private String chairman;

    @ApiModelProperty(value = "成立日期")
    private String foundDate;

    @ApiModelProperty(value = "员工人数")
    private String empNum;

    @ApiModelProperty(value = "注册地址")
    private String regPlace;

    @ApiModelProperty(value = "办公地址")
    private String address;

    @ApiModelProperty(value = "公司网址")
    private String orgWeb;

    @ApiModelProperty(value = "电邮地址")
    private String orgMail;

    @ApiModelProperty(value = "电话号码")
    private String orgTel;

    @ApiModelProperty(value = "传真号码")
    private String orgFax;

    @ApiModelProperty(value = "公司介绍")
    private String orgProfile;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
