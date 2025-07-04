package service.impl;

import entity.IpAddress;
import entity.UserInfo;
import entity.UserPositionChangeRecord;
import entity.UserStockPending;
import entity.UserStockPosition;
import entity.common.Response;
import entity.common.StockInfo;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import enums.StockPendingStatusEnum;
import enums.StockTypeEnum;
import enums.UserFundingStatusEnum;
import enums.UserRealAuthStatusEnum;
import enums.UserStockPositionChangeTypeEnum;
import mapper.UserStockPendingMapper;
import service.IpAddressService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserFinancingInterestInfoService;
import service.UserInfoService;
import service.UserStockPendingService;
import service.UserStockPositionService;
import utils.BuyAndSellUtils;
import utils.EastMoneyApiAnalysis;
import utils.OrderNumberGenerator;
import utils.SinaApi;
import utils.StringUtil;
import vo.common.StockQuotesVO;
import vo.manager.PendingListSearchParamVO;
import vo.manager.PendingListVO;
import vo.manager.PendingTransferDetailVO;
import vo.manager.TransferPositionSearchParamVO;
import vo.server.BuyStockParamVO;
import vo.server.BuyingStockPageVO;
import vo.server.StockPendingDetailVO;
import vo.server.StockPendingListVO;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import constant.Constant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户股票委托订单表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Service
public class UserStockPendingServiceImpl extends ServiceImpl<UserStockPendingMapper, UserStockPending> implements UserStockPendingService {

	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private UserStockPendingMapper userStockPendingMapper;
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@Resource
	UserFinancingInterestInfoService userFinancingInterestInfoService;
	/**
	 * 持仓管理-委托订单
	 */
	@Override
	public void managerPendingList(Page<PendingListVO> page, PendingListSearchParamVO param) {
		userStockPendingMapper.managerPendingList(page, param);
		List<PendingListVO> list = page.getRecords();
		if(list.size() == 0) 
			return;
		List<String> stockGids = new ArrayList<>();
		List<String> hkOrUsStockGids = new ArrayList<>();
		for(PendingListVO i : list) {
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

	/**
	 * 持仓管理-委托订单-转入详情（参数传委托单id）
	 */
	@Override
	public Response<PendingTransferDetailVO> transferDetail(Integer id) {
		UserStockPending userStockPending = this.getById(id);
		if (userStockPending != null) {
			PendingTransferDetailVO pendingTransferDetailVO = new PendingTransferDetailVO();
			pendingTransferDetailVO.setId(userStockPending.getId());
			pendingTransferDetailVO.setStockName(userStockPending.getStockName());
			pendingTransferDetailVO.setStockCode(userStockPending.getStockCode());
			pendingTransferDetailVO.setStockType(userStockPending.getStockType());
			pendingTransferDetailVO.setBuyingShares(userStockPending.getBuyingShares());
			pendingTransferDetailVO.setSharesOfHand(EastMoneyApiAnalysis.getSharesOfHand(userStockPending.getStockType(), userStockPending.getStockCode()));
			return Response.successData(pendingTransferDetailVO);
		}
		return Response.fail("委托订单详情不存在，委托单id:"+id);
	}

	/**
	 * 持仓管理-委托订单-转入持仓（单笔和批量通用，参数传委托单id集合，格式json[1,2,3,n..]）
	 */
	@Override
	@Transactional
	public Response<Void> pendingTransferPosition(List<TransferPositionSearchParamVO> searchParamList, String ip,
			String operator) {
		if(searchParamList == null || searchParamList.size() == 0) {
			return Response.fail("请选择要操作的订单");
		}
		if(searchParamList.size() == 0) {
			return Response.fail("请选择要操作的订单");
		}
		searchParamList.sort((p1, p2) -> Integer.compare(p1.getId(), p2.getId()));
		List<Integer> ids = new ArrayList<>();
		searchParamList.forEach(i->{
			ids.add(i.getId());
		});
		List<UserStockPending> list = this.lambdaQuery().in(UserStockPending::getId, ids).orderByAsc(UserStockPending::getId).list();
		HashMap<String, StockInfo> stockInfoMap = new HashMap<>();
		for(int i = 0; i < list.size(); i++) {
			UserStockPending usp = list.get(i);
			if(usp.getPositionStatus() != StockPendingStatusEnum.PENDING.getCode()) {
				return Response.fail("订单：【id：" + usp.getId() + "】状态不是委托中，请重新选择");
			}
			StockInfo si = stockInfoMap.get(usp.getStockType() + usp.getStockCode());
			if(si == null) {
				si = stockInfoService.getStockInfoByStockTypeAndStockCode(usp.getStockType(),usp.getStockCode());
				if(si == null) {
					return Response.fail("订单：【id：" + usp.getId() + "】转入失败，股票：" + usp.getStockName() + "(" + usp.getStockCode() + ")数据不存在或已被删除");
				}
				stockInfoMap.put(usp.getStockType() + usp.getStockCode(), si);
			}
			int sharesOfHand = EastMoneyApiAnalysis.getSharesOfHand(si.getStockType(), si.getStockCode());
			TransferPositionSearchParamVO param = searchParamList.get(i);
			if(param.getShares() % sharesOfHand != 0) {
				return Response.fail("转入失败，股票：" + usp.getStockName() + "(" + usp.getStockCode() + ")，转入的股数不符合最小买卖单位的整数倍，请返回修改。");
			}
		}
		IpAddress ia = this.ipAddressService.getIpAddress(ip);
		Date now = new Date();
		StockParamConfig spc = sysParamConfigService.getSysParamConfig();
		for(int i = 0; i < list.size(); i++) {
			UserStockPending usp = list.get(i);
			TransferPositionSearchParamVO param = searchParamList.get(i);
			int buyingShares = param.getShares();
			UserStockPosition newUsp = userStockPositionService.lambdaQuery()
					.eq(UserStockPosition::getUserId, usp.getUserId())
					.eq(UserStockPosition::getStockType, usp.getStockType())
					.eq(UserStockPosition::getStockCode, usp.getStockCode())
//					.eq(UserStockPosition::getIsBlockTrading, usp.getIsBlockTrading())
					.eq(UserStockPosition::getPositionDirection, usp.getPositionDirection())
					.eq(UserStockPosition::getPositionStatus, 2)					
					.eq(UserStockPosition::getLever, usp.getLever()).one();
			StockInfo si = stockInfoMap.get(usp.getStockType() + usp.getStockCode());
			Integer lockInPeriod = 0;
			if(lockInPeriod == null) {
				switch(usp.getStockType()) {
				case "us":
					lockInPeriod = spc.getMarketUsLockInPeriod();
					break;
				case "hk":
					lockInPeriod = spc.getMarketHkLockInPeriod();
					break;
				default :
					lockInPeriod = spc.getMarketALockInPeriod();
					break;
				}
			}
			if(newUsp == null) {
				newUsp = new UserStockPosition();
				newUsp.setPositionType(usp.getPositionType());
				newUsp.setUserId(usp.getUserId());
				newUsp.setStockName(usp.getStockName());
				newUsp.setStockCode(usp.getStockCode());
				newUsp.setStockType(usp.getStockType());
				newUsp.setStockPlate(usp.getStockPlate());
				newUsp.setPositionTime(now);
				newUsp.setBuyingFee(usp.getBuyingFee());
				newUsp.setPositionStatus(StockPendingStatusEnum.COMPLETED.getCode());
				newUsp.setBuyingPrice(usp.getBuyingPrice());
				newUsp.setPositionDirection(usp.getPositionDirection());
				newUsp.setBuyingShares(buyingShares);
				newUsp.setLever(usp.getLever());
				newUsp.setIsLock(false);
				newUsp.setBuyingStampDuty(usp.getBuyingStampDuty());
//				newUsp.setIsBlockTrading(usp.getIsBlockTrading());
				newUsp.setLockInPeriod(lockInPeriod);
				newUsp.insert();
			} else {
				BigDecimal totalShares = new BigDecimal(newUsp.getBuyingShares() + buyingShares);
				BigDecimal totalMarketValue = newUsp.getBuyingPrice().multiply(new BigDecimal(newUsp.getBuyingShares()));
				totalMarketValue = totalMarketValue.add(usp.getBuyingPrice().multiply(new BigDecimal(usp.getBuyingShares())));
				BigDecimal holdPrice = totalMarketValue.divide(totalShares, 3, RoundingMode.DOWN);
				BigDecimal buyingFee = newUsp.getBuyingFee().add(usp.getBuyingFee());
				BigDecimal buyingStampDuty = newUsp.getBuyingStampDuty().add(usp.getBuyingStampDuty());
				this.userStockPositionService.lambdaUpdate()
						.eq(UserStockPosition::getId, newUsp.getId())
						.set(UserStockPosition::getBuyingPrice, holdPrice)
						.set(UserStockPosition::getBuyingFee, buyingFee)
						.set(UserStockPosition::getBuyingShares, totalShares)
						.set(UserStockPosition::getLockInPeriod, lockInPeriod)
						.set(UserStockPosition::getBuyingStampDuty, buyingStampDuty).update();
				newUsp.setBuyingFee(holdPrice);
				newUsp.setBuyingFee(buyingFee);
				newUsp.setBuyingShares(totalShares.intValue());
				newUsp.setLockInPeriod(lockInPeriod);
				newUsp.setBuyingStampDuty(buyingStampDuty);
			}
			BigDecimal orderTotalAmount = usp.getBuyingPrice().multiply(new BigDecimal(usp.getBuyingShares())).divide(new BigDecimal(usp.getLever()), 2, RoundingMode.HALF_UP);
			orderTotalAmount = orderTotalAmount.add(usp.getBuyingFee()).add(usp.getBuyingStampDuty());
			if(param.getShares() < usp.getBuyingShares()) {
				BigDecimal newShares = new BigDecimal(buyingShares);
				BigDecimal newBuyingFee = usp.getBuyingPrice().multiply(newShares).multiply(usp.getBuyingFeeRate()).setScale(2, RoundingMode.HALF_UP);
				BigDecimal newBuyingStampDuty = usp.getBuyingPrice().multiply(newShares).multiply(usp.getBuyingStampDutyRate()).setScale(2, RoundingMode.HALF_UP);
				UserStockPending nextUsp = new UserStockPending();
				nextUsp.setUserId(usp.getUserId());
				nextUsp.setPositionType(usp.getPositionType());
				nextUsp.setStockName(usp.getStockName());
				nextUsp.setStockCode(usp.getStockCode());
				nextUsp.setStockType(usp.getStockType());
				nextUsp.setStockPlate(usp.getStockPlate());
				nextUsp.setTradingOrderSn(usp.getTradingOrderSn());
				nextUsp.setBuyingPrice(usp.getBuyingPrice());
				nextUsp.setPositionDirection(usp.getPositionDirection());
				nextUsp.setBuyingShares(usp.getBuyingShares() - buyingShares);
				nextUsp.setLever(usp.getLever());
				nextUsp.setBuyingFee(usp.getBuyingFee().subtract(newBuyingFee));
				nextUsp.setBuyingFeeRate(usp.getBuyingFeeRate());
				nextUsp.setExchangeRate(usp.getExchangeRate());
				nextUsp.setBuyingStampDuty(usp.getBuyingStampDuty().subtract(newBuyingStampDuty));
				nextUsp.setBuyingStampDutyRate(usp.getBuyingStampDutyRate());
				nextUsp.setPendingTime(usp.getPendingTime());
				nextUsp.insert();
				usp.setBuyingShares(buyingShares);
				usp.setBuyingFee(newBuyingFee);
				usp.setBuyingStampDuty(newBuyingStampDuty);
			}
			this.lambdaUpdate().eq(UserStockPending::getId, usp.getId())
				.set(UserStockPending::getPositionStatus, StockPendingStatusEnum.COMPLETED.getCode())
				.set(UserStockPending::getPositionTime, now)
				.set(UserStockPending::getBuyingShares, usp.getBuyingShares())
				.set(UserStockPending::getBuyingFee, usp.getBuyingFee())
				.set(UserStockPending::getBuyingStampDuty, usp.getBuyingStampDuty())
				.set(UserStockPending::getPositionId, newUsp.getId())
				.update();
			UserPositionChangeRecord uspcr = new UserPositionChangeRecord();
			uspcr.setUserId(usp.getUserId());
			uspcr.setPositionId(newUsp.getId());
			uspcr.setStockCode(usp.getStockCode());
			uspcr.setStockName(usp.getStockName());
			uspcr.setDataChangeTypeCode(UserStockPositionChangeTypeEnum.POSITION_STATUS.getCode());
			uspcr.setDataChangeTypeName(UserStockPositionChangeTypeEnum.POSITION_STATUS.getName());
			uspcr.setOldContent(usp.toString());
			uspcr.setNewContent(newUsp.toString());
			uspcr.setOperator(operator);
			uspcr.setIp(ip);
			uspcr.setIpAddress(ia.getAddress2());
			uspcr.setCreateTime(now);
			uspcr.insert();
			usp.setPositionStatus(StockPendingStatusEnum.COMPLETED.getCode());
			userInfoService.updateUserAvailableAmt(usp.getUserId(), AmtDeTypeEnum.TransferPosition, orderTotalAmount, "转入持仓：\n" + usp.toString(), CurrencyEnum.getByStockType(usp.getStockType()), usp.getExchangeRate(), ip, ia.getAddress2(), operator,usp.getTradingOrderSn());
			//6、通过持仓id，处理融资信息
			BigDecimal actualSubscriptionAmount = usp.getBuyingPrice().multiply(new BigDecimal(buyingShares));//实际认购金额 = 购买价格*委托股数
			userFinancingInterestInfoService.doUserFinancingInterestInfoByPositionId(newUsp.getId(),actualSubscriptionAmount);
			//10、跟据交易添加返佣明细
			BigDecimal operateFollowSum = orderTotalAmount.divide(usp.getExchangeRate(), 2, RoundingMode.HALF_UP);//操作金额转人名币（=orderTotalAmount除以汇率）
			UserInfo userInfo = userInfoService.getById(usp.getUserId());
			userInfoService.addUserRebateDetailOnTransaction(userInfo, operateFollowSum, 1, usp.getTradingOrderSn());
		}
		return Response.success();
	}

	/**
	 * 持仓管理-委托订单-拒绝订单（单笔和批量通用，参数传委托单id集合，格式json[1,2,3,n..]）
	 */
	@Override
	@Transactional
	public Response<Void> reject(List<Integer> ids, String ip, String operator) {
		if(ids == null || ids.size() == 0) {
			return Response.fail("请选择要操作的订单");
		}
		List<UserStockPending> list = this.lambdaQuery().in(UserStockPending::getId, ids).list();
		if(list.size() == 0) {
			return Response.fail("请选择要操作的订单");
		}
		for(UserStockPending usp : list) {
			if(usp.getPositionStatus() != StockPendingStatusEnum.PENDING.getCode()) {
				return Response.fail("订单：【id：" + usp.getId() + "】状态不是委托中，请重新选择");
			}
		}
		IpAddress ia = this.ipAddressService.getIpAddress(ip);
		Date now = new Date();
		for(UserStockPending usp : list) {
			BigDecimal orderTotalAmount = usp.getBuyingPrice().multiply(new BigDecimal(usp.getBuyingShares())).divide(new BigDecimal(usp.getLever()), 2, RoundingMode.HALF_UP);
			orderTotalAmount = orderTotalAmount.add(usp.getBuyingFee()).add(usp.getBuyingStampDuty());
			this.lambdaUpdate().eq(UserStockPending::getId, usp.getId())
				.set(UserStockPending::getPositionStatus, StockPendingStatusEnum.REJECTED.getCode())
				.update();
//			if(usp.getIsBlockTrading()) {
//				StockInfo si = stockInfoService.getStockInfoByStockTypeAndStockCode(usp.getStockType(),usp.getStockCode());
//				if(si != null) {
//					stockInfoService.lambdaUpdate()
//						.eq(StockInfo::getId, si.getId())
//						.set(StockInfo::getSoldBlockTradingNum, si.getSoldBlockTradingNum() - usp.getBuyingShares()).update();
//				}
//			}
			UserPositionChangeRecord uspcr = new UserPositionChangeRecord();
			uspcr.setUserId(usp.getUserId());
			uspcr.setPositionId(usp.getId());
			uspcr.setStockCode(usp.getStockCode());
			uspcr.setStockName(usp.getStockName());
			uspcr.setDataChangeTypeCode(UserStockPositionChangeTypeEnum.POSITION_STATUS.getCode());
			uspcr.setDataChangeTypeName(UserStockPositionChangeTypeEnum.POSITION_STATUS.getName());
			uspcr.setOldContent(usp.toString());
			usp.setPositionStatus(StockPendingStatusEnum.REJECTED.getCode());
			uspcr.setNewContent(usp.toString());
			uspcr.setOperator(operator);
			uspcr.setIp(ip);
			uspcr.setIpAddress(ia.getAddress2());
			uspcr.setCreateTime(now);
			uspcr.insert();
			userInfoService.updateUserAvailableAmt(usp.getUserId(), AmtDeTypeEnum.RejectedPending, orderTotalAmount, "拒绝委托订单：\n" + usp.toString(), CurrencyEnum.getByStockType(usp.getStockType()), usp.getExchangeRate(), ip, ia.getAddress2(), operator,usp.getTradingOrderSn());
		}
		return Response.success();
	}

	/**
	 * APP股票-买入-页面数据加载
	 */
	@Override
	public Response<BuyingStockPageVO> buyingStockPage(Integer userId, String stockCode, String stockType) {
		StockInfo si = stockInfoService.getStockInfoByStockTypeAndStockCode(stockType, stockCode);
		if(si == null) {
			return Response.fail("产品信息错误");
		}
		String am_begin, am_end, pm_begin, pm_end;
		StockQuotesVO sq;
		CurrencyEnum currency;
		BigDecimal buyingFeeRate, stampDutyRate = BigDecimal.ZERO;
		StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
		switch(si.getStockType()) {
        case "us":
        	am_begin = stockParamConfig.getMarketUs_amTradingStart();
        	am_end = stockParamConfig.getMarketUs_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketUs_pmTradingStart();
        	pm_end = stockParamConfig.getMarketUs_pmTradingEnd();
        	sq = EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode);
        	buyingFeeRate = stockParamConfig.getMarketUsBuyingFeeRate();
        	currency = CurrencyEnum.USD;
        	break;
        case "hk":
        	am_begin = stockParamConfig.getMarketHk_amTradingStart();
        	am_end = stockParamConfig.getMarketHk_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketHk_pmTradingStart();
        	pm_end = stockParamConfig.getMarketHk_pmTradingEnd();
        	sq = EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode);
        	buyingFeeRate = stockParamConfig.getMarketHkBuyingFeeRate();
        	currency = CurrencyEnum.HKD;
        	stampDutyRate = stockParamConfig.getMarketHkStampDutyRate();
        	break;
        default:
        	am_begin = stockParamConfig.getMarketA_amTradingStart();
        	am_end = stockParamConfig.getMarketA_amTradingEnd();
        	pm_begin = stockParamConfig.getMarketA_pmTradingStart();
        	pm_end = stockParamConfig.getMarketA_pmTradingEnd();
        	sq = SinaApi.getSinaStock(stockType, stockCode);
        	buyingFeeRate = stockParamConfig.getMarketABuyingFeeRate();
        	currency = CurrencyEnum.CNY;
        	break;
        }
		if(sq.getGid() == null || sq.getNowPrice().compareTo(BigDecimal.ZERO) < 1) {
        	return Response.fail("获取行情价格失败，请稍后再交易");
        }
		BuyingStockPageVO vo = new BuyingStockPageVO();
		UserInfo userInfo = userInfoService.getById(userId);
		BigDecimal exchangeRate = sysParamConfigService.getExchangeRate(currency);
//		if(!userInfo.getTradeEnable() || si.getIsLock()) {
//			vo.setIsTradable(false);
//		}else {
//			vo.setIsTradable(true);
//		}
		vo.setStockCode(stockCode);
		vo.setStockType(stockType);
		vo.setStockName(si.getStockName());
		vo.setStockPlate(si.getStockPlate());
		vo.setIncrease(sq.getNowPrice().subtract(sq.getPrevClose()));
		vo.setPercentageIncrease(sq.getPercentageIncrease());
		vo.setNowPrice(sq.getNowPrice());
		String lever = stockParamConfig.getLevers();
		if(StringUtil.isEmpty(lever)) {
			vo.setLevers(new String[]{"1"});
		} else {
			vo.setLevers(lever.split("/"));
		}
		vo.setBuyingFeeRate(buyingFeeRate);
		vo.setStampDutyRate(stampDutyRate);
		vo.setFinancingAvailable(userInfo.getFundingStatus() == UserFundingStatusEnum.REVIEWED.getCode() ? true : false);
		vo.setExchangeRate(exchangeRate);
		//实时获取一手的股票数
		vo.setSharesOfHand(EastMoneyApiAnalysis.getSharesOfHand(stockType, stockCode));
		//市场状态
		int status = BuyAndSellUtils.getTradingStatus(am_begin, am_end, pm_begin, pm_end, stockType,true);
		vo.setMarketStatus(status);
		//融资年华利率
		vo.setAnnualizedInterestRate(stockParamConfig.getAnnualizedInterestRate());
		return Response.successData(vo);
	}

