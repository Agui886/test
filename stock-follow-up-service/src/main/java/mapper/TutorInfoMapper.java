package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.TutorInfo;
import vo.manager.TutorDataStatisticsSearchParamVO;
import vo.manager.TutorDataStatisticsVO;
import vo.server.FollowTutorListParamVO;
import vo.server.FollowTutorListVO;

/**
 * <p>
 * 导师信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
public interface TutorInfoMapper extends BaseMapper<TutorInfo> {

	/**
	 * 客户端-跟投-导师列表
	 * @param page
	 * @param param
	 */
	Page<FollowTutorListVO> followTutorList(Page<FollowTutorListVO> page, @Param("param") FollowTutorListParamVO param);

	/**
	 * 数据报表-导师数据统计
	 * @param page
	 * @param param
	 */
	Page<TutorDataStatisticsVO> tutorDataStatistics(Page<TutorDataStatisticsVO> page, TutorDataStatisticsSearchParamVO param);

}
