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
 * ip信息表
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ip_address")
@ApiModel(value="IpAddress对象", description="ip信息表")
public class IpAddress extends Model<IpAddress> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String ip1;

    @ApiModelProperty(value = "IP1转换为数字")
    private Integer ip1Num;

    private String ip2;

    @ApiModelProperty(value = "IP2转换为数字")
    private Integer ip2Num;

    private String address1;

    @ApiModelProperty(value = "详细地址")
    private String address2;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
