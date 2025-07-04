package service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.date.DateUtil;
import entity.CashinRecord;
import entity.CashoutRecord;
import entity.SignInfo;
import entity.TutorInfo;
import entity.TutorInfo.Configuration;
import entity.UserFollowUpPending;
import entity.UserFollowUpPosition;
import entity.UserFollowUpRecord;
import entity.UserFollowUpTrading;
import entity.UserInfo;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import enums.SignTypeEnum;
import enums.StockMarketTypeEnum;
import enums.StockTypeEnum;
import enums.UserFollowPendingSignTraceStatusEnum;
import enums.UserFollowPendingStatusEnum;
import enums.UserFollowStatusEnum;
import io.netty.util.internal.ThreadLocalRandom;
import mapper.UserFollowUpPendingMapper;
import mapper.UserFollowUpTradingMapper;
import service.CashinRecordService;
import service.CashoutRecordService;
import service.IpAddressService;
import service.SignInfoService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.TutorInfoService;
import service.UserFollowUpPendingService;
import service.UserFollowUpPositionService;
import service.UserFollowUpRecordService;
import service.UserFollowUpTradingService;
import service.UserInfoService;
import utils.EastMoneyApiAnalysis;
import utils.OrderNumberGenerator;
import utils.SinaApi;
import utils.StringUtil;
import vo.common.StockDayTransactionParamVO;
import vo.common.StockDayTransactionVO;
import vo.common.TradeCalculationResultVO;
import vo.manager.FollowPendingListSearchParamVO;
import vo.manager.FollowPendingTransferDetailUserVO;
import vo.manager.FollowPendingTransferDetailVO;
import vo.manager.FollowPendingTransferFinishParamVO;
import vo.server.UserFollowPendingParamVO;
import vo.server.UserFollowPendingVO;

/**
 * <p>
 * 用户跟投委托表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-13
 */
@Service
public class UserFollowUpPendingServiceImpl extends ServiceImpl<UserFollowUpPendingMapper, UserFollowUpPending> implements UserFollowUpPendingService {

	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserFollowUpRecordService userFollowUpRecordService;
	
	@Resource
	private CashinRecordService cashinRecordService;
	
	@Resource
	private CashoutRecordService cashoutRecordService;
	
	@Resource
	private SignInfoService signInfoService;
	
	@Resource
	private UserFollowUpPositionService userFollowUpPositionService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserFollowUpTradingService userFollowUpTradingService;
	
	@Resource
	private UserFollowUpTradingMapper userFollowUpTradingMapper;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private TutorInfoService tutorInfoService;
	
	/**
	 * 跟投-导师信息-跟单详情-委托单记录
	 */
	@Override
	public void userFollowPending(Page<UserFollowPendingVO> page, UserFollowPendingParamVO param) {
		this.baseMapper.userFollowPending(page,param);
	}

	/**
	 * 跟投管理-用户跟投委托
	 */
	@Override
	public void managerList(Page<UserFollowUpPending> page, FollowPendingListSearchParamVO param) {
		this.baseMapper.managerList(page,param);
	}

