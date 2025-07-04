package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserRebateDetail;
import vo.manager.RebateDetailListVO;
import vo.manager.RebateDetailParamVO;
import vo.manager.RebateRecordParamVO;
import vo.manager.RebateRecordVO;
import vo.server.UserRebateDetailParamVO;

/**
 * <p>
 * 用户返佣记录明细 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-21
 */
public interface UserRebateDetailMapper extends BaseMapper<UserRebateDetail> {

	/**
	 * 我的-资产-代理中心-返佣明细-会员返佣详情-返佣列表分页数据
	 * @param page
	 * @param param
	 * @return
	 */
	Page<UserRebateDetail> userRebateDetail(Page<UserRebateDetail> page, UserRebateDetailParamVO param);

	Page<RebateRecordVO> managerList(Page<RebateRecordVO> page, @Param("param") RebateRecordParamVO param);
	
	Page<RebateDetailListVO> managerDetailList(Page<RebateDetailListVO> page, @Param("param") RebateDetailParamVO param);
}
