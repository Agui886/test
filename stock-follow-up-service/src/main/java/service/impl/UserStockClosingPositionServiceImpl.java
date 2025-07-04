package service.impl;

import entity.UserInfo;
import entity.UserStockClosingPosition;
import entity.UserStockPending;
import entity.UserStockPosition;
import entity.common.Response;
import entity.common.StockInfo;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import enums.StockTypeEnum;
import enums.UserRealAuthStatusEnum;
import mapper.UserStockClosingPositionMapper;
import mapper.UserStockPendingMapper;
import service.IpAddressService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserFinancingInterestDayDetailService;
import service.UserFinancingInterestInfoService;
import service.UserInfoService;
import service.UserStockClosingPositionService;
import service.UserStockPendingService;
import service.UserStockPositionService;
import utils.BuyAndSellUtils;
import utils.EastMoneyApiAnalysis;
import utils.OrderNumberGenerator;
import utils.SinaApi;
import vo.common.StockQuotesVO;
import vo.manager.StockClosingPositionListVO;
import vo.manager.StockPositionListSearchParamVO;
import vo.server.SellingStockPageVO;
import vo.server.UserStockClosingPositionDetailVO;
import vo.server.UserStockClosingPositionListVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import constant.Constant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户股票平仓订单 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Service
public class UserStockClosingPositionServiceImpl extends ServiceImpl<UserStockClosingPositionMapper, UserStockClosingPosition> implements UserStockClosingPositionService {

	@Resource
	private UserStockClosingPositionMapper userStockClosingPositionMapper;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserStockPendingService userStockPendingService;
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private UserStockPendingMapper userStockPendingMapper;
	
	@Resource
	private UserFinancingInterestDayDetailService userFinancingInterestDayDetailService;
	
	@Resource
	private UserFinancingInterestInfoService userFinancingInterestInfoService;
	
