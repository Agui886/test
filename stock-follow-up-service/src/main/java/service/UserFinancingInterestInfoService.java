package service;

import entity.UserFinancingInterestInfo;
import enums.CurrencyEnum;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户融资信息表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface UserFinancingInterestInfoService extends IService<UserFinancingInterestInfo> {

	/**
	 * 融资还款
	 * @param userId 用户id
	 * @param amt 还款金额
	 * @param deSummary 说明描述
	 * @param currency 货币类型
	 * @param exchangeRate 汇率
	 * @param ip ip
	 * @param ipAddress ip地址
	 * @param operator 操作人
	 * @param businessOrderSn 业务单号
	 */
	void doFinancingRepayment(Integer userId, BigDecimal deAmt, String string, CurrencyEnum currency,
			BigDecimal exchangeRate, String ip, String ipAddress, String operator,String businessOrderSn);

	/**
	 * 通过持仓id，处理融资信息
	 * @param positionId 持仓id
	 * @param actualSubscriptionAmount 实际认购金额 = 当时购买价格*当时购买股数
	 */
	void doUserFinancingInterestInfoByPositionId(Integer positionId,BigDecimal actualSubscriptionAmount);
}
