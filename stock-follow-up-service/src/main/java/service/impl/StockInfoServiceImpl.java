package service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.SiteHotStock;
import entity.UserFavoriteStock;
import entity.common.Response;
import entity.common.StockInfo;
import entity.common.StockParamConfig;
import enums.CurrencyEnum;
import enums.StockMarketTypeEnum;
import enums.StockTypeEnum;
import mapper.StockInfoMapper;
import redis.RedisKeyPrefix;
import service.IpAddressService;
import service.SiteHotStockService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserFavoriteStockService;
import utils.BuyAndSellUtils;
import utils.EastMoneyApiAnalysis;
import utils.EastMoneyApiAnalysis.EastMoneyStockSearchReturn;
import utils.RedisDao;
import utils.SinaApi;
import utils.StringUtil;
import vo.common.StockDayTransactionParamVO;
import vo.common.StockDayTransactionVO;
import vo.common.StockQuotesVO;
import vo.server.StockData;
import vo.server.StockDetailVO;

/**
 * <p>
 * 股票产品信息表 服务实现类
 * </p>
 *
 * @author
 * @since 2024-11-07
 */
@Service
public class StockInfoServiceImpl extends ServiceImpl<StockInfoMapper, StockInfo>  implements StockInfoService {

	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserFavoriteStockService userFavoriteStockService;
	
	@Resource
	private RedisDao redisDao;
	
	@Resource
	private SiteHotStockService siteHotStockService;

	/**
	 * 获取股票信息，根据股票类型和股票代码
	 */
	@Override
	public StockInfo getStockInfoByStockTypeAndStockCode(String stockType, String stockCode) {
		StockInfo stockInfo = new StockInfo();
		StockQuotesVO stockQuotesVO = EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode);
		if (stockQuotesVO != null) {
			stockInfo.setStockName(stockQuotesVO.getStockName());
			stockInfo.setStockCode(stockCode);
			stockInfo.setStockType(stockType);
			stockInfo.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
			stockInfo.setNowPrice(stockQuotesVO.getNowPrice());
			stockInfo.setHeightPrice(stockQuotesVO.getHeightPrice());
			stockInfo.setLowPrice(stockQuotesVO.getLowPrice());
			stockInfo.setOpenPrice(stockQuotesVO.getOpenPrice());
			stockInfo.setVolume(stockQuotesVO.getVolume());
			stockInfo.setTurnover(stockQuotesVO.getTurnover());
			stockInfo.setPrevClose(stockQuotesVO.getPrevClose());
		}
		return stockInfo;
	}

	/**
	 * 首页-热门-股票列表
	 */
	@Override
	public Response<List<StockData>> getHotRankFromApi() {
		//空股票列表
		List<StockData> list = new ArrayList<StockData>();
		//A股
		List<StockData> marketA = redisDao.getBeanList(RedisKeyPrefix.getAStockHotRankKey(), StockData.class);
		if (marketA != null) {
			List<String> stockGids = new ArrayList<>();
			marketA.forEach(i->{
				stockGids.add(i.getStockType() + i.getStockCode());
			});
			List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
			marketA.forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     i.setStockName(stockQuotesVO.getStockName());
					 }
				 });
			 });
			list.addAll(marketA.subList(0, 4));
		}
		//港股
		List<StockData> marketHk = redisDao.getBeanList(RedisKeyPrefix.getHkStockHotRankKey(), StockData.class);
		if (marketHk != null) {
			List<String> stockGids = new ArrayList<>();
			marketHk.forEach(i-> {
				stockGids.add(i.getStockType() + i.getStockCode());
			});
			List<StockQuotesVO> stockQuotesVOList =  sysParamConfigService.getStockRealTimeList(stockGids);
			marketHk.forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     i.setStockName(stockQuotesVO.getStockName());
					 }
				 });
			 });
			list.addAll(marketHk.subList(0, 2));
		}
		//美股
		List<StockData> marketUs = redisDao.getBeanList(RedisKeyPrefix.getUsStockHotRankKey(), StockData.class);
		if (marketUs != null) {
			List<String> stockGids = new ArrayList<>();
			marketUs.forEach(i-> {
				stockGids.add(i.getStockType() + i.getStockCode());
			});
			List<StockQuotesVO> stockQuotesVOList =  sysParamConfigService.getStockRealTimeList(stockGids);
			marketUs.forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     i.setStockName(stockQuotesVO.getStockName());
					 }
				 });
			 });
			list.addAll(marketUs.subList(0, 2));
		}
		//打乱排序
		Collections.shuffle(list);
		return Response.successData(list);
	}
	
	/**
	 * 短期活跃股票
	 */
