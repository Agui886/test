package com.f.stock.schema;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import entity.UserFinancingInterestDayDetail;
import entity.UserFinancingInterestInfo;
import entity.UserInfo;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import lombok.extern.slf4j.Slf4j;
import service.IpAddressService;
import service.UserFinancingInterestDayDetailService;
import service.UserFinancingInterestInfoService;
import service.UserInfoService;
import service.UserStockPositionService;
import utils.OrderNumberGenerator;

@Component
@Slf4j
public class UserInterestTask {

	@Resource
	private UserFinancingInterestInfoService userFinancingInterestInfoService;

	@Resource
	private UserFinancingInterestDayDetailService userFinancingInterestDayDetailService;

	@Resource
	private UserInfoService userInfoService;

	@Resource
	private IpAddressService ipAddressService;

	@Resource
	private UserStockPositionService userStockPositionService;
	

	/**
	 * 每日用户利息结算-每天凌晨3点执行一次
	 * 
	 * @throws UnknownHostException
	 */
	@Scheduled(cron = "0 0 3 * * ?")
	@PostConstruct
	@Transactional
	public void doDailyUserInterestSettlement() throws UnknownHostException {
		log.info("====================每日用户利息结算-任务开始====================");
		Date now = new Date();
		// 1、查询-融资信息表"年化利息开始计算日期"小于等于当天的数据
		List<UserFinancingInterestInfo> userFinancingInterestInfoList = userFinancingInterestInfoService.lambdaQuery()
				.le(UserFinancingInterestInfo::getAnnualizedInterestTime, now)
				.eq(UserFinancingInterestInfo::getSettlement, false).list();
		// 2、迭代-融资信息数据
		for (UserFinancingInterestInfo userFinancingInterestInfo : userFinancingInterestInfoList) {
			// 2.1、计算当天利息，存入，每日融资明细数据中,当天只能有一条数据
			LambdaQueryWrapper<UserFinancingInterestDayDetail> qw = new LambdaQueryWrapper<>();
			qw.eq(UserFinancingInterestDayDetail::getInterestAccrualDate, new SimpleDateFormat("yyyyMMdd").format(now));
			qw.eq(UserFinancingInterestDayDetail::getFinancingInterestId, userFinancingInterestInfo.getId());
			UserFinancingInterestDayDetail userFinancingInterestDayDetai = userFinancingInterestDayDetailService
					.getOne(qw);
			if (userFinancingInterestDayDetai == null) {
				//剩余融资本金=融资金额-已结算融资本金
				BigDecimal residueFinancingAmount = userFinancingInterestInfo.getFinancingAmount().subtract(userFinancingInterestInfo.getSettlementedFinancingAmount()).setScale(6,RoundingMode.HALF_UP);			
				//每日利息 = 融资年化利率*365天/剩余融资本金
				BigDecimal todayInterestGenerated = userFinancingInterestInfo.getAnnualizedInterestRate()
						.divide(new BigDecimal(365), 6, RoundingMode.HALF_UP)
						.multiply(residueFinancingAmount).setScale(6, RoundingMode.HALF_UP);
				UserFinancingInterestDayDetail todayDayDetail = new UserFinancingInterestDayDetail();
				todayDayDetail.setUserId(userFinancingInterestInfo.getUserId());
				todayDayDetail.setFinancingInterestId(userFinancingInterestInfo.getId());
				todayDayDetail.setInterestAccrualDate(now);
				todayDayDetail.setSettlement(false);
				todayDayDetail.setInterestGenerated(todayInterestGenerated);
				todayDayDetail.insert();
			}
			// 2.2、通过"融资信息ID"、"未结算"、"当天"，计算每日融资明细数据的，"产生利息总额"
			QueryWrapper<UserFinancingInterestDayDetail> ufiddqw = new QueryWrapper<>();
			ufiddqw.select("ifnull(sum(interest_generated),0) as interest_generated");
			ufiddqw.eq("financing_interest_id", userFinancingInterestInfo.getId());
			ufiddqw.eq("settlement", false);
			Map<String, Object> map = userFinancingInterestDayDetailService.getMap(ufiddqw);
			// 2.2判断-"产生利息总额"超过0.01,即可结算，更改数据为已结算，得到利息
			BigDecimal interestGeneratedSum = (BigDecimal) map.get("interest_generated");
			interestGeneratedSum = interestGeneratedSum.setScale(2, RoundingMode.HALF_UP);
			//"产生利息总额"大于等于"0.0.1"
			if (interestGeneratedSum.compareTo(new BigDecimal(0.01)) > -1) {
				log.info("产生利息总额：{}", interestGeneratedSum);
				//2.2.2、如果本金不够扣除，不去进行计算
				UserInfo userInfo = userInfoService.getById(userFinancingInterestInfo.getUserId());
				//"用户可用金额"大于等于"产生利息总额"
				if (userInfo.getAvailableAmt().compareTo(interestGeneratedSum) > -1) {
					// 2.2.3、执行用户资金记录
					this.userInfoService.updateUserAvailableAmt(userFinancingInterestInfo.getUserId(),
							AmtDeTypeEnum.UserInterest, interestGeneratedSum, "每日利息结算", CurrencyEnum.CNY,
							BigDecimal.ONE, "127.0.0.1", "系统服务器", "系统",
							OrderNumberGenerator.create(100)
							);
					// 2.2.1、通过"融资信息ID"、"未结算"、"当天"，更改当前一批数据为已结算
					userFinancingInterestDayDetailService.lambdaUpdate()
							.eq(UserFinancingInterestDayDetail::getFinancingInterestId, userFinancingInterestInfo.getId())
							.eq(UserFinancingInterestDayDetail::getSettlement, false)
							.set(UserFinancingInterestDayDetail::getSettlement, true).update();
				}
			}
		}
		log.info("====================每日用户利息结算-任务结束====================");
	}
	
}