	/**
	 * 委托订单-待转完成-详情
	 */
	@Override
	public Response<FollowPendingTransferDetailVO> transferDetail(List<Integer> pendingIdList, Integer signId,BigDecimal buyingPrice,Boolean marketPrice) {
		FollowPendingTransferDetailVO followPendingTransferDetailVO = new FollowPendingTransferDetailVO();
		//1、查询信号信息
		SignInfo signInfo = signInfoService.getById(signId);
		//1.1、信号显示时间验证
		if (signInfo == null || signInfo.getDisplayReleaseTime().after(new Date())) {
			return Response.fail("当前委托单的，信号显示时间，超过当前时间");
		}
		//1.2、赋值信号信息
		followPendingTransferDetailVO.setSignInfo(signInfo);
		//2、处理-用户及跟投相关List信息
		List<FollowPendingTransferDetailUserVO> followPendingTransferDetailUserVOList = new ArrayList<FollowPendingTransferDetailUserVO>();
		for (Integer pendingId : pendingIdList) {//循环处理多个委托单
			UserFollowUpPending userFollowUpPending = this.getById(pendingId);//查询委托单
			if (userFollowUpPending != null && userFollowUpPending.getSignId().equals(signId)) {
				if (userFollowUpPending.getPendingStatus() == UserFollowPendingStatusEnum.WAIT_COMPLETE.getCode()) {
					UserInfo userInfo = userInfoService.getById(userFollowUpPending.getUserId());
					if (userInfo != null) {
						FollowPendingTransferDetailUserVO followPendingTransferDetailUserVO = new FollowPendingTransferDetailUserVO();
						//2.1、设置用户相关属性，userInfo对象相同属性，赋值进followPendingTransferDetailUserVO中
						BeanUtils.copyProperties(userInfo, followPendingTransferDetailUserVO);
						UserFollowUpRecord userFollowUpRecord = userFollowUpRecordService.getById(userFollowUpPending.getFollowRecordId());
						if (userFollowUpRecord != null) {
							followPendingTransferDetailUserVO.setPendingId(pendingId);
							//2.2、设置，跟投记录相关属性
							followPendingTransferDetailUserVO.setInitialFollowSum(userFollowUpRecord.getInitialFollowSum());
							followPendingTransferDetailUserVO.setCurrentFollowSum(userFollowUpRecord.getCurrentFollowSum());
							followPendingTransferDetailUserVO.setFollowStartTime(userFollowUpRecord.getFollowStartTime());
							followPendingTransferDetailUserVO.setFollowEndTime(userFollowUpRecord.getFollowEndTime());
							followPendingTransferDetailUserVO.setRemainingDays(userFollowUpRecord.getRemainingDays());
							followPendingTransferDetailUserVO.setFollowStatus(userFollowUpRecord.getFollowStatus());
							//2.3、累计转入金额
							QueryWrapper<CashinRecord> cqw = new QueryWrapper<>();
							cqw.eq("order_status", 1);
							cqw.eq("user_id", userFollowUpPending.getUserId());
							cqw.select("ifnull(sum(final_amount),0) as final_amount");
							Map<String, Object> map = cashinRecordService.getMap(cqw);
							followPendingTransferDetailUserVO.setTotalCashinSum((BigDecimal)map.get("final_amount"));
							//2.4、累计转出金额
							QueryWrapper<CashoutRecord> dqw = new QueryWrapper<>();
							dqw.eq("order_status", 1);
							dqw.eq("user_id", userFollowUpPending.getUserId());
							dqw.select("ifnull(sum(final_amount),0) as finalAmount");
							map = cashoutRecordService.getMap(dqw);
							followPendingTransferDetailUserVO.setTotalCashoutSum((BigDecimal)map.get("finalAmount"));
							//2.5、累计初始-跟投资金
							QueryWrapper<UserFollowUpRecord> ufur = new QueryWrapper<>();
							ufur.eq("id", userFollowUpPending.getFollowRecordId());
							ufur.select("ifnull(sum(initial_follow_sum),0) as initialFollowSum");
							map = userFollowUpRecordService.getMap(ufur);
							followPendingTransferDetailUserVO.setTotalInitialFollowSum((BigDecimal)map.get("initialFollowSum"));
							//2.6、根据跟投id，计算总市值
							followPendingTransferDetailUserVO.setPositionFollowSum(userFollowUpRecordService.getPositionFollowSum(userFollowUpPending.getFollowRecordId()));
							
							//2.7、设置最后完成时间
							followPendingTransferDetailUserVO.setFinallyFinishTime(signInfo.getDisplayReleaseTime());//最后完成时间，默认取信号显示时间
							UserFollowUpTrading userFollowUpTrading = userFollowUpTradingService.lambdaQuery()
									.eq(UserFollowUpTrading :: getUserId, userFollowUpPending.getUserId())
									.eq(UserFollowUpTrading :: getFollowRecordId, userFollowUpPending.getFollowRecordId())
									.apply("finish_time = (SELECT MAX(finish_time) FROM user_follow_up_trading)")
									.last("LIMIT 1")
									.one();//通过信号id查询，最大一条完成时间数据
							//比对信号显示时间，与信号开始时间进行比对
							if (userFollowUpTrading != null && userFollowUpTrading.getFinishTime() != null) {
								//信号显示时间，晚于，最大的完成时间
								if (userFollowUpTrading.getFinishTime().after(signInfo.getDisplayReleaseTime())) {
									//最后完成时间，取最大完成时间
									followPendingTransferDetailUserVO.setFinallyFinishTime(userFollowUpTrading.getFinishTime());
					            }
							}
							//2.8、设置分时成交-随机-价格和随机完成时间
							StockDayTransactionParamVO stockDayTransactionParamVO = new StockDayTransactionParamVO();
							stockDayTransactionParamVO.setPageNo(0);
							stockDayTransactionParamVO.setPageSize(0);
							stockDayTransactionParamVO.setStockCode(signInfo.getStockCode());
							stockDayTransactionParamVO.setStockType(signInfo.getStockType());
							stockDayTransactionParamVO.setDisplayReleaseTime(followPendingTransferDetailUserVO.getFinallyFinishTime());
							stockDayTransactionParamVO.setBuyingPrice(buyingPrice);
							stockDayTransactionParamVO.setMarketPrice(marketPrice);
							StockDayTransactionVO stockDayTransactionVO = stockInfoService.getStockDayTransaction(stockDayTransactionParamVO);
							if (stockDayTransactionVO != null 
									&& stockDayTransactionVO.getDayTransactionData() != null 
									&& stockDayTransactionVO.getDayTransactionData().getRandomStockDayTransactionDataPage() != null) {
								//2.8.1、设置分时成交-随机-价格
								String price = stockDayTransactionVO.getDayTransactionData().getRandomStockDayTransactionDataPage().getPrice(); 
								followPendingTransferDetailUserVO.setRandomStockDayTransactionBuyingPrice(new BigDecimal(price));
								//2.8.2、设置分时成交-随机-成交时间
								String tradeDay = stockDayTransactionVO.getDayTransactionData().getRandomStockDayTransactionDataPage().getTradeDay();
								String tradeTime = stockDayTransactionVO.getDayTransactionData().getRandomStockDayTransactionDataPage().getTradeTime();
								// 组合日期和时间
						        String dateTimeStr = tradeDay + " " + tradeTime;  // 格式："yyyy-MM-dd HH:mm:ss"
						        Date randomStockDayTransactionFinishTime = DateUtil.parse(dateTimeStr);
								followPendingTransferDetailUserVO.setRandomStockDayTransactionFinishTime(randomStockDayTransactionFinishTime);
							}
							//2.9、将followPendingTransferDetailUserVO添加进followPendingTransferDetailUserVOList中
							followPendingTransferDetailUserVOList.add(followPendingTransferDetailUserVO);
						}else {
							return Response.fail("当前委托单的，跟投记录不存在，委托id："+pendingId);
						}
					}else {
						return Response.fail("当前委托单的，用户不存在，委托id："+pendingId);
					}
				}else {
					return Response.fail("当前委托单的,委托状态必须是-待完成，委托id："+pendingId);
				}
			}else {
				return Response.fail("当前委托单的，信号id不匹配，委托id："+pendingId);
			}
		}
		//3、赋值-用户及跟投相关List信息
		followPendingTransferDetailVO.setFollowPendingTransferDetailUserVO(followPendingTransferDetailUserVOList);
		return Response.successData(followPendingTransferDetailVO);
	}
	