	/**
	 * APP股票-买入
	 */
	@Override
	@Transactional
	public Response<Void> buyStock(Integer userId, BuyStockParamVO param, String ip) {
		StockInfo si = stockInfoService.getStockInfoByStockTypeAndStockCode(param.getStockType(), param.getStockCode());
		if(si == null) {
			return Response.fail("产品信息错误");
		}
//		if(si.getIsLock()) {
//			return Response.fail("该产品暂时停止交易");
//		}
		if(param.getBuyingShares() <= 0) {
			return Response.fail("请填写购买股数");
		}
		//一手股票数
		Integer sharesOfHand = EastMoneyApiAnalysis.getSharesOfHand(param.getStockType(), param.getStockCode());
		if(param.getBuyingShares() % sharesOfHand != 0) {
			return Response.fail("您所下订单不符合最小买卖单位的整数倍，请返回修改。");
		}
	    String am_begin, am_end, pm_begin, pm_end;
	    StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
	    StockQuotesVO sq;
	    BigDecimal buyingFeeRate, buyingStampDutyRate = BigDecimal.ZERO;
	    CurrencyEnum currency;
	    switch(si.getStockType()) {
	    case "us":
	    	am_begin = stockParamConfig.getMarketUs_amTradingStart();
	    	am_end = stockParamConfig.getMarketUs_amTradingEnd();
	    	pm_begin = stockParamConfig.getMarketUs_pmTradingStart();
	    	pm_end = stockParamConfig.getMarketUs_pmTradingEnd();
	    	sq =  EastMoneyApiAnalysis.getStockRealTimeData(param.getStockType(), param.getStockCode());
	    	buyingFeeRate = stockParamConfig.getMarketUsBuyingFeeRate();
	    	currency = CurrencyEnum.USD;
	    	break;
	    case "hk":
	    	am_begin = stockParamConfig.getMarketHk_amTradingStart();
	    	am_end = stockParamConfig.getMarketHk_amTradingEnd();
	    	pm_begin = stockParamConfig.getMarketHk_pmTradingStart();
	    	pm_end = stockParamConfig.getMarketHk_pmTradingEnd();
	    	sq = EastMoneyApiAnalysis.getStockRealTimeData(param.getStockType(), param.getStockCode());
	    	buyingFeeRate = stockParamConfig.getMarketHkBuyingFeeRate();
	    	buyingStampDutyRate = stockParamConfig.getMarketHkStampDutyRate();
	    	currency = CurrencyEnum.HKD;
	    	break;
	    default:
	    	am_begin = stockParamConfig.getMarketA_amTradingStart();
	    	am_end = stockParamConfig.getMarketA_amTradingEnd();
	    	pm_begin = stockParamConfig.getMarketA_pmTradingStart();
	    	pm_end = stockParamConfig.getMarketA_pmTradingEnd();
	    	sq = SinaApi.getSinaStock(param.getStockType(), param.getStockCode());
	    	buyingFeeRate = stockParamConfig.getMarketABuyingFeeRate();
	    	currency = CurrencyEnum.CNY;
	    	break;
	    }
	    int status = BuyAndSellUtils.getTradingStatus(am_begin, am_end, pm_begin, pm_end, param.getStockType(),true);
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
		int lever = param.getLever() == null || param.getLever() == 0 ? 1 : param.getLever();
		if(lever > 1 || param.getPositionDirection() == Constant.SHORT_SELLING) {
			if(userInfo.getFundingStatus() != UserFundingStatusEnum.REVIEWED.getCode()) {
				return Response.fail("委托失败，您还未开通融资账户");
			}
		}
		if(lever > 1) {
			String[] leverArr = stockParamConfig.getLevers().split("/");
			boolean isExists = false;
			for(String l : leverArr) {
				if(l.equals(String.valueOf(lever))) {
					isExists = true;
					break;
				}
			}
			if(!isExists) {
				return Response.fail("委托失败，您选择的杆杠位数错误");
			}
		}
		BigDecimal buyingShares = new BigDecimal(param.getBuyingShares());
		BigDecimal orderAmount = sq.getNowPrice().multiply(buyingShares);
		if(stockParamConfig.getMinBuyingAmount() != null && stockParamConfig.getMinBuyingAmount().compareTo(BigDecimal.ZERO) == 1) {
			if(orderAmount.compareTo(stockParamConfig.getMinBuyingAmount()) == -1) {
				return Response.fail("委托失败，最小购买金额为：" + stockParamConfig.getMinBuyingAmount());
			}
		}
		if(stockParamConfig.getMaxBuyingAmount() != null && stockParamConfig.getMaxBuyingAmount().compareTo(BigDecimal.ZERO) == 1) {
			if(orderAmount.compareTo(stockParamConfig.getMaxBuyingAmount()) == 1) {
				return Response.fail("委托失败，最大购买金额为：" + stockParamConfig.getMaxBuyingAmount());
			}
		}
		if(stockParamConfig.getMinBuyingShares() > 0) {
			if(param.getBuyingShares() < stockParamConfig.getMinBuyingShares()) {
				return Response.fail("委托失败，最低购买股数为：" + stockParamConfig.getMinBuyingShares());
			}
		}
		if(stockParamConfig.getMaxBuyingShares() > 0) {
			if(param.getBuyingShares() > stockParamConfig.getMaxBuyingShares()) {
				return Response.fail("委托失败，最高购买股数为：" + stockParamConfig.getMaxBuyingShares());
			}
		}
		if(stockParamConfig.getMinutesLimimtOfBuyingTimes() > 0 && stockParamConfig.getTimesLimimtOfBuyingTimes() > 0) {
			List<Integer> positionStatusList = new ArrayList<>();
			positionStatusList.add(StockPendingStatusEnum.PENDING.getCode());
			positionStatusList.add(StockPendingStatusEnum.COMPLETED.getCode());
			ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
			Timestamp timestamp = Timestamp.valueOf(zonedDateTime.toLocalDateTime());
			long time = timestamp.getTime() - 1000 * 60 * stockParamConfig.getMinutesLimimtOfBuyingTimes();
			int count = this.lambdaQuery()
						.eq(UserStockPending::getUserId, userId)
						.eq(UserStockPending::getStockCode, param.getStockCode())
						.eq(UserStockPending::getStockType, param.getStockType())
						.ge(UserStockPending::getPendingTime, new Date(time))
						.in(UserStockPending::getPositionStatus, positionStatusList)
						.count();
			if(count >= stockParamConfig.getTimesLimimtOfBuyingTimes()) {
				return Response.fail("委托失败，频繁交易：" + stockParamConfig.getMinutesLimimtOfBuyingTimes() + "分钟内同一股票持仓不得超过" + stockParamConfig.getTimesLimimtOfBuyingTimes() + "次");
			}
		}
		if (stockParamConfig.getMinutesLimimtOfBuyingShares() > 0 && stockParamConfig.getSharesLimimtOfBuyingShares() > 0) {
			List<Integer> positionStatusList = new ArrayList<>();
			positionStatusList.add(StockPendingStatusEnum.PENDING.getCode());
			positionStatusList.add(StockPendingStatusEnum.COMPLETED.getCode());
			ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
			Timestamp timestamp = Timestamp.valueOf(zonedDateTime.toLocalDateTime());
			long time = timestamp.getTime() - 1000 * 60 * stockParamConfig.getMinutesLimimtOfBuyingShares();
			QueryWrapper<UserStockPending> qw = new QueryWrapper<>();
			qw.select("ifnull(sum(buying_shares),0) as buying_shares");
			qw.eq("user_id", userId);
			qw.eq("stock_code", param.getStockCode());
			qw.eq("stock_type", param.getStockType());
			qw.ge("pending_time", new Date(time));
			qw.in("position_status", positionStatusList);
			Map<String, Object> map = this.getMap(qw);
			int shares = (Integer) map.get("buying_shares");
			if(shares >= stockParamConfig.getSharesLimimtOfBuyingShares()) {
				return Response.fail("委托失败，频繁交易：" + stockParamConfig.getMinutesLimimtOfBuyingShares() + "分钟内交易股数不得超过" + stockParamConfig.getSharesLimimtOfBuyingShares());
			}
		}
		BigDecimal buyingFee = orderAmount.multiply(buyingFeeRate).setScale(2, RoundingMode.HALF_UP);
		UserStockPending usp = new UserStockPending();
		usp.setUserId(userId);
		usp.setPositionType(userInfo.getAccountType());
		usp.setStockName(si.getStockName());
		usp.setStockCode(param.getStockCode());
		usp.setStockType(param.getStockType());
		usp.setStockPlate(si.getStockPlate());
		usp.setTradingOrderSn(OrderNumberGenerator.create(0));
		usp.setBuyingPrice(sq.getNowPrice());
		usp.setPositionDirection(param.getPositionDirection());
		usp.setBuyingShares(param.getBuyingShares());
		usp.setLever(lever);
		usp.setBuyingFee(buyingFee);
		usp.setBuyingFeeRate(buyingFeeRate);
		BigDecimal exchangeRate = this.sysParamConfigService.getExchangeRate(currency);
		usp.setExchangeRate(exchangeRate);
		BigDecimal deAmt = orderAmount.divide(new BigDecimal(lever), 2, RoundingMode.HALF_UP);     
		deAmt = deAmt.add(buyingFee);
		BigDecimal buyingStampDuty = orderAmount.multiply(buyingStampDutyRate).setScale(2, RoundingMode.HALF_UP);
		usp.setBuyingStampDuty(buyingStampDuty);
		usp.setBuyingStampDutyRate(buyingStampDutyRate);
		deAmt = deAmt.add(usp.getBuyingStampDuty());
		StringBuilder sb = new StringBuilder("购买股票：").append(si.getStockName()).append("(").append(si.getStockCode()).append(")(").append(StockTypeEnum.getNameByCode(param.getStockType())).append(")，价格：");
		sb.append(sq.getNowPrice()).append("，数量：").append(param.getBuyingShares()).append("，杠杆：").append(lever).append("，手续费：").append(buyingFee);
		sb.append("，印花税：").append(buyingStampDuty);
		BigDecimal availableAmt = userInfo.getAvailableAmt().multiply(exchangeRate);
		if(availableAmt.compareTo(deAmt) == -1) {
			if(param.getNowPrice().compareTo(sq.getNowPrice()) == -1) {
				return Response.fail("委托失败，价格变动");
			}
			return Response.fail("委托失败，您的可用余额不足");
		}
		userInfoService.updateUserAvailableAmt(userId, AmtDeTypeEnum.BuyStock, deAmt, sb.toString(), currency, exchangeRate, ip, ipAddressService.getIpAddress(ip).getAddress2(), userInfo.getOperator(),usp.getTradingOrderSn());
		usp.insert();
		return Response.success();
	}

