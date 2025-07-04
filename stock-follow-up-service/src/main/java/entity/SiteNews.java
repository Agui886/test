package entity;

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
 * 新闻信息表
 * </p>
 *
 * @author 
 * @since 2025-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("site_news")
@ApiModel(value="SiteNews对象", description="新闻信息表")
public class SiteNews extends Model<SiteNews> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "新闻主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "新闻类型：1、财经要闻，2、经济数据，3、全球股市，4、7*24全球，5、商品资讯，6、上市公司，7、全球央行")
    private Integer type;

    @ApiModelProperty(value = "新闻标题")
    private String title;

    @ApiModelProperty(value = "来源id，新闻的唯一标识")
    private String sourceId;

    @ApiModelProperty(value = "来源名称")
    private String sourceName;

    @ApiModelProperty(value = "浏览量")
    private Integer views;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @ApiModelProperty(value = "发布时间")
    private Date showTime;

    @ApiModelProperty(value = "图片地址")
    private String imgUrl;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "新闻内容")
    private String content;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