	/**
	 * 委托订单-完成操作
	 */
	@Override
	public Response<Void> transferFinish(FollowPendingTransferFinishParamVO param, String ip, String operator) {
		// 1. 参数验证
		if (param == null) {
			return Response.fail("委托订单-完成操作param请求参数必填");
		}
		if (param.getFollowPendingTransferDetailUserVOList() == null) {
			return Response.fail("followPendingTransferDetailUserVOList参数必填");
		}
		if (param.getSignId() == null) {
			return Response.fail("signId参数必填");
		}
		if (param.getTradingPosition() == null) {
			return Response.fail("tradingPosition参数必填");
		}
		
		//2、查询信号信息
		SignInfo signInfo = signInfoService.getById(param.getSignId());
		//2.1、 信号信息验证
		if (signInfo == null || !signInfo.getIsConfigured() 
	            || signInfo.getPosition() == null 
	            || signInfo.getStockCode() == null || signInfo.getStockName() == null || signInfo.getStockType() == null) {//验证-信号，以及信号状态，信号仓位，信号价格区间,信号股票三要素
	        return Response.fail("信号信息有误");
	    }
		//2.2、验证-交易仓位
		if (param.getTradingPosition().compareTo(signInfo.getPosition()) > 0) {
			return Response.fail("交易仓位不能大于信号仓位");
		}
		
		//3、获取系统配置
		StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
		
		try {
			//4、循环-处理多个委托单id
			for (FollowPendingTransferDetailUserVO followPendingTransferDetailUserVO : param.getFollowPendingTransferDetailUserVOList()) {
				//4.1、处理单个委托单
		        Response<Void> processResponse = doTransferFinishProcessPendingOrder(
		        		followPendingTransferDetailUserVO.getPendingId(), 
		        		signInfo,stockParamConfig, 
		        		followPendingTransferDetailUserVO.getRandomStockDayTransactionBuyingPrice(),
		        		followPendingTransferDetailUserVO.getRandomStockDayTransactionFinishTime(), 
		        		param.getTradingPosition(),
		        		ip,
		        		operator);	
		        // 判断code判断是否成功
		        if (!Response.SUCCESS.equals(processResponse.getCode())) {
		        	// 回滚前检查事务是否存在
	                if (TransactionSynchronizationManager.isActualTransactionActive()) {
	                	// 强制事务回滚
	                	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	                }
	                return Response.fail("委托单处理失败: " + processResponse.getMsg());
		            //return processResponse; // 处理失败立即返回
		        }
			}
			return Response.success();
		} catch (Exception e) {
			// 回滚前检查事务是否存在
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
            	// 强制事务回滚
            	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
	        return Response.fail("交易失败: " + e.getMessage());
		}
	}

