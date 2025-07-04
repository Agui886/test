package service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.SignInfo;
import entity.UserFollowUpPending;
import entity.UserFollowUpPosition;
import entity.UserFollowUpRecord;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.CurrencyEnum;
import enums.SignTypeEnum;
import enums.StockMarketTypeEnum;
import enums.StockTypeEnum;
import enums.UserFollowPendingSignTraceStatusEnum;
import enums.UserFollowPendingStatusEnum;
import enums.UserFollowStatusEnum;
import mapper.SignInfoMapper;
import service.SignInfoService;
import service.SysParamConfigService;
import service.TutorInfoService;
import service.UserFollowUpPendingService;
import service.UserFollowUpPositionService;
import service.UserFollowUpRecordService;
import utils.StringUtil;
import vo.manager.FollowPendingListSearchParamVO;
import vo.manager.SignListSearchParamVO;

/**
 * <p>
 * 跟投信号表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Service
public class SignInfoServiceImpl extends ServiceImpl<SignInfoMapper, SignInfo> implements SignInfoService {

	@Resource
	private SignInfoMapper signInfoMapper;
	
	@Resource
	private TutorInfoService tutorInfoService;
	
	@Resource
	UserFollowUpRecordService userFollowUpRecordService;
	
	@Resource
	UserFollowUpPendingService userFollowUpPendingService;
	
	@Resource
	UserFollowUpPositionService userFollowUpPositionService;
	
	@Resource
	SysParamConfigService sysParamConfigService;
	
	/**
	 * 跟投管理-跟投信号
	 */
	@Override
	public void managerList(Page<SignInfo> page, SignListSearchParamVO param) {
		signInfoMapper.managerList(page, param);
	}

	/**
	 * 跟投信号-发布信号
	 */
	@Override
	@Transactional
	public Response<Void> release(SignInfo signInfo, String publisher) {
		if(signInfo.getTutorId() == null || signInfo.getTutorId() < 1) {
			return Response.fail("请选择导师");
		}
		if(tutorInfoService.getById(signInfo.getTutorId()) == null) {
			return Response.fail("导师不存在");
		}
		if(signInfo.getItemType() == null) {
			return Response.fail("请选择项目");
		}
		if(signInfo.getItemType() < 0 || signInfo.getItemType() > 6) {
			return Response.fail("项目参数错误");
		}
		if(signInfo.getDisplayReleaseTime() == null) {
			return Response.fail("请选择显示发布时间");
		}
		if(signInfo.getSignType() == null) {
			return Response.fail("signType参数必填");
		}
		if (signInfo.getSignType() == SignTypeEnum.BUY.getCode()) {//买入
			signInfo.setBuySignId(null);
		}else if (signInfo.getSignType() == SignTypeEnum.SALE.getCode()) {//卖出
			if (signInfo.getBuySignId() == null || signInfo.getBuySignId() == 0) {
				return Response.fail("卖出操作，buySignId参数必填");
			}
			SignInfo buySignInfo = this.lambdaQuery().eq(SignInfo :: getId, signInfo.getBuySignId()).one();
			if (buySignInfo != null) {
				if(!signInfo.getDisplayReleaseTime().after(buySignInfo.getDisplayReleaseTime())) {
					return Response.fail("卖出时间，必须在买入时间之后");
				}
			}else {
				return Response.fail("卖出操作，所对应的买入信号不存在");
			}
		}else{
			return Response.fail("signType参数不匹配");
		}
		//1、添加跟投信号数据
		signInfo.setId(null);
		signInfo.setRealReleaseTime(new Date());
		signInfo.setPublisher(publisher);
		signInfo.setIsConfigured(false);
		signInfo.insert();
		//2、根据信号类型，处理委托单业务逻辑
		addFollowPending(signInfo);
		return Response.success();
	}

	/**
	 * 根据信号类型，处理委托单业务逻辑
	 * @param signInfo
	 */
	@Transactional
	private void addFollowPending(SignInfo signInfo) {
		//1、创建修改信号跟单人数对象
		SignInfo signInfoUpdate = new SignInfo();
		signInfoUpdate.setId(signInfo.getId());
		//2、根据信号类型，判断执行，买入/卖出，业务流程
		if (signInfo.getSignType() == SignTypeEnum.BUY.getCode()) {//买入
			//2.1、获取，当前导师，当前项目的，跟投记录，不包含已结束的记录
			List<UserFollowUpRecord> userFollowUpRecordList = userFollowUpRecordService.lambdaQuery()
					.eq(UserFollowUpRecord::getTutorId, signInfo.getTutorId())//导师
					.eq(UserFollowUpRecord::getItemType, signInfo.getItemType())//项目
					.ne(UserFollowUpRecord::getFollowStatus, UserFollowStatusEnum.FINISH.getCode())//不包含（已结束）
					.lt(UserFollowUpRecord::getFollowStartTime, signInfo.getDisplayReleaseTime())//小于显示时间
					.list();
			//2.2、根据跟投记录，批量添加委托单（买入代转持仓）
			for(UserFollowUpRecord userFollowUpRecord:userFollowUpRecordList){
				UserFollowUpPending userFollowUpPending = new UserFollowUpPending();
				userFollowUpPending.setUserId(userFollowUpRecord.getUserId());
				userFollowUpPending.setTutorId(userFollowUpRecord.getTutorId());
				userFollowUpPending.setSignId(signInfo.getId());
				userFollowUpPending.setFollowRecordId(userFollowUpRecord.getId());
				userFollowUpPending.setPendingTime(new Date());
				userFollowUpPending.setPendingStatus(UserFollowPendingStatusEnum.WAIT_COMPLETE.getCode());
				userFollowUpPending.setSignTraceStatus(UserFollowPendingSignTraceStatusEnum.UNDERWAY.getCode());
				userFollowUpPending.insert();
			}
			//2.3、设置修改信号，买入跟单人数
			signInfoUpdate.setFollowers(userFollowUpRecordList.size());
		}else {//卖出
			//3.1、获取，买入委托单
			List<UserFollowUpPending> userFollowUpPendingList = userFollowUpPendingService.lambdaQuery()
					.eq(UserFollowUpPending :: getSignId, signInfo.getBuySignId())//买入信号id
					.list();
			//3.2、根据，买入委托单，批量添加委托单（卖出待成交）
			for(UserFollowUpPending userFollowUpPendingBuy:userFollowUpPendingList){
				UserFollowUpPending userFollowUpPending = new UserFollowUpPending();
				userFollowUpPending.setUserId(userFollowUpPendingBuy.getUserId());
				userFollowUpPending.setTutorId(userFollowUpPendingBuy.getTutorId());
				userFollowUpPending.setSignId(signInfo.getId());
				userFollowUpPending.setFollowRecordId(userFollowUpPendingBuy.getFollowRecordId());
				userFollowUpPending.setPendingTime(new Date());
				userFollowUpPending.setPendingStatus(UserFollowPendingStatusEnum.WAIT_COMPLETE.getCode());
				userFollowUpPending.setSignTraceStatus(UserFollowPendingSignTraceStatusEnum.UNDERWAY.getCode());
				userFollowUpPending.insert();
			}
			//3.3、设置修改信号，卖出跟单人数
			signInfoUpdate.setFollowers(userFollowUpPendingList.size());
		}
		//4、修改跟投人数
		signInfoUpdate.updateById();
	}

	/**
	 * 跟投信号-配置信号
	 */
	@Override
	@Transactional
	public Response<Void> configure(SignInfo signInfo) {
		//1、验证参数
		if(signInfo.getId() == null || signInfo.getId() == 0) {
			return Response.fail("id主键必填");
		}
			
		if(signInfo.getPosition() == null) {
			return Response.fail("请选择仓位");
		}
		
		if(signInfo.getPosition().compareTo(BigDecimal.ZERO) < 1 || signInfo.getPosition().compareTo(BigDecimal.ONE) > 0) {
			return Response.fail("请选择正确的仓位");
		}
		
		if(signInfo.getPrice1() == null || signInfo.getPrice1().compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("请输入指导价");
		}
		
		if(signInfo.getPrice2() == null || signInfo.getPrice2().compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("请输入一手的指导价");
		}
		
		if(signInfo.getSharesOfHand() == null || signInfo.getSharesOfHand() < 1) {
			return Response.fail("sharesOfHand为必填");
		}
		//2、通过id获取当前信号信息
		SignInfo signInfoBygetId = this.getById(signInfo.getId());
		if(signInfoBygetId == null) {
			return Response.fail("信号信息不存在");
		}
		//2.1、通过卖出信号的-买入id，查买入信号
		SignInfo signBuyInfo = this.getById(signInfoBygetId.getBuySignId());
		
		//3、买入/卖出-处理逻辑
		if (signInfoBygetId.getSignType() == SignTypeEnum.BUY.getCode()) {//买入
			//3.1、买入，股票三要素必填
			if(StringUtil.isEmpty(signInfo.getStockName())) {
				return Response.fail("stockName股票名称必填");
			}
			if(StringUtil.isEmpty(signInfo.getStockCode())) {
				return Response.fail("stockCode股票代码必填");
			}
			if(StringUtil.isEmpty(signInfo.getStockType())) {
				return Response.fail("stockType股票类型必填");
			}
		}else if(signInfoBygetId.getSignType() == SignTypeEnum.SALE.getCode()) {//卖出
			if (signBuyInfo == null) {
				return Response.fail("当前卖出信号，对应的买入信号信息不存在");
			}
			//3.2.1、买入信号已配置
			if (signBuyInfo.getIsConfigured()) {
				//买入信息号，股票三要素已配置
				if(StringUtil.isNotEmpty(signBuyInfo.getStockName()) 
						&& StringUtil.isNotEmpty(signBuyInfo.getStockCode()) 
						&& StringUtil.isNotEmpty(signBuyInfo.getStockType())) {
					signInfo.setStockName(signBuyInfo.getStockName());
					signInfo.setStockCode(signBuyInfo.getStockCode());
					signInfo.setStockType(signBuyInfo.getStockType());
				}else {
					return Response.fail("当前卖出信号，对应的买入信号信息,股票三要素，配置不全");
				}
			}else {
				return Response.fail("当前卖出信号，对应的买入信号信息未配置");
			}
		}
		//4、根据信号id，查询跟投记录，处理跟上，未跟上状态
		List<UserFollowUpPending> userFollowUpPendingList = userFollowUpPendingService.lambdaQuery()
				.eq(UserFollowUpPending :: getSignId, signInfo.getId())
				.list();
		//跟上人数，默认0
		Integer followerUps = 0;
		
		//根据股票类型获取（手续费率、印花税率、币种）
		StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
		BigDecimal buyingFeeRate = stockParamConfig.getMarketABuyingFeeRate();//默认：A股买入手续费比例
		BigDecimal buyingStampDutyRate = stockParamConfig.getMarketAStampDutyRate();//默认：A股印花税比例
		CurrencyEnum currencyEnum = CurrencyEnum.CNY;//默认人名币
		StockTypeEnum stockTypeEnum = StockTypeEnum.getByCode(signInfo.getStockType());
		switch(stockTypeEnum) {
		case US:
			buyingFeeRate = stockParamConfig.getMarketUsBuyingFeeRate();//美股买入手续费比例
			buyingStampDutyRate = BigDecimal.ZERO;//美股无印花税
			currencyEnum = CurrencyEnum.USD;//美元
        	break;
		case HK:
			buyingFeeRate = stockParamConfig.getMarketHkBuyingFeeRate();//港股买入手续费比例
			buyingStampDutyRate = stockParamConfig.getMarketHkStampDutyRate();//港股印花税比例
			currencyEnum = CurrencyEnum.HKD;//港币
			break;
		}
		//根据币种获取汇率
		BigDecimal exchangeRate = sysParamConfigService.getExchangeRate(currencyEnum);
		//一手人名币价格 = （指导价/汇率）*一手股数
		BigDecimal buyPrice = (signInfo.getPrice1().divide(exchangeRate, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(signInfo.getSharesOfHand()));
		//一手手续续费 = 一手人名币价格*手续费率
		BigDecimal buyingFee = buyPrice.multiply(buyingFeeRate);
		//一手印花税费 = 一手人名币价格*印花税率
		BigDecimal buyingStampDuty = buyPrice.multiply(buyingStampDutyRate);
		//一手购买总费用 = 一手人名币价格 + 一手印花税费 + 一手印花税费
		BigDecimal totalCost = buyPrice.add(buyingFee).add(buyingStampDuty);
		//4.1、循环处理，跟投记录，变更，信号跟随状态
		for(UserFollowUpPending userFollowUpPending : userFollowUpPendingList) {
			//持仓id默认为空
			UserFollowUpPosition userFollowUpPosition = null;
			//跟上状态，默认，未跟上
			boolean falg = false;
			//跟投状态，不是结束状态，才允许计算，跟上人数，判断是否跟上状态
			UserFollowUpRecord userFollowUpRecord = userFollowUpRecordService.getById(userFollowUpPending.getFollowRecordId());
			if (userFollowUpRecord != null && userFollowUpRecord.getFollowStatus() != UserFollowStatusEnum.FINISH.getCode()) {
				if (signInfoBygetId.getSignType() == SignTypeEnum.BUY.getCode()) {//买入
					//初始化持仓
					 userFollowUpPosition = userFollowUpPositionService.getInitUserPositionMerge(
							userFollowUpPending.getUserId(),
							userFollowUpPending.getTutorId(),
							userFollowUpPending.getFollowRecordId(),
							signInfo.getStockName(),
							signInfo.getStockCode(),
							signInfo.getStockType());
					//一手购买总费用 <= 当前跟投资金
					if(totalCost.compareTo(userFollowUpRecord.getCurrentFollowSum()) <= 0) {
						falg = true;
						followerUps++;
					}
				}else if(signInfoBygetId.getSignType() == SignTypeEnum.SALE.getCode()) {//卖出
					//根据卖出委托单的用户查，卖出对应的买入委托单，一个用户，买入信号，肯定只会有一条买入委托单，并且有持仓id，如果查出多，证明数据有错误
					UserFollowUpPending userFollowUpPendingBuy = userFollowUpPendingService.lambdaQuery()
							.eq(UserFollowUpPending :: getUserId, userFollowUpPending.getUserId())//卖出委托单-用户id
							.eq(UserFollowUpPending :: getSignId, signBuyInfo.getId())//卖出信号-对应的买入信号id
							.one();
					//如果没查到，证明数据有错误
					if (userFollowUpPendingBuy != null && userFollowUpPendingBuy.getPositionId() != null) {
						//通过买入委托单的，持仓id查
						userFollowUpPosition = userFollowUpPositionService.getById(userFollowUpPendingBuy.getPositionId());
						if (userFollowUpPosition.getHoldShares() > 0) {//当前持有股数>0
							falg = true;
							followerUps++;
						}
					}
				}
			}
			//处理跟上状态
			if (falg) {
				userFollowUpPending.setSignTraceStatus(UserFollowPendingSignTraceStatusEnum.FOLLOW_UP.getCode());//跟上
			}else {
				userFollowUpPending.setSignTraceStatus(UserFollowPendingSignTraceStatusEnum.NOT_FOLLOW.getCode());//未跟上
			}
			//设置持仓id
			if (userFollowUpPosition != null) {
				userFollowUpPending.setPositionId(userFollowUpPosition.getId());
			}
			userFollowUpPending.updateById();
		}
		//5、修改信息号信息
		signInfo.setFollowerUps(followerUps);//跟上人数
		signInfo.setIsConfigured(true);
		signInfo.updateById();
		return Response.success();
	}

}
