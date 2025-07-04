package vo.server;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 用户认证信息表
 * </p>
 *
 * @author 
 * @since 2024-12-13
 */
@Data
public class AddUserRealAuthInfoVO  {

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

    @ApiModelProperty(value = "生日日期, 参数格式要传完整的 yyyy-MM-dd HH:mm:ss才能接收，如1989-01-01 00:00:00")
    private Date birthday;

    @ApiModelProperty(value = "证件有效期")
    private Date certificateValidityPeriod;
    
    @ApiModelProperty(value = "报税地区")
    private String taxFilingArea;
    
    @ApiModelProperty(value = "签名")
    private String signature;

}
