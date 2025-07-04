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
 * 用户认证信息表
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_real_auth_info")
@ApiModel(value="UserRealAuthInfo对象", description="用户认证信息表")
public class UserRealAuthInfo extends Model<UserRealAuthInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "证件类型：0-大陆身份证，1-护照")
    private Integer certificateType;

    @ApiModelProperty(value = "证件号")
    private String certificateNumber;

    @ApiModelProperty(value = "姓名")
    private String realName;

    @ApiModelProperty(value = "性别：0-男，1-女")
    private Integer gender;

    @ApiModelProperty(value = "身份证正面照片")
    private String idFrontImg;

    @ApiModelProperty(value = "身份证背面照片")
    private String idBackImg;

    @ApiModelProperty(value = "身份证手持照片")
    private String idHandImg;

    @ApiModelProperty(value = "护照照片")
    private String passportDataImg;

    @ApiModelProperty(value = "英文姓氏")
    private String enLastName;

    @ApiModelProperty(value = "英文名字")
    private String enFirstName;

    @ApiModelProperty(value = "英文中间名")
    private String enMiddleName;

    @ApiModelProperty(value = "国家地区")
    private String region;

    @ApiModelProperty(value = "生日日期")
    private Date birthday;

    @ApiModelProperty(value = "证件有效期")
    private Date certificateValidityPeriod;

    @ApiModelProperty(value = "申请时间")
    private Date requestTime;

    @ApiModelProperty(value = "操作、审核时间")
    private Date operateTime;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "注册时间")
    private Date regTime;

    @ApiModelProperty(value = "注册的IP")
    private String regIp;

    @ApiModelProperty(value = "注册的ip地址详情")
    private String regAddress;

    @ApiModelProperty(value = "订单状态（1：审核中，2：已通过，3：不通过）")
    private Integer realAuthStatus;

    @ApiModelProperty(value = "报税地区")
    private String taxFilingArea;

    @ApiModelProperty(value = "用户签名")
    private String signature;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