	/**
	 * 委托订单-完成操作-处理单个委托单
	 * @param pendingId
	 * @param signInfo
	 * @param buyingPrice
	 * @param finishTime
	 * @param tradingPosition
	 * @param ip
	 * @param operator
	 * @return
	 * @throws Exception 
	 */
	@Transactional
	private Response<Void> doTransferFinishProcessPendingOrder(Integer pendingId, SignInfo signInfo,StockParamConfig stockParamConfig,
			BigDecimal buyingPrice, Date finishTime, BigDecimal tradingPosition, String ip, String operator) throws Exception{
		
		// ============== 1. 校验并获取相关实体 ==============
	    Response<OrderValidationResult> validationResponse = validatePendingOrderAndFetchEntities(
	        pendingId, signInfo, buyingPrice, finishTime
	    );
	    // 如果校验失败直接返回错误
	    if (!Response.SUCCESS.equals(validationResponse.getCode())) {
	        return Response.fail(validationResponse.getMsg());
	    }
	    // 从校验结果中获取实体对象
	    OrderValidationResult result = validationResponse.getData();
	    UserFollowUpPending userFollowUpPending = result.userFollowUpPending;
	    UserFollowUpRecord userFollowUpRecord = result.userFollowUpRecord;
	    UserFollowUpPosition userFollowUpPosition = result.userFollowUpPosition;
	    UserInfo userInfo = result.userInfo;
	    TutorInfo tutorInfo = result.tutorInfo;
	    Configuration configuration = result.configuration;
		
	    // ============== 2. 初始化计算变量 ==============
	    // 2.1 基础变量初始化
		Integer buyingShares = 0;//默认操作股数0
		BigDecimal operateSum = BigDecimal.ZERO;//操作总金额
		BigDecimal operateFee = BigDecimal.ZERO;//操作手续费
		BigDecimal operateStampDuty = BigDecimal.ZERO;//操作印花税
		BigDecimal tutorCommission = BigDecimal.ZERO;//导师抽佣
		BigDecimal platformCommission = BigDecimal.ZERO;//平台抽佣
		Integer sharesOfHand = signInfo.getSharesOfHand();//一手的股数
		
		BigDecimal initialFollowSum = userFollowUpRecord.getInitialFollowSum();//初始-跟投资金
		
		BigDecimal operateFollowSum = BigDecimal.ZERO;//操作跟投金额
		BigDecimal positionValueRatio = BigDecimal.ZERO;//操作仓位数值比
		
		BigDecimal beCurrentFollowSum = userFollowUpRecord.getCurrentFollowSum();//变更前-当前-跟投资金=默认为当前跟投资金
		BigDecimal afCurrentFollowSum = BigDecimal.ZERO;//变更后-当前-跟投资金=默认0
		
		BigDecimal beCurrentPositionValueRatio = userFollowUpPosition.getCurrentPositionValueRatio();//变更前-当前-仓位数值比 = 默认为当前-仓位数值比
		BigDecimal afCurrentPositionValueRatio = BigDecimal.ZERO;//变更后-当前-仓位数值比=默认0
		
		BigDecimal beCurrentFollowPositionValueRatio = userFollowUpRecord.getCurrentFollowPositionValueRatio();//变更前-当前-跟投总仓位数值比=默认为当前-跟投总仓位数值比
		BigDecimal afCurrentFollowPositionValueRatio = BigDecimal.ZERO;//变更后-当前-跟投总仓位数值比=默认0
		
		// 2.2 费率相关变量
		BigDecimal exchangeRate = BigDecimal.ONE;//汇率
		BigDecimal buyingFeeRate = BigDecimal.ZERO;//手续费率
		BigDecimal buyingStampDutyRate = BigDecimal.ZERO;//印花税率
		BigDecimal tutorCommissionRatio = configuration.getTutorCommissionRatio();//导师抽佣比例
		BigDecimal platformCommissionRatio = configuration.getPlatformCommissionRatio();//平台抽佣比例
		CurrencyEnum currency;//币种
		 
		// ============== 3. 确定股票类型相关费率 ==============
		//根据股票类型获取（手续费率、印花税率、币种）
		StockTypeEnum stockTypeEnum = StockTypeEnum.getByCode(signInfo.getStockType());
		switch(stockTypeEnum) {
		case US://美股
			if (signInfo.getSignType() == SignTypeEnum.BUY.getCode()) {
				buyingFeeRate = stockParamConfig.getMarketUsBuyingFeeRate();//美股买入手续费比例
			}else {
				buyingFeeRate = stockParamConfig.getMarketUsSellingFeeRate();//美股卖出手续费比例
			}
	    	currency = CurrencyEnum.USD;//美元
        	break;
		case HK://港股
			if (signInfo.getSignType() == SignTypeEnum.BUY.getCode()) {
				buyingFeeRate = stockParamConfig.getMarketHkBuyingFeeRate();//港股买入手续费比例
			}else {
				buyingFeeRate = stockParamConfig.getMarketHkSellingFeeRate();//港股卖出手续费比例
			}
	    	buyingStampDutyRate = stockParamConfig.getMarketHkStampDutyRate();//港股印花税比例
	    	currency = CurrencyEnum.HKD;//港币
			break;
		default://A股
			if (signInfo.getSignType() == SignTypeEnum.BUY.getCode()) {
				buyingFeeRate = stockParamConfig.getMarketABuyingFeeRate();//A股买入手续费比例
			}else {
				buyingFeeRate = stockParamConfig.getMarketASellingFeeRate();//A股卖出手续费比例
			}
	    	buyingStampDutyRate = stockParamConfig.getMarketAStampDutyRate();//A股印花税比例
	    	currency = CurrencyEnum.CNY;//人名币
	    	break;
		}
		
		// ============== 4. 汇率转换和价格处理 ==============
		//根据币种获取汇率
		exchangeRate = this.sysParamConfigService.getExchangeRate(currency);
		//根据汇率，计算购买单价（转换成人名币的价格）=价格/汇率
		buyingPrice = buyingPrice.divide(exchangeRate, 2, RoundingMode.HALF_UP);
		
		// ============== 5. 计算操作股数 ==============
		if (signInfo.getSignType() == SignTypeEnum.BUY.getCode()) {
			//如果仓位大于0，操作股数=（用户可用金额/（买入价*汇率*（1+手续费比例+印花税比例）））*仓位
			if(tradingPosition.compareTo(BigDecimal.ZERO) == 1) {
				// 计算总费率因子 (1 + 手续费率 + 印花税率)
				BigDecimal totalRateFactor = BigDecimal.ONE
				        .add(buyingFeeRate)
				        .add(buyingStampDutyRate);

				// 计算单位成本：买入价 * 总费率因子
				BigDecimal unitCost = buyingPrice
				        .multiply(totalRateFactor);

				// 核心计算：buyingShares = floor((beCurrentFollowSum / unitCost) * tradingPosition)
				buyingShares = beCurrentFollowSum
				        .divide(unitCost, 10, RoundingMode.HALF_UP) // 保留10位小数避免精度丢失
				        .multiply(tradingPosition)
				        .setScale(0, RoundingMode.DOWN) // 向下取整到整数股
				        .intValueExact(); // 转换为Integer
				//操作股数 < 一手股数
				if (buyingShares < sharesOfHand) {
					return Response.fail("当前委托记录，当前跟投自己，买股数，小于一手的股数，"
							+ "委托id："+pendingId+",当前跟投金额:"+beCurrentFollowSum+",可买股数："+buyingShares+",一手股数："+sharesOfHand);
				}
			}
		}else {//卖出
			//操作股数=仓位*持有股数
			buyingShares = tradingPosition
			        .multiply(BigDecimal.valueOf(userFollowUpPosition.getHoldShares())) // 转换为BigDecimal后相乘
			        .setScale(0, RoundingMode.HALF_UP)        // 四舍五入取整
			        .intValueExact();
		}
		
		// ============== 6. 计算交易费用 ==============
		//操作总金额 = 操作金额*操作股数
		operateSum = buyingPrice.multiply(BigDecimal.valueOf(buyingShares));
		//操作手续费 = 当次操作总金额*手续费率
		operateFee = operateSum.multiply(buyingFeeRate);
		//操作印花税 = 当次操作总金额*印花税率
		operateStampDuty = operateSum.multiply(buyingStampDutyRate);
		
		if (signInfo.getSignType() == SignTypeEnum.BUY.getCode()) {//买入
			//操作跟投金额 = 操作总额+手续费+印花税
			operateFollowSum = (operateSum.add(operateFee)).add(operateStampDuty);
			//变更后当前跟投资金 = 变更前当前跟投资金 - 操作跟投金额
			afCurrentFollowSum = beCurrentFollowSum.subtract(operateFollowSum);
			
			//操作仓位数值比 = 操作跟投金额/初始跟投资金
			positionValueRatio = operateFollowSum.divide(initialFollowSum, 10, RoundingMode.HALF_UP); // 保留10位小数避免精度丢失
			
			//变更后-当前-仓位数值比 = 变更前当前仓位数值比 + 操作仓位数值比
			afCurrentPositionValueRatio = beCurrentPositionValueRatio.add(positionValueRatio);
			
			//变更后-当前-跟投总仓位数值比 = 变更前当前跟投总仓位数值比 + 操作仓位数值比
			afCurrentFollowPositionValueRatio = beCurrentFollowPositionValueRatio.add(positionValueRatio);
			
		}else {//卖出
			//操作跟投金额 = 操作总额-手续费-印花税-导师抽佣-平台抽佣
			operateFollowSum = (((operateSum.subtract(operateFee)).subtract(operateStampDuty)).subtract(tutorCommission)).subtract(platformCommission);
			//变更后当前跟投资金 = 变更前当前跟投资金 + 操作跟投金额
			afCurrentFollowSum = beCurrentFollowSum.add(operateFollowSum);
			
			//操作仓位数值比 = 仓位*变更前当前仓位数值比
			positionValueRatio = tradingPosition.multiply(beCurrentPositionValueRatio).setScale(10, RoundingMode.HALF_UP) ;// 保留10位小数避免精度丢失
			
			//变更后-当前-仓位数值比 = 变更前当前仓位数值比 - 操作仓位数值比
			afCurrentPositionValueRatio = beCurrentPositionValueRatio.subtract(positionValueRatio);
			
			//变更后-当前-跟投总仓位数值比 = 变更前当前跟投总仓位数值比 - 操作仓位数值比
			afCurrentFollowPositionValueRatio = beCurrentFollowPositionValueRatio.subtract(positionValueRatio);
		}
		//如果，变更后-当前-跟投资金小于0，是负数，将变更后-当前-跟投资金设置成0
		if(afCurrentFollowSum.compareTo(BigDecimal.ZERO) == -1) {
			afCurrentFollowSum = BigDecimal.ZERO;
		}
		//如果，变更后-当前-跟投总仓位数值比小于0，是负数，将变更后-当前-跟投总仓位数值比设置成0
		if(afCurrentFollowPositionValueRatio.compareTo(BigDecimal.ZERO) == -1) {
			afCurrentFollowPositionValueRatio = BigDecimal.ZERO;
		}
		
		// ============== 7. 核心业务流程【此流程，不可更改（流程不可改变，处理持仓→处理委托单→处理跟投记录（当前跟投金额）→添加交易记录→处理跟投记录（退费改状态））】 ==============
		//7.1、修改委托单数据，改为以完成
		doTransferFinishUserFollowUpPendingStatus(userFollowUpPending.getId());
		
		//7.2、处理持仓数据
		Integer holdShares = userFollowUpPosition.getHoldShares();//持有股数
		if (signInfo.getSignType() == SignTypeEnum.BUY.getCode()) {//买入
			//合并持仓
			holdShares = holdShares + buyingShares;//持有股数 = 持有股数 + 操作股数
			
			/*****因为流程是先处理持仓，在增加交易记录，所以，默认总额累计总额，和默认累计买入股数，为当次操作的，值******/
			BigDecimal cumulativeBuyingSum = operateSum;//累计买入金额-默认当次，操作总金额
			BigDecimal cumulativeBuyingFee = operateFee;//累计买入手续费-默认当次，操作手续费
			BigDecimal cumulativebuyingStampDuty = operateStampDuty;//累计买入印花税-默认当次，操作印花税
			Integer cumulativeBuyingShares = buyingShares;//累计买入股数-默认当次，操作股数
			//通过持仓id，只查买入，交易记录（买入,类型在信号上）
			List<UserFollowUpTrading> userFollowUpTradingBuyPositionList = userFollowUpTradingMapper.userFollowUpTradingPositionList(userFollowUpPosition.getId(),0);
			for(UserFollowUpTrading userFollowUpTradingBuyPosition : userFollowUpTradingBuyPositionList) {
				//买入金额=买入价格*买入股数
				BigDecimal buyingSum = userFollowUpTradingBuyPosition.getBuyingPrice()
				.multiply(BigDecimal.valueOf(userFollowUpTradingBuyPosition.getBuyingShares()))// 转换为BigDecimal后相乘
				.setScale(3,RoundingMode.HALF_UP);// 设置精度和舍入模式
				//累计买入金-累加
				cumulativeBuyingSum = cumulativeBuyingSum.add(buyingSum);
				//累计买入手续费-累加
				cumulativeBuyingFee = cumulativeBuyingFee.add(userFollowUpTradingBuyPosition.getBuyingFee());
				//累计买入印花税-累加
				cumulativebuyingStampDuty = cumulativebuyingStampDuty.add(userFollowUpTradingBuyPosition.getBuyingStampDuty());
				//累计买入股数-累加
				cumulativeBuyingShares = cumulativeBuyingShares + userFollowUpTradingBuyPosition.getBuyingShares();
			}
			//累计总额=累计买入金额+累计手续费+累计印花税
			BigDecimal cumulativeTotalAmount = (cumulativeBuyingSum.add(cumulativeBuyingFee)).add(cumulativebuyingStampDuty);
			//合并成本价 = 累计总额/累计买入股数
			BigDecimal buyingCostPrice = cumulativeTotalAmount.divide(
		            new BigDecimal(cumulativeBuyingShares), // 转换为BigDecimal
		            3,                                      // 小数位数
		            RoundingMode.HALF_UP);                  // 四舍五入模式
			userFollowUpPosition.setBuyingCostPrice(buyingCostPrice);//更改-成本价
			userFollowUpPosition.setBuyingPriceTotal(cumulativeTotalAmount);//累计买入金-累加
			userFollowUpPosition.setBuyingSharesTotal(cumulativeBuyingShares);//累计买入股数-累加
		}else {//卖出
			//持有股数=持有股数-操作股数
			holdShares = holdShares - buyingShares;
			// 1. 计算当次卖出操作的盈亏总额=((操作金额 - 持仓成本) * 操作股数) - 手续费 - 印花税
			BigDecimal buyingCostPrice = userFollowUpPosition.getBuyingCostPrice();//持仓成本
			//盈亏差价=（操作金额-持仓成本）
	        BigDecimal priceDifference = buyingPrice.subtract(buyingCostPrice);
	        //盈亏额=（盈亏差价*操作股数）
	        BigDecimal grossProfit = priceDifference.multiply(new BigDecimal(buyingShares));
	        //盈亏总额 =（盈亏金额-手续费-印花税）
	        BigDecimal netProfit = grossProfit.subtract(operateFee).subtract(operateStampDuty);
	        // 2. 仅当盈利时计算抽佣
	        if (netProfit.compareTo(BigDecimal.ZERO) > 0) {//盈亏总额大于0即为盈利
	            // 导师抽佣 = 盈利总额 × 导师比例（四舍五入保留2位小数）
	            tutorCommission = netProfit.multiply(tutorCommissionRatio)
	                                      .setScale(2, RoundingMode.HALF_UP);
	            // 平台抽佣 = 盈利总额 × 平台比例（四舍五入保留2位小数）
	            platformCommission = netProfit.multiply(platformCommissionRatio)
	                                         .setScale(2, RoundingMode.HALF_UP);
	        }
	        //通过持仓id，只查卖出，交易记录（卖出,类型在信号上）
	        List<UserFollowUpTrading> userFollowUpTradingSalePositionList = userFollowUpTradingMapper.userFollowUpTradingPositionList(userFollowUpPosition.getId(),1);
			BigDecimal cumulativeSaleSum = operateSum;//累计卖出金额-默认当次，操作总金额
			BigDecimal cumulativeSaleFee = operateFee;//累计卖出手续费-默认当次，操作手续费
			BigDecimal cumulativeSaleStampDuty = operateStampDuty;//累计卖出印花税-默认当次，操作印花税
			for(UserFollowUpTrading userFollowUpTradingSalePosition : userFollowUpTradingSalePositionList) {
				//卖出金额=卖出价格*卖出股数
				BigDecimal saleSum = userFollowUpTradingSalePosition.getBuyingPrice()
				.multiply(BigDecimal.valueOf(userFollowUpTradingSalePosition.getBuyingShares()))// 转换为BigDecimal后相乘
				.setScale(3,RoundingMode.HALF_UP);// 设置精度和舍入模式
				//累计买卖出金-累加
				cumulativeSaleSum = cumulativeSaleSum.add(saleSum);
				//累计买入手续费-累加
				cumulativeSaleFee = cumulativeSaleFee.add(userFollowUpTradingSalePosition.getBuyingFee());
				//累计买入印花税-累加
				cumulativeSaleStampDuty = cumulativeSaleStampDuty.add(userFollowUpTradingSalePosition.getBuyingStampDuty());
			}
			//累计卖出总额=累计卖出金额-累计卖出手续费-累计卖出印花税
			BigDecimal salePriceTotal = (cumulativeSaleSum.subtract(cumulativeSaleFee)).subtract(cumulativeSaleStampDuty);
	        userFollowUpPosition.setSalePriceTotal(salePriceTotal);//卖出-总金额(每次卖出操作累加)
		}
		userFollowUpPosition.setHoldShares(holdShares);//更改-持有股数
		if(holdShares == 0) {//持有股数为0时改为平仓状态
			userFollowUpPosition.setPositionStatus(1);//更改-（持仓状态为-平仓）
		}
		userFollowUpPosition.setCurrentPositionValueRatio(afCurrentPositionValueRatio);//更改-（当前-仓位数值比）
		//更改持仓数据
		userFollowUpPosition.updateById();
		
		//7.3、处理跟投记录（当前跟投金额）
		UserFollowUpRecord updateCurrentFollowSum = new UserFollowUpRecord();
		updateCurrentFollowSum.setId(userFollowUpPending.getFollowRecordId());
		updateCurrentFollowSum.setCurrentFollowSum(afCurrentFollowSum);
		updateCurrentFollowSum.setCurrentFollowPositionValueRatio(afCurrentFollowPositionValueRatio);
		//修改变更后-当前-跟投资金
		updateCurrentFollowSum.updateById();
		
		//7.4、增加交易记录
		UserFollowUpTrading userFollowUpTrading = new UserFollowUpTrading();
		userFollowUpTrading.setUserId(userFollowUpPending.getUserId());
		userFollowUpTrading.setTutorId(userFollowUpPending.getTutorId());
		userFollowUpTrading.setSignId(userFollowUpPending.getSignId());
		userFollowUpTrading.setFollowRecordId(userFollowUpPending.getFollowRecordId());
		if (userFollowUpPosition != null) {
			userFollowUpTrading.setPositionId(userFollowUpPosition.getId());
		}
		userFollowUpTrading.setPendingId(userFollowUpPending.getId());
		String no = OrderNumberGenerator.create(8);
		userFollowUpTrading.setTradingOrderSn(no);
		userFollowUpTrading.setTradingTime(new Date());
		userFollowUpTrading.setTradingPosition(tradingPosition);
		userFollowUpTrading.setBuyingPrice(buyingPrice);
		userFollowUpTrading.setBuyingShares(buyingShares);
		userFollowUpTrading.setBuyingFee(operateFee);
		userFollowUpTrading.setBuyingFeeRate(buyingFeeRate);
		userFollowUpTrading.setBuyingStampDuty(operateStampDuty);
		userFollowUpTrading.setBuyingStampDutyRate(buyingStampDutyRate);
		userFollowUpTrading.setExchangeRate(exchangeRate);
		userFollowUpTrading.setTutorCommission(tutorCommission);
		userFollowUpTrading.setTutorCommissionRatio(tutorCommissionRatio);
		userFollowUpTrading.setPlatformCommission(platformCommission);
		userFollowUpTrading.setPlatformCommissionRatio(platformCommissionRatio);
		userFollowUpTrading.setBeCurrentFollowSum(beCurrentFollowSum);
		userFollowUpTrading.setAfCurrentFollowSum(afCurrentFollowSum);
		userFollowUpTrading.setFinishTime(finishTime);
		// 生成4个有序时间点
		Date[] timeline = StringUtil.generateOrderedTimeline(signInfo.getDisplayReleaseTime(),finishTime,4);
		userFollowUpTrading.setFlowTime1(timeline[0]);
		userFollowUpTrading.setFlowTime2(timeline[1]);
		userFollowUpTrading.setFlowTime3(timeline[2]);
		userFollowUpTrading.setFlowTime4(timeline[3]);
		userFollowUpTrading.setOperator(operator);
		userFollowUpTrading.setSignType(signInfo.getSignType());
		userFollowUpTrading.setBeHoldShares(userFollowUpPosition.getHoldShares());
		userFollowUpTrading.setBeCurrentPositionValueRatio(beCurrentPositionValueRatio);
		userFollowUpTrading.setAfCurrentPositionValueRatio(afCurrentPositionValueRatio);
		userFollowUpTrading.setBeCurrentFollowPositionValueRatio(beCurrentFollowPositionValueRatio);
		userFollowUpTrading.setAfCurrentFollowPositionValueRatio(afCurrentFollowPositionValueRatio);
		userFollowUpTrading.insert();
		
		//7.5、跟据交易添加返佣明细
		userInfoService.addUserRebateDetailOnTransaction(userInfo, operateFollowSum, 0, no);
		
		//7.6、卖出-处理跟投记录结束操作（退费改状态）
		if (signInfo.getSignType() == SignTypeEnum.SALE.getCode()) {
			doTransferFinishUserFollowUpRecordOver(userFollowUpPending.getFollowRecordId(),ip,operator);
		}	
		return Response.success();
	}
	