	/**
	 * APP资产-挂单列表
	 */
	@Override
	public void pendingList(Page<StockPendingListVO> page, Integer userId, Integer positionStatus) {
		userStockPendingMapper.pendingList(page, userId, positionStatus);
		List<StockPendingListVO> list = page.getRecords();
		if(list.size() == 0) 
			return;
		List<String> stockGids = new ArrayList<>();
		List<String> hkOrUsStockGids = new ArrayList<>();
		for(StockPendingListVO i : list) {
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

	@Override
	public Response<StockPendingDetailVO> pendingDetail(Integer id, Integer userId) {
		UserStockPending usp = this.lambdaQuery().eq(UserStockPending::getId, id).eq(UserStockPending::getUserId, userId).one();
		if(usp == null) {
			return Response.fail("订单信息错误");
		}
		StockPendingDetailVO vo = new StockPendingDetailVO();
		vo.setId(usp.getId());
		vo.setStockName(usp.getStockName());
		vo.setStockCode(usp.getStockCode());
		vo.setStockType(usp.getStockType());
		vo.setStockPlate(usp.getStockPlate());
		vo.setPositionDirection(usp.getPositionDirection());
		vo.setBuyingPrice(usp.getBuyingPrice());
		vo.setBuyingShares(usp.getBuyingShares());
		vo.setLever(usp.getLever());
		vo.setPendingTime(usp.getPendingTime());
//		vo.setIsBlockTrading(usp.getIsBlockTrading());
		vo.setPositionStatus(usp.getPositionStatus());
		vo.setTradingOrderSn(usp.getTradingOrderSn());
		vo.setBuyingFee(usp.getBuyingFee());
		vo.setBuyingStampDuty(usp.getBuyingStampDuty());
		BigDecimal shares = new BigDecimal(usp.getBuyingShares());
		vo.setOrderAmount(usp.getBuyingPrice().multiply(shares));
		vo.setFinalAmount(vo.getOrderAmount().add(vo.getBuyingFee()).add(vo.getBuyingStampDuty()));
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
		vo.setIncrease(q.getNowPrice().subtract(q.getPrevClose()));
		return Response.successData(vo);
	}

	/**
	 * APP资产-挂单取消、撤单
	 */
	@Override
	@Transactional
	public Response<Void> cancel(Integer id, Integer userId, String ip, String operator) {
		UserStockPending usp = this.lambdaQuery().eq(UserStockPending::getId, id).eq(UserStockPending::getUserId, userId).one();
		if(usp == null) {
			return Response.fail("订单不存在");
		}
		if(usp.getPositionStatus() != StockPendingStatusEnum.PENDING.getCode()) {
			return Response.fail("撤销失败，订单状态不是委托中");
		}
		Date now = new Date();
		BigDecimal orderTotalAmount = usp.getBuyingPrice().multiply(new BigDecimal(usp.getBuyingShares())).divide(new BigDecimal(usp.getLever()), 2, RoundingMode.HALF_UP);
		orderTotalAmount = orderTotalAmount.add(usp.getBuyingFee()).add(usp.getBuyingStampDuty());
		this.lambdaUpdate().eq(UserStockPending::getId, id)
			.set(UserStockPending::getPositionStatus, StockPendingStatusEnum.CANNELED.getCode())
			.update();
//		if(usp.getIsBlockTrading()) {
//			StockInfo si = stockInfoService.getStockInfoByStockTypeAndStockCode(usp.getStockType(), usp.getStockCode());
//			if(si != null) {
//				stockInfoService.lambdaUpdate()
//					.eq(StockInfo::getId, si.getId())
//					.set(StockInfo::getSoldBlockTradingNum, si.getSoldBlockTradingNum() - usp.getBuyingShares()).update();
//			}
//		}
		IpAddress ia = this.ipAddressService.getIpAddress(ip);
		UserPositionChangeRecord uspcr = new UserPositionChangeRecord();
		uspcr.setUserId(usp.getUserId());
		uspcr.setPositionId(id);
		uspcr.setStockCode(usp.getStockCode());
		uspcr.setStockName(usp.getStockName());
		uspcr.setDataChangeTypeCode(UserStockPositionChangeTypeEnum.POSITION_STATUS.getCode());
		uspcr.setDataChangeTypeName(UserStockPositionChangeTypeEnum.POSITION_STATUS.getName());
		uspcr.setOldContent(usp.toString());
		usp.setPositionStatus(StockPendingStatusEnum.CANNELED.getCode());
		uspcr.setNewContent(usp.toString());
		uspcr.setOperator(operator);
		uspcr.setIp(ip);
		uspcr.setIpAddress(ia.getAddress2());
		uspcr.setCreateTime(now);
		uspcr.insert();
		userInfoService.updateUserAvailableAmt(usp.getUserId(), AmtDeTypeEnum.CanneledPending, orderTotalAmount, "撤销委托订单：\n" + usp.toString(), CurrencyEnum.getByStockType(usp.getStockType()), usp.getExchangeRate(), ip, ia.getAddress2(), operator,usp.getTradingOrderSn());
		return Response.success();
	}

}
