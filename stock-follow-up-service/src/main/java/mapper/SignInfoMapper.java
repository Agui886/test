package mapper;

import entity.SignInfo;
import vo.manager.SignListSearchParamVO;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 跟投信号表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
public interface SignInfoMapper extends BaseMapper<SignInfo> {
	
	Page<SignInfo> managerList(Page<SignInfo> page, @Param("param") SignListSearchParamVO param);
	
}