//	@Override
//	public Page<StockData> getActiveStockListFromApi(Integer pageNo, Integer pageSize,Integer stockMarketTypeCode) {
//		//成交量排序
//		return getStockQuoteListPage(pageNo, pageSize, "turnover", "desc", stockMarketTypeCode);
//	}
	
	/**
	 * 近1个月涨幅榜
	 */
	@Override
	public List<StockData> getIncreaseRateRank(Integer pageNo, Integer pageSize,Integer stockMarketTypeCode) {
		if(stockMarketTypeCode != null) {
				return getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", stockMarketTypeCode).getRecords();//涨幅排序
		}else {
			List<StockData> list = new ArrayList<StockData>();
			list.addAll(getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", StockMarketTypeEnum.Market_Hk.getCode()).getRecords());
			list.addAll(getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", StockMarketTypeEnum.Market_Us.getCode()).getRecords());
			list.addAll(getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", StockMarketTypeEnum.Market_A.getCode()).getRecords());
			list.sort(Comparator.comparing(StockData::getPercentageIncrease).reversed());//涨幅比例-降序;
			return list;
		}
	}

	/**
	 * 获取股票涨幅排行榜分页数据
	 * @param pageNo 
	 * @param pageSize
	 * @param sortTerm 排序条件
	 * @param sortType 排序类型
	 * @param reidsKey 缓存key
	 * @return
	 */
	@Override
	public Page<StockData> getStockQuoteListPage(Integer pageNo, Integer pageSize, String sortTerm, String sortType,
			Integer stockMarketTypeCode) {
		// 1、从缓存获取涨幅数据
		List<StockData> list = new ArrayList<StockData>();
		StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
		if(stockMarketTypeEnum != null) {
			switch(stockMarketTypeEnum) {
				case Market_Hk://港股
					list = redisDao.getBeanList(RedisKeyPrefix.getHkStockQuoteListKey(), StockData.class);
					break;
				case Market_Us://美股
					list = redisDao.getBeanList(RedisKeyPrefix.getUsStockQuoteListKey(), StockData.class);
					break;	
				default:
					list = redisDao.getBeanList(RedisKeyPrefix.getAStockQuoteListKey(), StockData.class);
					break;
			}
		}else {
			list.addAll(redisDao.getBeanList(RedisKeyPrefix.getHkStockQuoteListKey(), StockData.class));
			list.addAll(redisDao.getBeanList(RedisKeyPrefix.getUsStockQuoteListKey(), StockData.class));
			list.addAll(redisDao.getBeanList(RedisKeyPrefix.getAStockQuoteListKey(), StockData.class));
		}
		// 2、排序
		if (!StringUtil.isEmpty(sortTerm)) {
			Comparator<? super StockData> c = null;
			switch (sortTerm) {
			case "nowPrice":// 最新价
				c = Comparator.comparing(StockData::getNowPrice);// 最新价-升序;
				if (sortType.equals("desc")) {
					c = Comparator.comparing(StockData::getNowPrice).reversed();// 最新价-降序;
					// list.sort(Comparator.comparing(StockData::getNowPrice));//最新价-升序;
					// list.sort(Comparator.comparing(StockData::getNowPrice).reversed());//最新价-降序;
				}
				break;
			case "percentageIncrease":// 涨幅比例
				c = Comparator.comparing(StockData::getPercentageIncrease);// 涨幅比例-升序;
				if (sortType.equals("desc")) {
					c = Comparator.comparing(StockData::getPercentageIncrease).reversed();// 涨幅比例-降序;
					// list.sort(Comparator.comparing(StockData::getPercentageIncrease));//涨幅比例-升序;
					// list.sort(Comparator.comparing(StockData::getPercentageIncrease).reversed());//涨幅比例-降序;
				}
				break;
			case "turnover":// 成交量
				// c = Comparator.comparing(StockData::getTurnover);//成交量-升序;
				if (sortType.equals("desc")) {
					c = Comparator.comparing(StockData::getTurnover).reversed();// 成交量-降序;
					// list.sort(Comparator.comparing(StockData::getTurnover));//成交量-升序;
					// list.sort(Comparator.comparing(StockData::getTurnover).reversed());//成交量-降序;
				}
				break;
			default:
				break;
			}
			list.sort(c);
		}
		// 3、封装数据返回结果
		if (pageSize > 100) {
			pageSize = 100;
		}
		Page<StockData> page = new Page<>(pageNo, pageSize);
		if (list == null || list.size() == 0) {
			return page;
		}
		int total = list.size();
		page.setTotal(total);
		int offset = (pageNo - 1) * pageSize;
		int limit = pageNo * pageSize;
		if (limit > total) {
			if (total > offset) {
				page.setRecords(new ArrayList<>(list.subList(offset, total)));
			}
		} else {
			page.setRecords(new ArrayList<>(list.subList(offset, limit)));
		}
		return page;
	}

	/**
	 * 股票信息
	 */
	@Override
	public Response<StockDetailVO> getStockDetail(Integer userId, String stockCode, String stockType) {
		try {
			StockInfo si = this.lambdaQuery().eq(StockInfo::getStockCode, stockCode).eq(StockInfo::getStockType, stockType).one();
			StockQuotesVO q;
			String am_begin, am_end, pm_begin, pm_end;
			StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
			switch(stockType) {
			case "us":
				am_begin = stockParamConfig.getMarketUs_amTradingStart();
	        	am_end = stockParamConfig.getMarketUs_amTradingEnd();
	        	pm_begin = stockParamConfig.getMarketUs_pmTradingStart();
	        	pm_end = stockParamConfig.getMarketUs_pmTradingEnd();
	        	q = EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode);
	        	break;
			case "hk":
				am_begin = stockParamConfig.getMarketHk_amTradingStart();
	        	am_end = stockParamConfig.getMarketHk_amTradingEnd();
	        	pm_begin = stockParamConfig.getMarketHk_pmTradingStart();
	        	pm_end = stockParamConfig.getMarketHk_pmTradingEnd();
	        	q = EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode);
				break;
			default:
				am_begin = stockParamConfig.getMarketA_amTradingStart();
	        	am_end = stockParamConfig.getMarketA_amTradingEnd();
	        	pm_begin = stockParamConfig.getMarketA_pmTradingStart();
	        	pm_end = stockParamConfig.getMarketA_pmTradingEnd();
				q = SinaApi.getSinaStock(stockType, stockCode);
				break;
			}
			if(q.getGid() == null) {
				return Response.fail("产品信息错误,userI:"+ userId+"stockCode:"+ stockCode+"stockType:"+ stockType);
			}
			//1、通过股票类型，股票代码，搜索股票，获取market
			EastMoneyStockSearchReturn eastMoneyStockSearchReturn = EastMoneyApiAnalysis.getEastMoneyStockSearch(stockType, stockCode);			
			//数据库无股票信息数据，则增加一条数据
			if(si == null) {
				si = new StockInfo();
				si.setStockCode(stockCode);
				si.setStockName(q.getStockName());
				si.setStockType(stockType);
				if(stockCode.startsWith("688") && stockCode.equals("sh")) {
					si.setStockPlate("科创");
				} else if(stockCode.equals("sz") && stockCode.startsWith("30")) {
					si.setStockPlate("创业");
				}
				si.setIsShow(true);
				si.setIsLock(false);
				si.setCreator("系统");
				if (eastMoneyStockSearchReturn != null && eastMoneyStockSearchReturn.getMarket() != null) {
					si.setMarket(eastMoneyStockSearchReturn.getMarket());
				}
				si.insert();
//				StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
//				stockDataChangeRecord.setStockId(si.getId());
//				stockDataChangeRecord.setStockCode(si.getStockCode());
//				stockDataChangeRecord.setStockName(si.getStockName());
//				stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.ADD_STOCK.getCode());
//				stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.ADD_STOCK.getName());
//				stockDataChangeRecord.setNewContent(si.toString());
//				stockDataChangeRecord.setIp("127.0.0.1");
//				stockDataChangeRecord.setIpAddress("服务器本机ip");
//				stockDataChangeRecord.setOperator("系统");
//				stockDataChangeRecord.insert();
			}else {
				//有股票信息数据，查看market是否缺失，缺失就修改market
				if (si.getMarket() == null) {
					if (eastMoneyStockSearchReturn != null && eastMoneyStockSearchReturn.getMarket() != null) {
						StockInfo updateMarket = new StockInfo();
						updateMarket.setId(si.getId());
						updateMarket.setMarket(eastMoneyStockSearchReturn.getMarket());
						updateMarket.updateById();
					}
				}
			}
//			if(!si.getIsShow()) {
//				return Response.fail("该产品暂时无法显示");
//			}
			StockDetailVO d = new StockDetailVO();
			d.setStockName(si.getStockName());
			d.setStockCode(stockCode);
			d.setStockType(stockType);
			d.setStockPlate(si.getStockPlate());
//			d.setIsLock(si.getIsLock());
			d.setPercentageIncrease(q.getPercentageIncrease());
			d.setNowPrice(q.getNowPrice());
			d.setHightPrice(q.getHeightPrice());
			d.setLowPrice(q.getLowPrice());
			d.setOpenPrice(q.getOpenPrice());
			d.setVolume(q.getVolume());
			d.setTurnover(q.getTurnover());
			d.setPrevClose(q.getPrevClose());
			if(userId > 0) {
				int count = userFavoriteStockService.lambdaQuery()
					.eq(UserFavoriteStock::getUserId, userId)
					.eq(UserFavoriteStock::getStockCode, stockCode)
					.eq(UserFavoriteStock::getStockType, stockType).count();
				if(count > 0) {
					d.setIsFavorite(true);
				}
			}
			int status = BuyAndSellUtils.getTradingStatus(am_begin, am_end, pm_begin, pm_end, stockType,true);
			d.setMarketStatus(status);
			if(d.getNowPrice() != null && d.getPrevClose() != null && d.getNowPrice() != BigDecimal.ZERO && d.getPrevClose() != BigDecimal.ZERO) {
				d.setIncrease(d.getNowPrice().subtract(d.getPrevClose()));
			}
			//实时获取一手的股票数
			d.setSharesOfHand(EastMoneyApiAnalysis.getSharesOfHand(stockType, stockCode));
			//增加热门搜索数据
			SiteHotStock siteHotStock = siteHotStockService.lambdaQuery().
			eq(SiteHotStock::getStockCode, d.getStockCode()).
			eq(SiteHotStock::getStockType, d.getStockType()).
			one();
			SiteHotStock siteHotStockNew = new SiteHotStock();
			if (siteHotStock != null) {
				siteHotStockNew.setId(siteHotStock.getId());
				siteHotStockNew.setSearchTimes(siteHotStock.getSearchTimes()+1) ;
			}else {
				siteHotStockNew.setStockName(d.getStockName());
				siteHotStockNew.setStockCode(d.getStockCode());
				siteHotStockNew.setStockType(d.getStockType());
				siteHotStockNew.setStockPlate(d.getStockPlate());
				siteHotStockNew.setSearchTimes(1);
			}
			siteHotStockNew.insertOrUpdate();
			return Response.successData(d);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.fail("产品信息错误,userI:"+ userId+"stockCode:"+ stockCode+"stockType:"+ stockType);
		}
	}

	/**
	 * 获取股票rieds(分时成交记录)
	 */
	public StockDayTransactionVO getStockDayTransactionReids(String stockCode, String stockType) {
		StockDayTransactionVO stockDayTransactionVO = redisDao.getBean(RedisKeyPrefix.getStockDayTransactionKey(stockCode,stockType), StockDayTransactionVO.class);
		if (stockDayTransactionVO == null) {
			stockDayTransactionVO = EastMoneyApiAnalysis.getStockDayTransaction(stockType, stockCode);
			if (stockDayTransactionVO != null) {
				redisDao.setBean(RedisKeyPrefix.getStockDayTransactionKey(stockCode,stockType), stockDayTransactionVO,5,TimeUnit.MINUTES);//5分钟销毁
			}
		}
		return stockDayTransactionVO;
	}

	/**
	 * 跟投信号-跟投信号详情-获取股票(分时成交记录)
	 */
	@Override
	public StockDayTransactionVO getStockDayTransaction(StockDayTransactionParamVO param) {
		if (param.getStockCode().isEmpty() || param.getStockType().isEmpty()) {
			return null;
		}
		if (param.getDisplayReleaseTime() != null) {
			// 判断displayReleaseTime是否大于当前时间
		    if (param.getDisplayReleaseTime().after(new Date())) {
		    	return null;
		    }
		}
		StockDayTransactionVO stockDayTransactionVO = getStockDayTransactionReids(param.getStockCode(),param.getStockType());
		if (stockDayTransactionVO != null) {
			//获取分页数据
			stockDayTransactionVO.setDayTransactionData(stockDayTransactionVO.getDayTransactionData(param));
			StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
			int stockMarketTypeCode = StockMarketTypeEnum.Market_A.getCode();//默认：A股code
			BigDecimal buyingFeeRate = stockParamConfig.getMarketABuyingFeeRate();//默认：A股买入手续费比例
			BigDecimal buyingStampDutyRate = stockParamConfig.getMarketAStampDutyRate();//默认：A股印花税比例
			CurrencyEnum currencyEnum = CurrencyEnum.CNY;//默认人名币
			StockTypeEnum stockTypeEnum = StockTypeEnum.getByCode(param.getStockType());
			switch(stockTypeEnum) {
			case US:
				stockMarketTypeCode = StockMarketTypeEnum.Market_Us.getCode();//美股code
				buyingFeeRate = stockParamConfig.getMarketUsBuyingFeeRate();//美股买入手续费比例
				buyingStampDutyRate = BigDecimal.ZERO;//美股无印花税
				currencyEnum = CurrencyEnum.USD;//美元
	        	break;
			case HK:
				stockMarketTypeCode = StockMarketTypeEnum.Market_Hk.getCode();//港股code
				buyingFeeRate = stockParamConfig.getMarketHkBuyingFeeRate();//港股买入手续费比例
				buyingStampDutyRate = stockParamConfig.getMarketHkStampDutyRate();//港股印花税比例
				currencyEnum = CurrencyEnum.HKD;//港币
				break;
			}
			//获取市场状态
			stockDayTransactionVO.setMarketStatus(sysParamConfigService.getMarketTradingStatus(stockMarketTypeCode));
			//手续费
			stockDayTransactionVO.setBuyingFeeRate(buyingFeeRate);
			//印花税
			stockDayTransactionVO.setBuyingStampDutyRate(buyingStampDutyRate);
			//获取汇率
			stockDayTransactionVO.setExchangeRate(sysParamConfigService.getExchangeRate(currencyEnum));
			return stockDayTransactionVO;
		}
		
		return null;
	}
	
//	/**
//	 * 股票数据初始化
//	 */
//	@Override
//	public Response<String> stockDataInitialize() {
//		String aCount = stockDataInitializeBystockType(null);
//		String usCount = stockDataInitializeBystockType(StockTypeEnum.US.getCode());
//		String hkCount = stockDataInitializeBystockType(StockTypeEnum.HK.getCode());
//		return Response.success("A股-"+aCount+"/n美股-"+usCount+"/n港股-"+hkCount);
//	}
//	
//	/**
//	 * 执行东财API，根据股票类型，获取股票数据【（暂时弃用）访问频繁，会导致连接超时的问题】
//	 * @param stockType 股票类型，可选择为（sh：沪股、sz：深股、bj：北证、us：美股、hk：港股），为空则处理所有A股
//	 */
//	private String stockDataInitializeBystockType(String stockType) {
//		List<String> stockTypeIn = new ArrayList<String>();
//		if (stockType != null) {
//			stockTypeIn.add(stockType);
//		}else {//查所有A股数据
//			stockTypeIn.add(StockTypeEnum.SH.getCode());
//			stockTypeIn.add(StockTypeEnum.SZ.getCode());
//			stockTypeIn.add(StockTypeEnum.BJ.getCode());
//		}
//		int stockInfoCount = this.lambdaQuery().in(StockInfo::getStockType, stockTypeIn).count();
//		if (stockInfoCount == 0) {
//			List<StockInfo> stockInfoList = (List<StockInfo>) EastMoneyApiAnalysis.getStockList(stockType,StockInfo.class);//执行东财API，根据股票类型-获取-股票列表信息【暂时弃用，因请求频繁，存在访问连接超时问题】
//			//List<StockInfo> stockInfoList = NowStockApi.getFinanceStockList(stockType);//执行NOWApi，根据股票类型-获取-股票列表信息
//			int processedCount = 0;
//			for (StockInfo stockInfo : stockInfoList) {
//				if (StringUtil.isNotEmpty(stockInfo.getStockType()) && 
//						StringUtil.isNotEmpty(stockInfo.getStockCode()) && 
//						StringUtil.isNotEmpty(stockInfo.getStockName())) {
//					if(stockInfo.getStockCode().startsWith("688") && stockInfo.getStockCode().equals("sh")) {
//						stockInfo.setStockPlate("科创");
//					} else if(stockInfo.getStockCode().equals("sz") && stockInfo.getStockCode().startsWith("30")) {
//						stockInfo.setStockPlate("创业");
//					}
//					stockInfo.setIsShow(true);
//					stockInfo.setIsLock(false);
//					stockInfo.setCreator("系统-初始化");
//					stockInfo.insert();
//					StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
//					stockDataChangeRecord.setStockId(stockInfo.getId());
//					stockDataChangeRecord.setStockCode(stockInfo.getStockCode());
//					stockDataChangeRecord.setStockName(stockInfo.getStockName());
//					stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.ADD_STOCK.getCode());
//					stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.ADD_STOCK.getName());
//					stockDataChangeRecord.setNewContent(stockInfo.toString());
//					stockDataChangeRecord.setIp("127.0.0.1");
//					stockDataChangeRecord.setIpAddress("服务器本机ip");
//					stockDataChangeRecord.setOperator("系统-初始化");
//					stockDataChangeRecord.insert();
//					processedCount++;
//				}
//			}
//			return "总数："+stockInfoList.size()+",处理成功："+processedCount;
//		}
//		return "当前已有数据，无需初始化";
//	}

}
