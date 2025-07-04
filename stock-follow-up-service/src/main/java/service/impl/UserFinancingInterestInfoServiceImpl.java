package service.impl;

import entity.UserFinancingInterestDayDetail;
import entity.UserFinancingInterestInfo;
import entity.UserInfo;
import entity.UserStockPosition;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import mapper.UserFinancingInterestInfoMapper;
import service.SysParamConfigService;
import service.UserFinancingInterestDayDetailService;
import service.UserFinancingInterestInfoService;
import service.UserInfoService;
import service.UserStockPositionService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户融资信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Service
public class UserFinancingInterestInfoServiceImpl extends ServiceImpl<UserFinancingInterestInfoMapper, UserFinancingInterestInfo> implements UserFinancingInterestInfoService {

	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserFinancingInterestDayDetailService userFinancingInterestDayDetailService;
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	/**
	 * 融资还款
	 */
	@Override
	public void doFinancingRepayment(Integer userId, BigDecimal amt, String deSummary, CurrencyEnum currency,
			BigDecimal exchangeRate, String ip, String ipAddress, String operator,String businessOrderSn) {
		//还款金额
		BigDecimal deAmt;
		//根据币种换算成人名币
		switch(currency) {
		case CNY:
		default:
			deAmt = amt;
			break;
		case HKD:
		case USD:
			deAmt = amt.divide(exchangeRate, 2, RoundingMode.HALF_UP);
			break;
		}
		// 1、查询-融资信息表
		List<UserFinancingInterestInfo> userFinancingInterestInfoList = this.lambdaQuery()
						.eq(UserFinancingInterestInfo::getSettlement, false)
						.eq(UserFinancingInterestInfo::getUserId, userId)
						.orderByAsc(UserFinancingInterestInfo::getCreateTime)
						.list();
		for (UserFinancingInterestInfo userFinancingInterestInfo: userFinancingInterestInfoList) {
			//剩余融资金额=融资金额-已结算融资本金
			BigDecimal residuegetFinancingAmount = userFinancingInterestInfo.getFinancingAmount().subtract(userFinancingInterestInfo.getSettlementedFinancingAmount());			
			//剩余融资金额，与还款金额比较
			switch (residuegetFinancingAmount.compareTo(deAmt)) {
			case -1://剩余融资金额<还款金额
				//1、剩余融资金额不变，2、还款金额=还款金额-剩余融资金额
				deAmt = deAmt.subtract(residuegetFinancingAmount);
				break;
			case 0://剩余融资金额=还款金额
				//1、剩余融资金额不变，2、还款金额=0
				deAmt = BigDecimal.ZERO;
				break;

			default://剩余融资金额>还款金额
				//1、剩余融资金额=剩余融资金额-还款金额，2、还款金额=0
				residuegetFinancingAmount = residuegetFinancingAmount.subtract(deAmt);
				deAmt = BigDecimal.ZERO;
				break;
			}
			//计算已结算融资本金=当前已结算融资本金+剩余融资金额
			BigDecimal settlementedFinancingAmount = userFinancingInterestInfo.getSettlementedFinancingAmount().add(residuegetFinancingAmount);
			UserFinancingInterestInfo userFinancingInterestInfoUpdate = new UserFinancingInterestInfo();
			userFinancingInterestInfoUpdate.setId(userFinancingInterestInfo.getId());
			userFinancingInterestInfoUpdate.setSettlementedFinancingAmount(settlementedFinancingAmount);
			//如果当前已结算融资本金=计算已结算融资本金
			if (userFinancingInterestInfo.getFinancingAmount().compareTo(settlementedFinancingAmount) == 0) {
				//2.2.2、如果本金不够扣除，不去进行计算
				UserInfo userInfo = userInfoService.getById(userFinancingInterestInfo.getUserId());
				//"用户可用金额"大于等于"产生利息总额"
				if (userInfo.getAvailableAmt().compareTo(residuegetFinancingAmount) > -1) {
					//执行用户资金记录
					userInfoService.updateUserAvailableAmt(userId, AmtDeTypeEnum.FinancingRepayment, residuegetFinancingAmount, deSummary, CurrencyEnum.CNY, BigDecimal.ONE, ip, ipAddress, operator,businessOrderSn);
					//设置该笔融资为已结算
					userFinancingInterestInfoUpdate.setSettlement(true);
				}
				//当该笔融资信息，已结算时，利息也处理为已结算，通过"融资信息ID"、"未结算"，更改当前一批数据为已结算
				userFinancingInterestDayDetailService.lambdaUpdate()
						.eq(UserFinancingInterestDayDetail::getFinancingInterestId, userFinancingInterestInfo.getId())
						.eq(UserFinancingInterestDayDetail::getSettlement, false)
						.set(UserFinancingInterestDayDetail::getSettlement, true).update();
			}
			//修改融资信息
			this.updateById(userFinancingInterestInfoUpdate);
			//还款金额为0时停止
			if (deAmt.equals(BigDecimal.ZERO)) {
				return;
			}
		}
	}

	/**
	 * 通过持仓id，处理融资信息
	 */
	@Override
	public void doUserFinancingInterestInfoByPositionId(Integer positionId,BigDecimal actualSubscriptionAmount) {
		UserStockPosition userStockPosition = userStockPositionService.getById(positionId);
		if (userStockPosition != null && userStockPosition.getPositionStatus() == 2) {//已成交
			if(userStockPosition.getLever() > 1) {//加了杠杆代表，有融资
				Date now = new Date();
				//融资金额*****************actualSubscriptionAmount看看需不需要处理汇率问题
				BigDecimal financingAmount = actualSubscriptionAmount
						.multiply(new BigDecimal(userStockPosition.getLever()).subtract(BigDecimal.ONE))
						.divide(new BigDecimal(userStockPosition.getLever()),3, RoundingMode.DOWN);
				UserFinancingInterestInfo ufii = new UserFinancingInterestInfo();
				//通过持仓id查询,未结算的融资信息
				UserFinancingInterestInfo userFinancingInterestInfo = this.lambdaQuery()
						.eq(UserFinancingInterestInfo::getSourceId, positionId)
						.eq(UserFinancingInterestInfo::getSettlement, 0)
						.one();
				if (userFinancingInterestInfo != null) {//有则，合并融资信息
					ufii.setId(userFinancingInterestInfo.getId());
					ufii.setFinancingAmount(userFinancingInterestInfo.getFinancingAmount().add(financingAmount));//融资金额累加
					ufii.updateById();
				}else {//无则，添加融资信息
					StockParamConfig stockParamConfig =this.sysParamConfigService.getSysParamConfig();
					ufii.setUserId(userStockPosition.getUserId());
					ufii.setAnnualizedInterestTime(new Date(now.getTime() + 1000 * 60 * 60 * 24));
				    ufii.setAnnualizedInterestRate(stockParamConfig.getAnnualizedInterestRate());
				    ufii.setFinancingAmount(financingAmount);
				    ufii.setSource(0);
			        ufii.setSourceId(positionId);
					ufii.insert();
				}
		      }
		}
	}

}
