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
 * 代理上下级关系表
 * </p>
 *
 * @author 
 * @since 2025-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("agent_parent_info")
@ApiModel(value="AgentParentInfo对象", description="代理上下级关系表")
public class AgentParentInfo extends Model<AgentParentInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "本级代理ID")
    private Integer agentId;

    @ApiModelProperty(value = "下级代理的直属上级代理ID")
    private Integer memberParentId;

    @ApiModelProperty(value = "下级成员代理ID")
    private Integer memberAgentId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