	/**
	 * 校验委托单并获取关联实体
	 */
	private Response<OrderValidationResult> validatePendingOrderAndFetchEntities(
	        Integer pendingId, SignInfo signInfo, BigDecimal buyingPrice, Date finishTime) {
	    
	    // 参数基础校验
	    if (buyingPrice == null || buyingPrice.compareTo(BigDecimal.ZERO) <= 0) {
	        return Response.fail("当前委托单的，随机价格参数必须大于0，委托id：" + pendingId);
	    }
	    if (finishTime == null) {
	        return Response.fail("当前委托单的，随机完成时间为空，委托id：" + pendingId);
	    }
	    
	    // 查询委托单信息
	    UserFollowUpPending userFollowUpPending = this.getById(pendingId);
	    if (userFollowUpPending == null || !userFollowUpPending.getSignId().equals(signInfo.getId())) {
	        return Response.fail("当前委托单的，信号id不匹配，委托id：" + pendingId);
	    }
	    if (userFollowUpPending.getPendingStatus() != UserFollowPendingStatusEnum.WAIT_COMPLETE.getCode()) {
	        return Response.fail("当前委托单的,委托状态必须是-待完成，委托id：" + pendingId);
	    }
	    
	    // 查询跟投信息
	    UserFollowUpRecord userFollowUpRecord = userFollowUpRecordService.getById(userFollowUpPending.getFollowRecordId());
	    if (userFollowUpRecord == null || userFollowUpRecord.getFollowStatus() == UserFollowStatusEnum.FINISH.getCode()) {
	        return Response.fail("当前委托单的,跟投信息已结束，委托id：" + pendingId);
	    }
	    
	    // 查询持仓数据
	    if (userFollowUpPending.getPositionId() == null) {
	        return Response.fail("当前委托单的,跟投信息持仓id为空，此为旧的异常数据，新流程中，配置信号时已经存入了持仓id，委托id：" + pendingId);
	    }
	    UserFollowUpPosition userFollowUpPosition = userFollowUpPositionService.getById(userFollowUpPending.getPositionId());
	    if (userFollowUpPosition == null || userFollowUpPosition.getPositionStatus() != 0) {
	        return Response.fail("当前委托单的,无仓，或已平仓，委托id：" + pendingId);
	    }
	    if (signInfo.getSignType() == SignTypeEnum.SALE.getCode() && userFollowUpPosition.getHoldShares() <= 0) {
	        return Response.fail("当前委托单的,持仓数是0,不允许卖出，委托id：" + pendingId);
	    }
	    
	    // 查询用户信息
	    UserInfo userInfo = userInfoService.getById(userFollowUpPending.getUserId());
	    if (userInfo == null) {
	        return Response.fail("当前委托单的,用户不存在，委托id：" + pendingId);
	    }
	    
	    //查导师信息
	    TutorInfo tutorInfo = tutorInfoService.getById(signInfo.getTutorId());
		if (tutorInfo == null) {
			return Response.fail("当前委托单的,导师不存在，委托id：" + pendingId);
		}
		
		Configuration configuration = tutorInfo.getConfigurationByItemType(signInfo.getItemType());
		if (configuration == null) {
			return Response.fail("当前委托单的,导师项目不存在，委托id：" + pendingId);
		}
	    
	    // 返回校验通过的结果
	    OrderValidationResult result = new OrderValidationResult();
	    result.userFollowUpPending = userFollowUpPending;
	    result.userFollowUpRecord = userFollowUpRecord;
	    result.userFollowUpPosition = userFollowUpPosition;
	    result.userInfo = userInfo;
	    result.tutorInfo = tutorInfo;
	    result.configuration = configuration;
	    return Response.successData(result);
	}

