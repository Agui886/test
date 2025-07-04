package service.impl;

import entity.IpAddress;
import entity.UserInfo;
import entity.UserPositionChangeRecord;
import entity.UserStockClosingPosition;
import entity.UserStockPosition;
import entity.common.Response;
import entity.common.StockInfo;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import enums.StockTypeEnum;
import enums.UserRealAuthStatusEnum;
import enums.UserStockPositionChangeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import mapper.UserStockPendingMapper;
import mapper.UserStockPositionMapper;
import service.IpAddressService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserFinancingInterestInfoService;
import service.UserInfoService;
import service.UserPositionChangeRecordService;
import service.UserStockPositionService;
import utils.EastMoneyApiAnalysis;
import utils.OrderNumberGenerator;
import utils.SinaApi;
import vo.common.StockQuotesVO;
import vo.manager.StockPositionListSearchParamVO;
import vo.manager.StockPositionListVO;
import vo.server.StockPositionDetailVO;
import vo.server.UserStockPositionListVO;

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
 * 用户持仓信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Service
@Slf4j
public class UserStockPositionServiceImpl extends ServiceImpl<UserStockPositionMapper, UserStockPosition> implements UserStockPositionService {

	@Resource
	private UserStockPositionMapper userStockPositionMapper;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private UserPositionChangeRecordService userStockPositionChangeRecordService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private UserFinancingInterestInfoService userFinancingInterestInfoService;
	
	@Resource
	private UserStockPendingMapper userStockPendingMapper;

