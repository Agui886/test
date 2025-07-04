package service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.UserInfo;
import entity.UserRebateDetail;
import entity.common.Response;
import mapper.UserRebateDetailMapper;
import service.UserInfoService;
import service.UserRebateDetailService;
import vo.manager.RebateDetailListVO;
import vo.manager.RebateDetailParamVO;
import vo.manager.RebateRecordParamVO;
import vo.manager.RebateRecordVO;
import vo.manager.RebateStatisticsVO.RebateStatistics;
import vo.server.UserRebateDetailParamVO;
import vo.server.UserRebateDetailVO;

/**
 * <p>
 * 用户返佣记录明细 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-21
 */
@Service
public class UserRebateDetailServiceImpl extends ServiceImpl<UserRebateDetailMapper, UserRebateDetail> implements UserRebateDetailService {

	@Resource
	private UserRebateDetailMapper userRebateDetailMapper;
	
	@Resource
	private UserInfoService userInfoService;
	
	/**
	 * 获取返佣统计数据
	 */
	@Override
	public RebateStatistics getRebateStatistics(Integer userId, Date startTime, Date endTime, Integer levelVal) {
		RebateStatistics rebateStatistics = new RebateStatistics();
		//查询统计数据
		QueryWrapper<UserRebateDetail> cqw = new QueryWrapper<>();
		cqw.eq("is_settlemented", 1);//是否结算,已结算
		if (userId != null) {
			cqw.eq("user_id", userId);
		}
		if (levelVal != null) {
			cqw.eq("from_user_level_val", levelVal);
		}
		if (startTime != null) {
			cqw.ge("settlement_date", startTime);//大于等于
		}
		if (endTime != null) {
			cqw.le("settlement_date", endTime);//小于等于
		}
		cqw.select("COALESCE(SUM(rebate_amount), 0) AS total_rebate_amount, "//返佣总金额
				+ "COALESCE(SUM(performance), 0) AS total_performance, "//贡献用户当时总业绩
				+ "COUNT(DISTINCT from_user_id) AS total_from_user_id, "//贡献用户总数（贡献用户去重）
				+ "COUNT(DISTINCT user_id) AS total_user_id "//获得返佣用户总数（用户去重）
				);
		Map<String, Object> map = this.getMap(cqw);
		//存储入对象中
		rebateStatistics.setTotalRebateAmount((BigDecimal)map.get("total_rebate_amount"));
		rebateStatistics.setTotalPerformance((BigDecimal)map.get("total_performance"));
		rebateStatistics.setTotalFromUserId(((Long)map.get("total_from_user_id")).intValue());
		rebateStatistics.setTotalUserId(((Long)map.get("total_user_id")).intValue());
		return rebateStatistics;
	}

	/**
	 * 我的-资产-代理中心-返佣明细-会员返佣详情
	 */
	@Override
	public Response<UserRebateDetailVO> userRebateDetail(UserRebateDetailParamVO param) {
		UserRebateDetailVO userRebateDetailVO = new UserRebateDetailVO();
		//1.设置统计数据
		RebateStatistics rebateStatistics = getRebateStatistics(param.getUserId(), param.getSettlementDateStart(), param.getSettlementDateEnd(), null);
		userRebateDetailVO.setRebateStatistics(rebateStatistics);
		//2.设置list分页
		Page<UserRebateDetail> page = new Page<>(param.getPageNo(), param.getPageSize());
		userRebateDetailMapper.userRebateDetail(page,param);
		userRebateDetailVO.setPage(page);
		return Response.successData(userRebateDetailVO);
	}

	/**
	 * 返佣用户list，返佣金额总计排序
	 */
	@Override
	public List<RebateStatistics> getUserRebateStatistics(Integer userId, Date startTime, Date endTime,
			Integer levelVal) {
		//查询统计数据
		QueryWrapper<UserRebateDetail> cqw = new QueryWrapper<>();
		cqw.eq("is_settlemented", 1);//是否结算,已结算
		if (userId != null) {
			cqw.eq("user_id", userId);
		}
		if (levelVal != null) {
			cqw.eq("from_user_level_val", levelVal);
		}
		if (startTime != null) {
			cqw.ge("settlement_date", startTime);//大于等于
		}
		if (endTime != null) {
			cqw.le("settlement_date", endTime);//小于等于
		}
		cqw.select("user_id, "//用户id
				+ "COALESCE(SUM(rebate_amount), 0) AS total_rebate_amount, "//返佣总金额
				+ "COALESCE(SUM(performance), 0) AS total_performance, "//贡献用户当时总业绩
				+ "COUNT(DISTINCT from_user_id) AS total_from_user_id, "//贡献用户总数（贡献用户去重）
				+ "COUNT(DISTINCT user_id) AS total_user_id "//获得返佣用户总数（用户去重）
				);
		cqw.groupBy("user_id");         // GROUP BY user_id分组
		cqw.orderByDesc("total_rebate_amount");  // 返佣总金额，排序
		cqw.last("LIMIT 10");  // 限制返回前10条记录
		List<Map<String, Object>> mapList = this.listMaps(cqw);
		//存储list对象
		List<RebateStatistics> rebateStatisticsList = mapList.stream().map(entity -> {
			RebateStatistics rebateStatistics = new RebateStatistics();
			//存储入对象中
			rebateStatistics.setUserId((Integer)entity.get("user_id"));
			rebateStatistics.setTotalRebateAmount((BigDecimal)entity.get("total_rebate_amount"));
			rebateStatistics.setTotalPerformance((BigDecimal)entity.get("total_performance"));
			rebateStatistics.setTotalFromUserId(((Long)entity.get("total_from_user_id")).intValue());
			rebateStatistics.setTotalUserId(((Long)entity.get("total_user_id")).intValue());
			UserInfo userInfo = userInfoService.getById(rebateStatistics.getUserId());
			if (userInfo != null) {
				rebateStatistics.setNickname(userInfo.getNickname());
			}
			return rebateStatistics;
		}).collect(Collectors.toList());
		
		return rebateStatisticsList;
	}

	@Override
	public void managerList(Page<RebateRecordVO> page, RebateRecordParamVO param) {
		this.userRebateDetailMapper.managerList(page, param);
	}

	@Override
	public void managerDetailList(Page<RebateDetailListVO> page, RebateDetailParamVO param) {
		this.userRebateDetailMapper.managerDetailList(page, param);
	}
}
