package entity;

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
 * 国家地区信息表
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("country")
@ApiModel(value="Country对象", description="国家地区信息表")
public class Country extends Model<Country> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String code;

    @ApiModelProperty(value = "中文简称")
    private String nameCn;

    @ApiModelProperty(value = "英文简称")
    private String nameEn;

    private String areaCode;

    @ApiModelProperty(value = "排序值，值越小，排序优先级越高")
    private Integer sort;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