	/**
	 * 持仓管理-股票持仓
	 */
	@Override
	public void managerStockPositionList(Page<StockPositionListVO> page, StockPositionListSearchParamVO param, boolean isBlockTrading) {
		this.userStockPositionMapper.managerStockPositionList(page, param, isBlockTrading);
		List<StockPositionListVO> list = page.getRecords();
		//以下代码需要封装
		if(list.size() == 0) 
			return;
		BigDecimal usdRate = null, hkdRate = null;
		List<String> stockGids = new ArrayList<>();
		List<String> hkOrUsStockGids = new ArrayList<>();
		for(StockPositionListVO i : list) {
			if(i.getUnavailableShares() > i.getBuyingShares()) {
				i.setUnavailableShares(i.getBuyingShares());
			}
			String gid = i.getStockType() + i.getStockCode();
			if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
					|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
					|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
				if(!stockGids.contains(gid)) {
					stockGids.add(gid);
				}
			} else {
				if(!hkOrUsStockGids.contains(gid)) {
					hkOrUsStockGids.add(gid);
				}
			}
		}
		if(stockGids.size() > 0) {
			 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
			 list.forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     BigDecimal buyingShares = new BigDecimal(i.getBuyingShares());
					     i.setMarketValue(i.getNowPrice().multiply(buyingShares));
					     BigDecimal floatingProfit = stockQuotesVO.getNowPrice().subtract(i.getBuyingPrice()).multiply(new BigDecimal(i.getBuyingShares()));
					     if(i.getPositionDirection() == Constant.SHORT_SELLING) {
					    	floatingProfit = BigDecimal.ZERO.subtract(floatingProfit);
					     }
					     i.setFloatingProfit(floatingProfit);
					 }
				 });
			 });
		}
		if (hkOrUsStockGids.size() > 0) {
			List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
			for (StockPositionListVO i : list) {
				for (StockQuotesVO stockQuotesVO : stockQuotesVOList) {
					if (stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						i.setNowPrice(stockQuotesVO.getNowPrice());
						BigDecimal buyingShares = new BigDecimal(i.getBuyingShares());
						i.setMarketValue(i.getNowPrice().multiply(buyingShares));
						BigDecimal floatingProfit = stockQuotesVO.getNowPrice().subtract(i.getBuyingPrice()).multiply(new BigDecimal(i.getBuyingShares()));
						if (i.getPositionDirection() == Constant.SHORT_SELLING) {
							floatingProfit = BigDecimal.ZERO.subtract(floatingProfit);
						}
						i.setFloatingProfit(floatingProfit);
						if (i.getStockType().equals(StockTypeEnum.US.getCode())) {
							if (usdRate == null) {
								usdRate = sysParamConfigService.getExchangeRate(CurrencyEnum.USD);
							}
							i.setExchangeRate(usdRate);
						} else {
							if (hkdRate == null) {
								hkdRate = sysParamConfigService.getExchangeRate(CurrencyEnum.HKD);
							}
							i.setExchangeRate(hkdRate);
						}
					}
				}
			}
		}
	}

	/**
	 * 持仓管理-股票持仓-锁仓
	 */
	@Override
	@Transactional
	public Response<Void> lock(Integer id, String lockMsg, String ip, String operator) {
		UserStockPosition usp = this.getById(id);
		if(usp == null) {
			return Response.fail("持仓信息错误");
		}
		if(usp.getIsLock()) {
			return Response.fail("已锁仓，请匆重复操作");
		}
		if(usp.getPositionStatus() != 2) {
			return Response.fail("操作失败，持仓状态错误");
		}
		this.lambdaUpdate().eq(UserStockPosition::getId, id).set(UserStockPosition::getIsLock, true).set(UserStockPosition::getLockMsg, lockMsg).update();
		IpAddress ia = this.ipAddressService.getIpAddress(ip);
		UserPositionChangeRecord uspcr = new UserPositionChangeRecord();
		uspcr.setUserId(usp.getUserId());
		uspcr.setPositionId(id);
		uspcr.setStockCode(usp.getStockCode());
		uspcr.setStockName(usp.getStockName());
		uspcr.setDataChangeTypeCode(UserStockPositionChangeTypeEnum.LOCK_STATIS_EDIT.getCode());
		uspcr.setDataChangeTypeName(UserStockPositionChangeTypeEnum.LOCK_STATIS_EDIT.getName());
		uspcr.setOldContent(usp.toString());
		usp.setIsLock(true);
		usp.setLockMsg(lockMsg);
		uspcr.setNewContent(usp.toString());
		uspcr.setOperator(operator);
		uspcr.setIp(ip);
		uspcr.setIpAddress(ia.getAddress2());
		uspcr.setCreateTime(new Date());
		uspcr.insert();
		return Response.success();
	}

	/**
	 * 持仓管理-股票持仓-解锁
	 */
	@Override
	@Transactional
	public Response<Void> unlock(Integer id, String ip, String operator) {
		UserStockPosition usp = this.getById(id);
		if(usp == null) {
			return Response.fail("持仓信息错误");
		}
		if(!usp.getIsLock()) {
			return Response.fail("已解锁，请匆重复操作");
		}
		if(usp.getPositionStatus() != 2) {
			return Response.fail("操作失败，持仓状态错误");
		}
		this.lambdaUpdate().eq(UserStockPosition::getId, id).set(UserStockPosition::getIsLock, false).set(UserStockPosition::getLockMsg, "").update();
		IpAddress ia = this.ipAddressService.getIpAddress(ip);
		UserPositionChangeRecord uspcr = new UserPositionChangeRecord();
		uspcr.setUserId(usp.getUserId());
		uspcr.setPositionId(id);
		uspcr.setStockCode(usp.getStockCode());
		uspcr.setStockName(usp.getStockName());
		uspcr.setDataChangeTypeCode(UserStockPositionChangeTypeEnum.LOCK_STATIS_EDIT.getCode());
		uspcr.setDataChangeTypeName(UserStockPositionChangeTypeEnum.LOCK_STATIS_EDIT.getName());
		uspcr.setOldContent(usp.toString());
		usp.setIsLock(false);
		usp.setLockMsg("");
		uspcr.setNewContent(usp.toString());
		uspcr.setOperator(operator);
		uspcr.setIp(ip);
		uspcr.setIpAddress(ia.getAddress2());
		uspcr.setCreateTime(new Date());
		uspcr.insert();
		return Response.success();
	}

	/**
	 * 持仓管理-股票持仓-修改锁仓天数
	 */
	@Override
	@Transactional
	public Response<Void> lockInPeriodEdit(Integer id, Integer lockInPeriod, String ip, String operator) {
		UserStockPosition usp = this.getById(id);
		if(usp == null) {
			return Response.fail("持仓信息错误");
		}
		if(usp.getLockInPeriod() != null && usp.getLockInPeriod().equals(lockInPeriod) ) {
			return Response.fail("天数与原参数相同，请重新输入");
		}
		if(usp.getPositionStatus() != 2) {
			return Response.fail("操作失败，持仓状态错误");
		}
		this.lambdaUpdate().eq(UserStockPosition::getId, id).set(UserStockPosition::getLockInPeriod, lockInPeriod).update();
		IpAddress ia = this.ipAddressService.getIpAddress(ip);
		UserPositionChangeRecord uspcr = new UserPositionChangeRecord();
		uspcr.setUserId(usp.getUserId());
		uspcr.setPositionId(id);
		uspcr.setStockCode(usp.getStockCode());
		uspcr.setStockName(usp.getStockName());
		uspcr.setDataChangeTypeCode(UserStockPositionChangeTypeEnum.LOCK_IN_PERIOD.getCode());
		uspcr.setDataChangeTypeName(UserStockPositionChangeTypeEnum.LOCK_IN_PERIOD.getName());
		uspcr.setOldContent(usp.toString());
		usp.setLockInPeriod(lockInPeriod);
		uspcr.setNewContent(usp.toString());
		uspcr.setOperator(operator);
		uspcr.setIp(ip);
		uspcr.setIpAddress(ia.getAddress2());
		uspcr.setCreateTime(new Date());
		uspcr.insert();
		return Response.success();
	}

	/**
	 * 持仓管理-股票持仓-强制平仓
	 */
	@Override
	@Transactional
	public Response<Void> managerClosePosition(Integer id, String ip, String operator) {
		UserStockPosition usp = this.getById(id);
		if(usp == null) {
			return Response.fail("持仓信息错误");
		}
		if(usp.getPositionStatus() != 2) {
			return Response.fail("平仓失败，持仓状态错误");
		}
		if(usp.getIsLock()) {
			return Response.fail("平仓失败，该持仓已被管理员锁定，锁定原因：" + usp.getLockMsg());
		}
		UserInfo user = this.userInfoService.getById(usp.getUserId());
		if(user == null) {
			return Response.fail("平仓失败，用户信息错误");
		}
		if(usp.getPositionType() == Constant.ACCOUNT_TYPE_REAL  && user.getRealAuthStatus() != UserRealAuthStatusEnum.REVIEWED.getCode()) {
			return Response.fail("平仓失败，该用户还未实名认证");
		}
//		if(!user.getTradeEnable()) {
//			return Response.fail("平仓失败，该用户已被限制交易");
//		}
		StockInfo si = this.stockInfoService.getStockInfoByStockTypeAndStockCode(usp.getStockType(), usp.getStockCode());
		if(si == null) {
			return Response.fail("股票信息不存在");
		}
		BigDecimal nowPrice, sellingFeeRate, stampDutyRate;
		CurrencyEnum currency;
		StockParamConfig sysParamConfig = sysParamConfigService.getSysParamConfig();
		switch(usp.getStockType()) {
		case Constant.STOCK_TYPE_US:
			nowPrice = EastMoneyApiAnalysis.getStockRealTimeData(usp.getStockType(), usp.getStockCode()).getNowPrice();
			sellingFeeRate = sysParamConfig.getMarketUsSellingFeeRate();
			stampDutyRate = BigDecimal.ZERO;
			currency = CurrencyEnum.USD;
			break;
		case Constant.STOCK_TYPE_HK:
			nowPrice = EastMoneyApiAnalysis.getStockRealTimeData(usp.getStockType(), usp.getStockCode()).getNowPrice();
			sellingFeeRate = sysParamConfig.getMarketHkSellingFeeRate();
			stampDutyRate = sysParamConfig.getMarketHkStampDutyRate();
			currency = CurrencyEnum.HKD;
			break;
		default:
			nowPrice = SinaApi.getSinaStock(usp.getStockType(), usp.getStockCode()).getNowPrice();
			sellingFeeRate = sysParamConfig.getMarketASellingFeeRate();
			stampDutyRate = sysParamConfig.getMarketAStampDutyRate();
			currency = CurrencyEnum.CNY;
			break;
		}
		if(nowPrice == null || nowPrice.compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("平仓失败，获取股票最新价格失败，请稍后再试");
		}
		log.info("获取到股票{}({})({})最新价：{}", usp.getStockName(), usp.getStockCode(), StockTypeEnum.getNameByCode(usp.getStockType()), nowPrice);
		BigDecimal buyingShares = new BigDecimal(usp.getBuyingShares());
		BigDecimal holdingCost = usp.getBuyingPrice().multiply(buyingShares);
		BigDecimal marketValue = nowPrice.multiply(buyingShares);
		BigDecimal floatingProfit;
		if(usp.getPositionDirection() == Constant.GO_LONG) {
			floatingProfit = marketValue.subtract(holdingCost);
		} else {
			floatingProfit = holdingCost.subtract(marketValue);
		}
		BigDecimal exchangeRate = sysParamConfigService.getExchangeRate(currency);
		BigDecimal buyingFee = usp.getBuyingFee();
		BigDecimal buyingStampDuty = usp.getBuyingStampDuty();
		BigDecimal spreadFee = new BigDecimal(0); //si.getSpreadRate().multiply(marketValue).setScale(2, RoundingMode.HALF_UP);
		BigDecimal sellingStampDuty = marketValue.multiply(stampDutyRate).setScale(2, RoundingMode.HALF_UP);
		BigDecimal sellingFee = marketValue.multiply(sellingFeeRate).setScale(2, RoundingMode.HALF_UP);
		BigDecimal amountReceived = marketValue.subtract(sellingFee).subtract(sellingStampDuty).subtract(spreadFee);
		BigDecimal totaSellinglFee = sellingFee.add(sellingStampDuty).add(spreadFee);
		BigDecimal deAmt = marketValue.subtract(totaSellinglFee);
		BigDecimal actualProfit = floatingProfit.subtract(totaSellinglFee).subtract(buyingFee).subtract(buyingStampDuty);
		Date now = new Date();
		StringBuilder deSummary = new StringBuilder();
		deSummary.append("管理员强制平仓");
		deSummary.append("\n持仓ID：").append(id);
		deSummary.append("\n持仓类型：").append(usp.getPositionType() == Constant.ACCOUNT_TYPE_REAL ? "实盘" : "模拟");
//		deSummary.append("\n是否大宗交易：").append(usp.getIsBlockTrading() ? "是" : "否");
		deSummary.append("\n用户ID：").append(usp.getUserId());
		deSummary.append("\n持有股票：").append(usp.getStockName()).append("(").append(usp.getStockCode()).append(")(").append(StockTypeEnum.getNameByCode(usp.getStockType())).append(")");
		deSummary.append("\n卖出价格：").append(nowPrice).append("，卖出手续费：").append(sellingFee).append("，卖出印花税：").append(sellingStampDuty).append("，点差费：").append(spreadFee);
		deSummary.append("\n浮动盈亏：").append(floatingProfit);
		if(usp.getLever() > 1) {
			deSummary.append("\n杠杆：").append(usp.getLever());
		}
		String address = this.ipAddressService.getIpAddress(ip).getAddress2();
		UserStockClosingPosition uscp = new UserStockClosingPosition();
		uscp.setPositionType(usp.getPositionType());
		uscp.setUserId(usp.getUserId());
		uscp.setStockName(usp.getStockName());
		uscp.setStockCode(usp.getStockCode());
		uscp.setStockType(usp.getStockType());
		uscp.setStockPlate(usp.getStockPlate());
		uscp.setPositionDirection(usp.getPositionDirection());
		uscp.setBuyingPrice(usp.getBuyingPrice());
		uscp.setBuyingShares(usp.getBuyingShares());
		uscp.setLever(usp.getLever());
		uscp.setBuyingFee(usp.getBuyingFee());
		uscp.setSpreadFee(spreadFee);
		uscp.setBuyingStampDuty(usp.getBuyingStampDuty());
//		uscp.setIsBlockTrading(usp.getIsBlockTrading());
		uscp.setTradingOrderSn(OrderNumberGenerator.create(1));
		uscp.setPositionTime(usp.getPositionTime());
		uscp.setClosingTime(now);
		uscp.setSellingPrice(nowPrice);
		uscp.setSellingShares(usp.getBuyingShares());
		uscp.setSellingFee(sellingFee);
		uscp.setAmountReceived(amountReceived);
		uscp.setSellingStampDuty(sellingStampDuty);
		uscp.setFloatingProfit(floatingProfit);
		uscp.setActualProfit(actualProfit);
		uscp.setPositionId(usp.getId());
		uscp.setExchangeRate(exchangeRate);
		uscp.insert();
		UserPositionChangeRecord uspcr = new UserPositionChangeRecord();
		uspcr.setUserId(usp.getUserId());
		uspcr.setPositionId(id);
		uspcr.setStockCode(usp.getStockCode());
		uspcr.setStockName(usp.getStockName());
		uspcr.setDataChangeTypeCode(UserStockPositionChangeTypeEnum.CLOSING_POSITION.getCode());
		uspcr.setDataChangeTypeName(UserStockPositionChangeTypeEnum.CLOSING_POSITION.getName());
		uspcr.setOldContent(usp.toString());
		usp.setPositionStatus(5);
		uspcr.setNewContent(usp.toString());
		uspcr.setOperator(operator);
		uspcr.setIp(ip);
		uspcr.setIpAddress(address);
		uspcr.setCreateTime(now);
		uspcr.insert();
		this.lambdaUpdate().eq(UserStockPosition::getId, id).set(UserStockPosition::getPositionStatus, 5).update();
		deSummary.append("\n实际盈亏：").append(actualProfit);
		deSummary.append("\n到账金额：").append(deAmt);
		//平仓-增加用户金额
		this.userInfoService.updateUserAvailableAmt(usp.getUserId(), AmtDeTypeEnum.ClosingPosition, deAmt, deSummary.toString(), currency, exchangeRate, ip, address, operator,uscp.getTradingOrderSn());		
		//执行融资还款
		userFinancingInterestInfoService.doFinancingRepayment(usp.getUserId(), deAmt, "持仓id："+id+deSummary.toString(), currency, exchangeRate, ip, address, operator,uscp.getTradingOrderSn());
		//10、跟据交易添加返佣明细
		BigDecimal operateFollowSum = deAmt.divide(exchangeRate, 2, RoundingMode.HALF_UP);//操作金额转人名币（=deAmt除以汇率）
		UserInfo userInfo = userInfoService.getById(usp.getUserId());
		userInfoService.addUserRebateDetailOnTransaction(userInfo, operateFollowSum, 2, uscp.getTradingOrderSn());
		return Response.success();
	}

	/**
	 * APP股票-卖出-加载持仓列表
	 */
	@Override
	public void stockPositionList(Page<UserStockPositionListVO> page, Integer userId, String stockType, String stockCode) {
		userStockPositionMapper.stockPositionList(page, userId, stockType, stockCode);
		List<UserStockPositionListVO> list = page.getRecords();
		if(list.size() == 0) 
			return;
		List<String> stockGids = new ArrayList<>();
		List<String> hkOrUsStockGids = new ArrayList<>();
		for(UserStockPositionListVO i : list) {
			String gid = i.getStockType() + i.getStockCode();
			if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
					|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
					|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
				
				if(!stockGids.contains(gid)) {
					stockGids.add(gid);
				}
			} else {
				if(!hkOrUsStockGids.contains(gid)) {
					hkOrUsStockGids.add(gid);
				}
			}
		}
		if(stockGids.size() > 0) {
			 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
			 list.forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     BigDecimal buyingShares = new BigDecimal(i.getBuyingShares());
					     i.setMarketValue(i.getNowPrice().multiply(buyingShares));
					     BigDecimal floatingProfit = stockQuotesVO.getNowPrice().subtract(i.getBuyingPrice()).multiply(buyingShares);
					     if(i.getPositionDirection() == Constant.SHORT_SELLING) {
					    	floatingProfit = BigDecimal.ZERO.subtract(floatingProfit);
					     }
					     i.setFloatingProfit(floatingProfit);
					 }
				 });
			 });
		}
		if (hkOrUsStockGids.size() > 0) {
			List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
			list.forEach(i -> {
				stockQuotesVOList.forEach(stockQuotesVO -> {
					if (stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						i.setNowPrice(stockQuotesVO.getNowPrice());
						 BigDecimal buyingShares = new BigDecimal(i.getBuyingShares());
					     i.setMarketValue(i.getNowPrice().multiply(buyingShares));
					     BigDecimal floatingProfit = stockQuotesVO.getNowPrice().subtract(i.getBuyingPrice()).multiply(buyingShares);
					     if (i.getPositionDirection() == Constant.SHORT_SELLING) {
							floatingProfit = BigDecimal.ZERO.subtract(floatingProfit);
						}
						i.setFloatingProfit(floatingProfit);
					}
				});
			});
		}
	}

	/**
	 * APP资产-持仓详情
	 */
	@Override
	public Response<StockPositionDetailVO> stockPositionDetail(Integer id, Integer userId) {
		UserStockPosition usp = this.lambdaQuery().eq(UserStockPosition::getId, id).eq(UserStockPosition::getUserId, userId).one();
		if(usp == null) {
			return Response.fail("持仓信息错误");
		}
		StockPositionDetailVO vo = new StockPositionDetailVO();
		vo.setId(usp.getId());
		vo.setStockName(usp.getStockName());
		vo.setStockCode(usp.getStockCode());
		vo.setStockType(usp.getStockType());
		vo.setStockPlate(usp.getStockPlate());
		vo.setPositionDirection(usp.getPositionDirection());
		vo.setBuyingPrice(usp.getBuyingPrice());
		vo.setBuyingShares(usp.getBuyingShares());
		vo.setLever(usp.getLever());
		vo.setPositionTime(usp.getPositionTime());
//		vo.setIsBlockTrading(usp.getIsBlockTrading());
		StockQuotesVO q;
		switch(usp.getStockType()) {
		case "us":
		case "hk":
			q = EastMoneyApiAnalysis.getStockRealTimeData(usp.getStockType(), usp.getStockCode());
			break;
		default:
			q = SinaApi.getSinaStock(usp.getStockType(), usp.getStockCode());
			break;
		}
		vo.setNowPrice(q.getNowPrice());
		vo.setPercentageIncrease(q.getPercentageIncrease());
		vo.setMarketValue(q.getNowPrice().multiply(new BigDecimal(usp.getBuyingShares())));
		vo.setIncrease(q.getNowPrice().subtract(q.getPrevClose()));
		int unavailableShares = 0;
		if(usp.getLockInPeriod() > 0) {
			unavailableShares = this.userStockPendingMapper.getUnavailableShares(usp.getId(), usp.getLockInPeriod());
			unavailableShares = unavailableShares > usp.getBuyingShares() ? usp.getBuyingShares() : unavailableShares;			
		}
		Integer sharesAvailableForSale = vo.getBuyingShares() - unavailableShares;
		vo.setSharesAvailableForSale(sharesAvailableForSale);
		return Response.successData(vo);
	}

}
