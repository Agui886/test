package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import utils.StringUtil;

/**
 * <p>
 * 导师信息表
 * </p>
 *
 * @author 
 * @since 2025-04-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tutor_info")
@ApiModel(value="TutorInfo对象", description="导师信息表")
@Slf4j
public class TutorInfo extends Model<TutorInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "导师头像")
    private String avatarPic;

    @ApiModelProperty(value = "导师名称")
    private String tutorName;

    @ApiModelProperty(value = "导师简介")
    private String tutorProfile;

    @ApiModelProperty(value = "域类型，0-公开，1-私密")
    private Integer domainType;

    @ApiModelProperty(value = "私域邀请码")
    private String privateInvitationCode;

    @ApiModelProperty(value = "排序值，值越小排序优先级越高")
    private Integer sort;

    @ApiModelProperty(value = "从业经验（年限）")
    private Integer experience;

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "是否可跟投，1-启用，0-禁止")
    private Boolean isEnabled;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "项目配置")
    @JsonIgnore
    private String configurationJson;

    public String getConfigurationJson() {
    	if(configurationJson == null && configuration != null && configuration.size() > 0) {
    		try {
    			configurationJson = JSONUtil.toJsonStr(configuration);
        	} catch(Exception e) {
        		log.error("configuration转JSON字符串异常", e);
        	}
    	}
    	return configurationJson;
    }
    
    @TableField(exist = false)
    @ApiModelProperty(value = "项目配置")
    private List<Configuration> configuration;
    
    public List<Configuration> getConfiguration() {
    	if(configuration == null && !StringUtil.isEmpty(configurationJson)) {
    		try {
    			configuration = JSONUtil.toList(configurationJson, Configuration.class);
        	} catch(Exception e) {
        		log.error("configurationJson转对象异常", configurationJson);
        	}
    	}
    	return configuration;
    }
    
    @JsonIgnore
    public String getConfigurationContent() {
    	List<Configuration> list = this.getConfiguration();
    	if(list == null || list.size() == 0) {
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	for(Configuration c : list) {
    		String cs = c.toString();
    		if(cs == null) {
    			continue;
    		}
    		sb.append(c.toString()).append("\n");
    	}
    	return sb.toString();
    }
    
    public static final int DOMAIN_PUBLIC = 0;
    
    public static final int DOMAIN_PRIVATE = 1;
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Data
    public static class Configuration {
    	
    	@ApiModelProperty(value = "项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    	private int itemType;
    	
    	@ApiModelProperty(value = "最小跟投金额")
    	private BigDecimal minAmount;
    	
    	@ApiModelProperty(value = "最大跟投金额")
    	private BigDecimal maxAmount;
    	
    	@ApiModelProperty(value = "导师抽佣比例%")
    	private BigDecimal  tutorCommissionRatio;
    	
    	@ApiModelProperty(value = "平台抽佣比例%")
    	private BigDecimal  platformCommissionRatio;
    	
    	@ApiModelProperty(value = "开启状态")
    	private Boolean isEnabled;
    	
    	@ApiModelProperty(value = "上月收益")
    	private BigDecimal monthYield;
    	
    	@ApiModelProperty(value = "上月胜率")
    	private BigDecimal winRate;
    	
    	public String toString() {
    		if(this.minAmount == null && this.maxAmount == null && this.tutorCommissionRatio == null && this.platformCommissionRatio == null && this.isEnabled) {
    			return null;
    		}
    		StringBuilder sb = new StringBuilder();
    		sb.append("[");
			switch(itemType) {
			default:
				sb.append("每日跟投");
				break;
			case 1:
				sb.append("7日跟投");
				break;
			case 2:
				sb.append("16日跟投");
				break;
			case 3:
				sb.append("38日跟投");
				break;
			case 4:
				sb.append("108日跟投");
				break;
			case 5:
				sb.append("180日跟投");
				break;
			case 6:
				sb.append("360日跟投");
				break;
			}
			if(this.minAmount != null) {
    			sb.append("][最小跟投金额：").append(this.minAmount);
    		}
			if(this.maxAmount != null) {
    			sb.append("][最大跟投金额：").append(this.maxAmount);
    		}
			if(this.tutorCommissionRatio != null) {
    			sb.append("][导师抽佣：").append(this.tutorCommissionRatio).append("%");
    		}
			if(this.platformCommissionRatio != null) {
				sb.append("][平台抽佣：").append(this.platformCommissionRatio).append("%");
    		}
			if(this.isEnabled != null) {
				sb.append("][状态：").append(this.isEnabled ? "开启" : "关闭");
    		}
			if(this.monthYield != null) {
				sb.append("][月收益：").append(this.monthYield);
    		}
			if(this.winRate != null) {
				sb.append("][胜率：").append(this.winRate);
    		}
			sb.append("]");
    		return sb.toString();
    	}
    }
    
    public Configuration getConfigurationByItemType(int itemType) {
    	List<Configuration> configurationList = this.getConfiguration();
    	if (configurationList != null && configurationList.size() > 0) {
			for(Configuration configurationObject : configurationList){
				if (configurationObject.getItemType() == itemType) {
					return configurationObject;
				}
			}
		}
    	return null;
    }
    
}
