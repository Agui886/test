package service.impl;

import entity.CashinRecord;
import entity.UserRebateDetail;
import entity.common.Response;
import mapper.CashinRecordMapper;
import service.CashinRecordService;
import vo.manager.CashInOutStatisticsVO.CashInOutStatistics;
import vo.manager.RebateStatisticsVO.RebateStatistics;
import vo.manager.CashinRecordListParamVO;
import vo.manager.CashinRecordListVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashinRecordVO;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员充值记录表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
@Service
public class CashinRecordServiceImpl extends ServiceImpl<CashinRecordMapper, CashinRecord> implements CashinRecordService {

	@Resource
	private CashinRecordMapper cashinRecordMapper;

	/**
	 * 管理系统-资金管理-充值列表
	 */
	@Override
	public Response<CashinRecordListVO> managerList(CashinRecordListParamVO param) {
		CashinRecordListVO vo = new CashinRecordListVO();
		Page<CashinRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		cashinRecordMapper.managerList(page, param);
		QueryWrapper<CashinRecord> qw = new QueryWrapper<>();
		qw.select("ifnull(sum(order_amount),0) as orderAmount, ifnull(sum(final_amount),0) as finalAmount");
		Map<String, Object> map = this.getMap(qw);
		vo.setTotalOrderAmount((BigDecimal) map.get("orderAmount"));
		vo.setTotalFinalAmount((BigDecimal) map.get("finalAmount"));
		vo.setPage(page);
		return Response.successData(vo);
	}

	/**
	 * 资产-入金-记录
	 */
	@Override
	public void record(Page<CashinRecordVO> page, CashinAndOutRecordParamVO param) {
		cashinRecordMapper.record(page, param);
	}

	/**
	 * 充提/提现统计-充值统计
	 */
	@Override
	public CashInOutStatistics getCashInOutStatistics(Date startTime, Date endTime, Integer orderStatus) {
		CashInOutStatistics cashInOutStatistics = new CashInOutStatistics();
		//查询统计数据
		QueryWrapper<CashinRecord> cqw = new QueryWrapper<>();
		if (orderStatus != null) {
			cqw.eq("order_status", orderStatus);//订单状态
			if (orderStatus == 0) {//申请时间
				if (startTime != null) {
					cqw.ge("request_time", startTime);//大于等于
				}
				if (endTime != null) {
					cqw.le("request_time", endTime);//小于等于
				}
			}else if (orderStatus == 1) {//审核时间
				if (startTime != null) {
					cqw.ge("operate_time", startTime);//大于等于
				}
				if (endTime != null) {
					cqw.le("operate_time", endTime);//小于等于
				}
			}
		}
		cqw.select("COALESCE(SUM(order_amount), 0) AS total_order_amount, "//订单金额总额
			     + "COALESCE(SUM(final_amount), 0) AS total_final_amount, "//到账金额总额
			     + "COUNT(DISTINCT user_id) AS distinct_user_count, "//去重会员总数
			     + "COUNT(*) AS total_records "//总记录数
				);
		Map<String, Object> map = this.getMap(cqw);
		//存储入对象中
		cashInOutStatistics.setTotalOrderAmount((BigDecimal)map.get("total_order_amount"));
		cashInOutStatistics.setTotalFinalAmount((BigDecimal)map.get("total_final_amount"));
		cashInOutStatistics.setDistinctUserCount(((Long)map.get("distinct_user_count")).intValue());
		cashInOutStatistics.setTotalRecords(((Long)map.get("total_records")).intValue());
		return cashInOutStatistics;
	}

}
