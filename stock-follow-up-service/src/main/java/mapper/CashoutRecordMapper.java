package mapper;

import entity.CashoutRecord;
import vo.manager.CashoutRecordListParamVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashoutRecordVO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 提现记录表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
public interface CashoutRecordMapper extends BaseMapper<CashoutRecord> {

	/**
	 * 管理系统-资金管理-提现列表
	 * @param page
	 * @param param
	 */
	Page<CashoutRecord> managerList(Page<CashoutRecord> page, CashoutRecordListParamVO param);

	/**
	 * 资产-出金-记录
	 * @param page
	 * @param param
	 */
	Page<CashoutRecordVO> record(Page<CashoutRecordVO> page, CashinAndOutRecordParamVO param);

}
