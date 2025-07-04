package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.TutorInfo;
import entity.common.Response;
import vo.manager.TutorChangeRecordListSearchParamVO;
import vo.manager.TutorDataStatisticsSearchParamVO;
import vo.manager.TutorDataStatisticsVO;
import vo.server.FollowTutorDetailVO;
import vo.server.FollowTutorListParamVO;
import vo.server.FollowTutorListVO;
import vo.server.IndexTutorRecommendVo;

/**
 * <p>
 * 导师信息表 服务类
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
public interface TutorInfoService extends IService<TutorInfo> {
	
	Response<Void> add(TutorInfo tutorInfo, String ip, String operator);
	
	Response<Void> edit(TutorInfo tutorInfo, String ip, String operator);
	
	Response<Void> delete(Integer id, String ip, String operator);
	
	Response<Void> updateEnableStatus(Integer id, Boolean status, String ip, String operator);
	
	/**
	 * 客户端-跟投-导师列表
	 * @param page
	 * @param param
	 */
	void followTutorList(Page<FollowTutorListVO> page, FollowTutorListParamVO param);

	/**
	 * 数据报表-导师数据统计
	 * @param page
	 * @param param
	 */
	void tutorDataStatistics(Page<TutorDataStatisticsVO> page, TutorDataStatisticsSearchParamVO param);
	
}
