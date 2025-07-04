package mapper;

import entity.UserFollowUpPosition;
import vo.manager.FollowPositionListSearchParamVO;
import vo.manager.FundAnalysisStatisticsPositionResultVO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 用户-跟投-持仓信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-28
 */
public interface UserFollowUpPositionMapper extends BaseMapper<UserFollowUpPosition> {

	/**
	 * 跟投管理-用户跟投持仓
	 * @param page
	 * @param param
	 * @return
	 */
	Page<UserFollowUpPosition> managerList(Page<UserFollowUpPosition> page,@Param("param") FollowPositionListSearchParamVO param);

	/**
	 * 资金分布（资金分析统计）-查询持仓市值统计list(查user_stock_position、user_follow_up_position两张表)
	 * @return
	 */
	List<FundAnalysisStatisticsPositionResultVO> fundAnalysisStatisticsPositionResultVOList();

	/**
	 * 跟投记录id，查持仓记录，股票分组统计
	 * @param followRecordId
	 * @return
	 */
	List<FundAnalysisStatisticsPositionResultVO> getFollowPositionStatisticsList(Integer followRecordId);

}