	/**
	 * 校验结果封装类
	 */
	private static class OrderValidationResult {
	    UserFollowUpPending userFollowUpPending;
	    UserFollowUpRecord userFollowUpRecord;
	    UserFollowUpPosition userFollowUpPosition;
	    UserInfo userInfo;
	    TutorInfo tutorInfo;
	    Configuration configuration;
	}

	/**
	 * 委托订单-完成操作-参数基础验证
	 * @param tradingPosition
	 * @param buyingPrice
	 * @param finishTime
	 * @param signId
	 * @return
	 */
	@Transactional
	private Response<Void> doTransferFinishValidateParameters(BigDecimal tradingPosition, BigDecimal buyingPrice, Date finishTime,
			Integer signId) {
		if (tradingPosition == null) {
			return Response.fail("tradingPosition参数必填");
		}
		if (buyingPrice == null || buyingPrice.compareTo(BigDecimal.ZERO) <= 0) {
			return Response.fail("buyingPrice参数必须大于0");
		}
		if (finishTime == null) {
			return Response.fail("finishTime参数必填");
		}
		return null;
	}

	/**
	 * 委托订单-完成操作-修改委托单数据，改为以完成
	 * @param pendingId
	 * @param positionId
	 */
	@Transactional
	private void doTransferFinishUserFollowUpPendingStatus(Integer pendingId) {
		UserFollowUpPending userFollowUpPendingUpdate = new UserFollowUpPending();
		userFollowUpPendingUpdate.setId(pendingId);
		userFollowUpPendingUpdate.setPendingStatus(UserFollowPendingStatusEnum.PROCESSED_COMPLETE.getCode());//已完成
		userFollowUpPendingUpdate.setOperationTime(new Date());
		userFollowUpPendingUpdate.updateById();
	}

