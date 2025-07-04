package mapper;

import entity.CashinRecord;
import vo.manager.CashinRecordListParamVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashinRecordVO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 会员充值记录表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
public interface CashinRecordMapper extends BaseMapper<CashinRecord> {

	/**
	 * 管理系统-资金管理-充值列表
	 * @param page
	 * @param param
	 */
	Page<CashinRecord> managerList(Page<CashinRecord> page, CashinRecordListParamVO param);

	/**
	 * 资产-入金-记录
	 * @param page
	 * @param param
	 */
	Page<CashinRecordVO> record(Page<CashinRecordVO> page, CashinAndOutRecordParamVO param);

}
