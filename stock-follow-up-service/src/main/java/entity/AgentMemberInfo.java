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
 * 代理的下级会员信息表
 * </p>
 *
 * @author 
 * @since 2025-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("agent_member_info")
@ApiModel(value="AgentMemberInfo对象", description="代理的下级会员信息表")
public class AgentMemberInfo extends Model<AgentMemberInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "本级代理ID")
    private Integer agentId;

    @ApiModelProperty(value = "下级成员的直属上级代理ID")
    private Integer memberAgentId;

    @ApiModelProperty(value = "下级成员用户ID")
    private Integer memberUserId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