	/**
	 * 持仓管理-股票持仓-平仓单
	 */
	@Override
	public void managerStockClosingList(Page<StockClosingPositionListVO> page, StockPositionListSearchParamVO param,
			boolean isBlockTrading) {
		this.userStockClosingPositionMapper.managerStockClosingList(page, param, isBlockTrading);
		List<StockClosingPositionListVO> list = page.getRecords();
		if(list.size() > 0) {
			List<String> stockGids = new ArrayList<>();
			List<String> hkOrUsStockGids = new ArrayList<>();
			for(StockClosingPositionListVO i : list) {
				String gid = i.getStockType() + i.getStockCode();
				if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
						|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
						|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
					if(!stockGids.contains(gid)) {
						stockGids.add(gid);
					}
				} else {
					if(!hkOrUsStockGids.contains(gid)) {
						hkOrUsStockGids.add(i.getStockType() + i.getStockCode());
					}
				}
			}
			if(stockGids.size() > 0) {
				 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
				 list.forEach(i-> {
					 stockQuotesVOList.forEach(stockQuotesVO-> {
						 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						     i.setNowPrice(stockQuotesVO.getNowPrice());
						 }
					 });
				 });
			}
			if(hkOrUsStockGids.size() > 0) {
				 List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
				 list.forEach(i-> {
					 stockQuotesVOList.forEach(stockQuotesVO-> {
						 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						     i.setNowPrice(stockQuotesVO.getNowPrice());
						 }
					 });
				 });
			}
		}
	}
	
	/**
	 * APP资产-平仓列表
	 */
	@Override
	public void userStockClosingPositionList(Page<UserStockClosingPositionListVO> page, Integer userId) {
		this.userStockClosingPositionMapper.userStockClosingPositionList(page, userId);
	}

	/**
	 * APP资产-平仓详情
	 */
	@Override
	public Response<UserStockClosingPositionDetailVO> userStockClosingPositionDetail(Integer id, Integer userId) {
		UserStockClosingPosition uscp = this.lambdaQuery().eq(UserStockClosingPosition::getId, id).eq(UserStockClosingPosition::getUserId, userId).one();
		if(uscp == null) {
			return Response.fail("订单信息错误");
		}
		UserStockClosingPositionDetailVO vo = new UserStockClosingPositionDetailVO();
		vo.setId(uscp.getId());
		vo.setStockName(uscp.getStockName());
		vo.setStockCode(uscp.getStockCode());
		vo.setStockType(uscp.getStockType());
		vo.setStockPlate(uscp.getStockPlate());
		vo.setPositionDirection(uscp.getPositionDirection());
		vo.setBuyingPrice(uscp.getBuyingPrice());
		vo.setActualProfit(uscp.getActualProfit());
		vo.setLever(uscp.getLever());
		vo.setSellingPrice(uscp.getSellingPrice());
		vo.setSellingFee(uscp.getSellingFee());
		vo.setSellingShares(uscp.getSellingShares());
		vo.setSellingStampDuty(uscp.getSellingStampDuty());
		vo.setSpreadFee(uscp.getSpreadFee());
		vo.setTradingOrderSn(uscp.getTradingOrderSn());
		vo.setClosingTime(uscp.getClosingTime());
		vo.setTransactionTime(uscp.getClosingTime());
		StockQuotesVO q;
		switch(uscp.getStockType()) {
		case "us":
		case "hk":
			q = EastMoneyApiAnalysis.getStockRealTimeData(uscp.getStockType(), uscp.getStockCode());
			break;
		default:
			q = SinaApi.getSinaStock(uscp.getStockType(), uscp.getStockCode());
			break;
		}
		vo.setNowPrice(q.getNowPrice());
		vo.setPercentageIncrease(q.getPercentageIncrease());
		vo.setIncrease(q.getNowPrice().subtract(q.getPrevClose()));
		return Response.successData(vo);
	}
	
	/**
	 * APP股票-卖出-页面数据加载
	 */
	@Override
	public Response<SellingStockPageVO> sellingStockPage(Integer id, Integer userId) {
		UserStockPosition usp = this.userStockPositionService.lambdaQuery().eq(UserStockPosition::getId, id).eq(UserStockPosition::getUserId, userId).one();
		if(usp == null) {
			return Response.fail("持仓信息不存在");
		}
		StockInfo si = stockInfoService.getStockInfoByStockTypeAndStockCode(usp.getStockType(),usp.getStockCode());
		if(si == null) {
			return Response.fail("产品信息不存在");
		}
		UserInfo ui = this.userInfoService.getById(userId);
		StockParamConfig spc = sysParamConfigService.getSysParamConfig();
		SellingStockPageVO ret = new SellingStockPageVO();
		ret.setBuyingShares(usp.getBuyingShares());
		ret.setStockType(usp.getStockType());
		ret.setStockCode(usp.getStockCode());
		ret.setStockName(si.getStockName());
		ret.setStockPlate(si.getStockPlate());
//		ret.setSpreadRate(si.getSpreadRate());
		if(usp.getLockInPeriod() > 0) {
			int unavailableShares = this.userStockPendingMapper.getUnavailableShares(usp.getId(), usp.getLockInPeriod());
			ret.setUnavailableShares(unavailableShares > usp.getBuyingShares() ? usp.getBuyingShares() : unavailableShares);
		}
		String am_begin, am_end, pm_begin, pm_end;
		StockQuotesVO sq;
		StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
		switch(usp.getStockType()) {
		case Constant.STOCK_TYPE_US:
			am_begin = stockParamConfig.getMarketUs_amTradingStart();
        	am_end = stockParamConfig.getMarketUs_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketUs_pmTradingStart();
        	pm_end = stockParamConfig.getMarketUs_pmTradingEnd();
			sq = EastMoneyApiAnalysis.getStockRealTimeData(usp.getStockType(), usp.getStockCode());
			ret.setSellingFeeRate(spc.getMarketUsSellingFeeRate());
			ret.setStampDutyRate(BigDecimal.ZERO);
			break;
		case Constant.STOCK_TYPE_HK:
			am_begin = stockParamConfig.getMarketHk_amTradingStart();
        	am_end = stockParamConfig.getMarketHk_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketHk_pmTradingStart();
        	pm_end = stockParamConfig.getMarketHk_pmTradingEnd();
        	sq = EastMoneyApiAnalysis.getStockRealTimeData(usp.getStockType(), usp.getStockCode());
			ret.setSellingFeeRate(spc.getMarketHkSellingFeeRate());
			ret.setStampDutyRate(spc.getMarketHkStampDutyRate());
			break;
		default:
			am_begin = stockParamConfig.getMarketA_amTradingStart();
        	am_end = stockParamConfig.getMarketA_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketA_pmTradingStart();
        	pm_end = stockParamConfig.getMarketA_pmTradingEnd();
			sq = SinaApi.getSinaStock(usp.getStockType(), usp.getStockCode());
			ret.setSellingFeeRate(spc.getMarketASellingFeeRate());
			ret.setStampDutyRate(spc.getMarketAStampDutyRate());
			break;
		}
		ret.setNowPrice(sq.getNowPrice());
		ret.setIncrease(sq.getNowPrice().subtract(sq.getPrevClose()));
		ret.setPercentageIncrease(sq.getPercentageIncrease());
		//市场状态
		int status = BuyAndSellUtils.getTradingStatus(am_begin, am_end, pm_begin, pm_end, usp.getStockType(),true);
		ret.setMarketStatus(status);
		ret.setUserAllInterestGenerated(userFinancingInterestDayDetailService.getInterestGeneratedByUserId(usp.getUserId()));//用户产生的所有融资利息
		return Response.successData(ret);
	}
	
	/**
	 * APP股票-卖出
	 */
	@Override
	@Transactional
	public Response<Void> sellStock(Integer id, Integer userId, Integer shares, String ip) {
		if(shares <= 0) {
			return Response.fail("请填写购买股数");
		}
		UserStockPosition usp = this.userStockPositionService.lambdaQuery().eq(UserStockPosition::getId, id).eq(UserStockPosition::getUserId, userId).one();
		if(usp == null) {
			return Response.fail("持仓信息不存在");
		}
		if(usp.getIsLock()) {
			return Response.fail("当前持仓暂时无法卖出");
		}
		if(usp.getBuyingShares() < shares) {
			return Response.fail("持有股数不足");
		}
		if(usp.getLockInPeriod() > 0) {
			int unavailableShares = this.userStockPendingMapper.getUnavailableShares(usp.getId(), usp.getLockInPeriod());
			if(unavailableShares > usp.getBuyingShares()) {
				unavailableShares = usp.getBuyingShares();
			}
			int availableShares = usp.getBuyingShares() - unavailableShares;
			if (availableShares == 0) {
				return Response.fail("T+1平仓");
			}
			if(availableShares < shares) {
				return Response.fail("当前可交易股数为：" + availableShares);
			}
		}
		StockInfo si = stockInfoService.getStockInfoByStockTypeAndStockCode(usp.getStockType(),usp.getStockCode());
		if(si == null) {
			return Response.fail("产品信息不存在");
		}
//		if(si.getIsLock()) {
//			return Response.fail("该产品暂时停止交易");
//		}
		
		StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
		int closingMinutesLimitAfterBuying = stockParamConfig.getClosingMinutesLimitAfterBuying();
		Date now = new Date();
		int count = this.userStockPendingService.lambdaQuery()
				.eq(UserStockPending::getStockType, si.getStockType())
				.eq(UserStockPending::getStockCode, si.getStockCode())
				.between(UserStockPending::getPendingTime, new Date(now.getTime() - closingMinutesLimitAfterBuying * 60 * 1000), now).count();
		if(count > 0) {
			return Response.fail("频繁交易，您暂时无法卖出该产品，请稍候再操作");
		}
		String am_begin, am_end, pm_begin, pm_end;
        StockQuotesVO sq;
        BigDecimal sellingFeeRate, sellingStampDutyRate = BigDecimal.ZERO;
        CurrencyEnum currency;
        switch(si.getStockType()) {
        case "us":
        	am_begin = stockParamConfig.getMarketUs_amTradingStart();
        	am_end = stockParamConfig.getMarketUs_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketUs_pmTradingStart();
        	pm_end = stockParamConfig.getMarketUs_pmTradingEnd();
        	sq = EastMoneyApiAnalysis.getStockRealTimeData(usp.getStockType(), usp.getStockCode());
        	sellingFeeRate = stockParamConfig.getMarketUsSellingFeeRate();
        	currency = CurrencyEnum.USD;
        	break;
        case "hk":
        	am_begin = stockParamConfig.getMarketHk_amTradingStart();
        	am_end = stockParamConfig.getMarketHk_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketHk_pmTradingStart();
        	pm_end = stockParamConfig.getMarketHk_pmTradingEnd();
        	sq = EastMoneyApiAnalysis.getStockRealTimeData(usp.getStockType(), usp.getStockCode());
        	sellingFeeRate = stockParamConfig.getMarketHkSellingFeeRate();
        	sellingStampDutyRate = stockParamConfig.getMarketHkStampDutyRate();
        	currency = CurrencyEnum.HKD;
        	break;
        default:
        	am_begin = stockParamConfig.getMarketA_amTradingStart();
        	am_end = stockParamConfig.getMarketA_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketA_pmTradingStart();
        	pm_end = stockParamConfig.getMarketA_pmTradingEnd();
        	sq = SinaApi.getSinaStock(si.getStockType(), si.getStockCode());
        	sellingFeeRate = stockParamConfig.getMarketASellingFeeRate();
        	sellingStampDutyRate = stockParamConfig.getMarketAStampDutyRate();
        	currency = CurrencyEnum.CNY;
        	break;
        }
        int status = BuyAndSellUtils.getTradingStatus(am_begin, am_end, pm_begin, pm_end, si.getStockType(),true);
        if(status == 0) {
        	return Response.fail("已收盘，暂停交易");
        }
        if(status == 1) {
        	return Response.fail("午间休市，暂停交易");
        }
        if(sq.getGid() == null || sq.getNowPrice().compareTo(BigDecimal.ZERO) < 1) {
        	return Response.fail("获取行情价格失败，请稍后再交易");
        }
		UserInfo userInfo = userInfoService.getById(userId);
		if(userInfo.getRealAuthStatus() != UserRealAuthStatusEnum.REVIEWED.getCode()) {
			return Response.fail("您的账户还未进行实名认证，请先提交您的认证信息");
		}
//		if(!userInfo.getTradeEnable()) {
//			return Response.fail("您的账户已被禁止交易，请联系管理员");
//		}
		BigDecimal exchangeRate = sysParamConfigService.getExchangeRate(currency);
		BigDecimal sellingShares = new BigDecimal(shares);
		BigDecimal orderAmount = sq.getNowPrice().multiply(sellingShares);
		BigDecimal sellingFee = orderAmount.multiply(sellingFeeRate).setScale(2, RoundingMode.HALF_UP);
		BigDecimal spreadFee = new BigDecimal(0); //orderAmount.multiply(si.getSpreadRate()).setScale(2, RoundingMode.HALF_UP);
		BigDecimal sellingStampDuty = orderAmount.multiply(sellingStampDutyRate).setScale(2, RoundingMode.HALF_UP);
		BigDecimal amountReceived = orderAmount.subtract(sellingFee).subtract(sellingStampDuty).subtract(spreadFee);
		UserStockClosingPosition uscp = new UserStockClosingPosition();
		uscp.setPositionType(usp.getPositionType());
		uscp.setUserId(usp.getUserId());
		uscp.setStockName(usp.getStockName());
		uscp.setStockCode(usp.getStockCode());
		uscp.setStockType(usp.getStockType());
		uscp.setStockPlate(usp.getStockPlate());
		uscp.setBuyingPrice(usp.getBuyingPrice());
		uscp.setPositionDirection(usp.getPositionDirection());
		uscp.setBuyingShares(usp.getBuyingShares());
		uscp.setLever(usp.getLever());
		uscp.setBuyingFee(usp.getBuyingFee());
		uscp.setSpreadFee(spreadFee);
		uscp.setBuyingStampDuty(usp.getBuyingStampDuty());
//		uscp.setIsBlockTrading(usp.getIsBlockTrading());
		uscp.setTradingOrderSn(OrderNumberGenerator.create(1));
		uscp.setPositionTime(usp.getPositionTime());
		uscp.setClosingTime(now);
		uscp.setSellingPrice(sq.getNowPrice());
		uscp.setSellingShares(shares);
		uscp.setSellingFee(sellingFee);
		uscp.setSellingStampDuty(sellingStampDuty);
		uscp.setAmountReceived(amountReceived);
		BigDecimal floatingProfit, actualProfit;
		if(usp.getPositionDirection() == Constant.GO_LONG) {
			floatingProfit = sq.getNowPrice().subtract(usp.getBuyingPrice()).multiply(sellingShares).setScale(2, RoundingMode.DOWN);
		} else {
			floatingProfit = usp.getBuyingPrice().subtract(sq.getNowPrice()).multiply(sellingShares).setScale(2, RoundingMode.DOWN);
		}
		BigDecimal totalFee = usp.getBuyingFee().divide(new BigDecimal(usp.getBuyingShares()), 2, RoundingMode.DOWN).multiply(sellingShares).add(sellingFee).add(sellingStampDuty).add(spreadFee);
		actualProfit = floatingProfit.subtract(totalFee).setScale(2, RoundingMode.HALF_UP);
		uscp.setFloatingProfit(floatingProfit);
		uscp.setActualProfit(actualProfit);
		uscp.setPositionId(usp.getId());
		uscp.insert();
		if(usp.getBuyingShares() - shares == 0) {
			this.userStockPositionService.lambdaUpdate().set(UserStockPosition::getBuyingShares, usp.getBuyingShares() - shares).set(UserStockPosition::getPositionStatus, 5).eq(UserStockPosition::getId, usp.getId()).update();
		} else {
			this.userStockPositionService.lambdaUpdate().set(UserStockPosition::getBuyingShares, usp.getBuyingShares() - shares).eq(UserStockPosition::getId, usp.getId()).update();
		}
		BigDecimal deAmt = orderAmount.subtract(spreadFee).subtract(sellingFee).subtract(sellingStampDuty);
		//平仓-增加用户金额
		StringBuilder sb = new StringBuilder("卖出股票：").append(si.getStockName()).append("(").append(si.getStockCode()).append(")(").append(StockTypeEnum.getNameByCode(si.getStockType())).append(")，价格：");
		sb.append(sq.getNowPrice()).append("，数量：").append(shares).append("，杠杆：").append(usp.getLever()).append("，手续费：").append(sellingFee);
		sb.append("，印花税：").append(sellingStampDuty).append("，点差费：").append(spreadFee);		
		userInfoService.updateUserAvailableAmt(userId, AmtDeTypeEnum.ClosingPosition, deAmt, sb.toString(), currency, exchangeRate, ip, ipAddressService.getIpAddress(ip).getAddress2(), userInfo.getOperator(),uscp.getTradingOrderSn());
		//执行融资还款
		userFinancingInterestInfoService.doFinancingRepayment(userId, deAmt, "持仓id："+id+sb.toString(), currency, exchangeRate, ip, ipAddressService.getIpAddress(ip).getAddress2(), userInfo.getOperator(),uscp.getTradingOrderSn());
		//10、跟据交易添加返佣明细
		BigDecimal operateFollowSum = deAmt.divide(exchangeRate, 2, RoundingMode.HALF_UP);//操作金额转人名币（=deAmt除以汇率）
		userInfoService.addUserRebateDetailOnTransaction(userInfo, operateFollowSum, 2, uscp.getTradingOrderSn());
		return Response.success();
	}


}
