package mapper;

import entity.UserAmtChangeRecord;
import vo.manager.UserAmtChangeRecordSearchParamVO;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 会员资金变更记录表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
public interface UserAmtChangeRecordMapper extends BaseMapper<UserAmtChangeRecord> {

	Page<UserAmtChangeRecord> managerList(Page<UserAmtChangeRecord> page, @Param("param") UserAmtChangeRecordSearchParamVO param);

}