	/**
	 * 委托订单-完成操作-处理跟投记录结束操作（退费改状态）
	 * @param signInfo
	 * @param userFollowUpPending
	 */
	@Transactional
	private void doTransferFinishUserFollowUpRecordOver(Integer followRecordId,String ip, String operator) {
		//2、根据当前委托单的跟投记录id，查询，不含已完成，的总数量
		Integer followPendingCount = this.lambdaQuery()
			.eq(UserFollowUpPending::getFollowRecordId, followRecordId)
			.eq(UserFollowUpPending::getSignTraceStatus, UserFollowPendingSignTraceStatusEnum.FOLLOW_UP.getCode())
			.ne(UserFollowUpPending::getPendingStatus, UserFollowPendingStatusEnum.PROCESSED_COMPLETE.getCode())
			.count();
		//2.1、如果followPendingCount为0代表，当前跟投记录id的委托单，已全部处理完，可以进行下一步
		if (followPendingCount == 0) {
			//3、根据当前委托单的跟投记录id，查询，不含已平仓，的总数量
			Integer followPositionCount = userFollowUpPositionService.lambdaQuery()
					.eq(UserFollowUpPosition::getFollowRecordId,  followRecordId)
					.ne(UserFollowUpPosition::getPositionStatus, 1)
					.count();
			//3.1、如果followPositionCount为0代表，当前跟投记录id的持仓，已全部处理完，可以进行下一步
			if (followPositionCount == 0) {
				//4、根据当前委托单的跟投记录id，查询最新的跟投记录
				UserFollowUpRecord userFollowUpRecordNew = userFollowUpRecordService.getById(followRecordId);
				//4.1、当前最新的跟投记录，状态不是已结束，可进行下一步处理
				if (userFollowUpRecordNew != null && userFollowUpRecordNew.getFollowStatus() != UserFollowStatusEnum.FINISH.getCode()) {
					//5、当前最新的跟投记录，结束-跟投时间 小于当前系统时间，表示到期，可以进行下一步
					if (DateUtil.compare(userFollowUpRecordNew.getFollowEndTime(), new Date()) < 0) {
						//6、根据当前委托单的跟投记录id，查询，总导师佣金、总平台抽佣
						QueryWrapper<UserFollowUpTrading> qw = new QueryWrapper<>();
						qw.eq("follow_record_id", followRecordId);
						qw.select("ifnull(sum(tutor_commission),0) as tutorCommissionTotal, ifnull(sum(platform_commission),0) as platformCommissionTotal");
						Map<String, Object> map = userFollowUpTradingService.getMap(qw);
						//6.1、总导师佣金
						BigDecimal tutorCommissionTotal = (BigDecimal) map.get("tutorCommissionTotal");
						//6.2、总平台抽佣
						BigDecimal platformCommissionTotal = (BigDecimal) map.get("platformCommissionTotal");
						//6.3、退还跟投资金=当前跟投资金-总导师抽佣-总平台抽佣
						BigDecimal returnFollowSum = (userFollowUpRecordNew.getCurrentFollowSum().subtract(tutorCommissionTotal)).subtract(platformCommissionTotal);						
						//7、修改跟投记录为已完结，并处理，费用数据
						UserFollowUpRecord userFollowUpRecordOver = new UserFollowUpRecord();
						userFollowUpRecordOver.setId(userFollowUpRecordNew.getId());
						userFollowUpRecordOver.setFollowStatus(UserFollowStatusEnum.FINISH.getCode());//设置为已完结
						userFollowUpRecordOver.setFollowClosingTime(new Date());
						userFollowUpRecordOver.setCurrentFollowSum(BigDecimal.ZERO);
						userFollowUpRecordOver.setReturnFollowSum(returnFollowSum);
						userFollowUpRecordOver.setTutorCommission(tutorCommissionTotal);
						userFollowUpRecordOver.setPlatformCommission(platformCommissionTotal);
						userFollowUpRecordOver.updateById();
						//8、退款金额，加回用户费用中
						userInfoService.updateUserAvailableAmt(userFollowUpRecordNew.getUserId(), 
							AmtDeTypeEnum.FinishFollowRecord, returnFollowSum,
							"后台，操作结束跟投记录",
									CurrencyEnum.CNY,
									BigDecimal.ONE,
									ip,
									ipAddressService.getIpAddress(ip).getAddress2(),
									operator,userFollowUpRecordNew.getFollowOrderSn());
						//9、扣除跟投资金费用
						userInfoService.updateUserAvailableAmt(userFollowUpRecordNew.getUserId(), 
								AmtDeTypeEnum.FinishFollowRecord_reduce_follow_amt, userFollowUpRecordNew.getInitialFollowSum(),
								"后台，操作结束跟投记录",
										CurrencyEnum.CNY,
										BigDecimal.ONE,
										ip,
										ipAddressService.getIpAddress(ip).getAddress2(),
										operator,userFollowUpRecordNew.getFollowOrderSn());
					}
				}
			}
		}
	}

    /**
     * 委托订单-撤单操作
     */
	@Override
	public Response<Void> revoke(List<Integer> pendingIdList,String ip,String operator) {
		for(Integer pendingId : pendingIdList) {
			UserFollowUpPending userFollowUpPending = this.getById(pendingId);
			if(userFollowUpPending != null) {
				if (userFollowUpPending.getSignTraceStatus() == UserFollowPendingSignTraceStatusEnum.FOLLOW_UP.getCode() 
						&& userFollowUpPending.getPendingStatus() == UserFollowPendingStatusEnum.WAIT_COMPLETE.getCode()) {
					//1、修改信号跟踪状态为，信号-未跟上
					userFollowUpPending.setSignTraceStatus(UserFollowPendingSignTraceStatusEnum.NOT_FOLLOW.getCode());
					userFollowUpPending.updateById();
					//2、处理跟投记录结束操作（退费改状态）
					doTransferFinishUserFollowUpRecordOver(userFollowUpPending.getFollowRecordId(),ip,operator);
				}else {
					return Response.fail("当前委托单,必须是，信号-已跟上，并且，待成交");
				}
			}else {
				return Response.fail("当前委托单的不存在");
			}
		}
		
		return Response.success();
	}


}
