package com.f.stock.schema;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import entity.TutorInfo;
import entity.TutorInfoChangeRecord;
import entity.TutorInfo.Configuration;
import enums.TutorInfoChangeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import mapper.UserFollowUpPendingMapper;
import service.TutorInfoService;
import utils.StringUtil;

@Component
@Slf4j
public class TutorTask {

	@Resource
	private TutorInfoService tutorInfoService;
	
	@Resource
	private UserFollowUpPendingMapper userFollowUpPendingMapper;
	

	/**
	 * 生成导师项目中的,上月数据统计-每月凌晨3点执行一次
	 * 
	 * @throws UnknownHostException
	 */
	@Scheduled(cron = "0 0 1 1 * ?")  // 每月1号凌晨1点执行
	@PostConstruct
	@Transactional
	public void doMonthConfiguration() throws UnknownHostException {
		log.info("====================生成导师项目中的,上月结算数据-任务开始====================");
		Date now = new Date();
		//1、查询所有导师数据
		List<TutorInfo> list = tutorInfoService.list();
		//2、迭代循环导师数据
		for (TutorInfo oldTutorInfo : list) {
			//3、创建修改的导师项目list
			List<Configuration> updateConfigurationList = new ArrayList<Configuration>();
			//4、循环旧的导师项目list
			for (Configuration oldConfiguration : oldTutorInfo.getConfiguration()) {
				//5、查询旧导师，上月，卖出总次数
				Integer lastMonthSignTraceSaleTotal = userFollowUpPendingMapper.getLastMonthSignTraceSaleTotal(oldTutorInfo.getId(),oldConfiguration.getItemType());
				BigDecimal monthYield = BigDecimal.ZERO;//默认上月收益0
				BigDecimal winRate = BigDecimal.ZERO;//默认上月胜率0
				if (lastMonthSignTraceSaleTotal > 0) {//上月-信号跟随-卖出总次数 > 0,
					//随机生成70%-100% monthYield、winRate
					monthYield = StringUtil.scopeRandomValue();
					winRate = StringUtil.scopeRandomValue();
				}
				//6、修改项目对象=旧项目对象
				Configuration updateConfiguration = oldConfiguration; 
				updateConfiguration.setMonthYield(monthYield);//修改上月收益
				updateConfiguration.setWinRate(winRate);//修改上月胜率
				updateConfigurationList.add(updateConfiguration);
			}
			//7、修改导师项目
			TutorInfo updateTutorInfo = new TutorInfo();
			updateTutorInfo.setId(oldTutorInfo.getId());
			updateTutorInfo.setConfiguration(updateConfigurationList);
			updateTutorInfo.updateById();
			
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(oldTutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.DO_MONTH_CONFIGURATION.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.DO_MONTH_CONFIGURATION.getName());
			tr.setOldContent(oldTutorInfo.getConfigurationContent());
			tr.setNewContent(updateTutorInfo.getConfigurationContent());
			tr.setOperator("系统");
			tr.setIp("127.0.0.1");
			tr.setIpAddress("服务器本机ip");
			tr.setCreateTime(now);
			tr.insert();
		}
		log.info("====================生成导师项目中的,上月结算数据-任务结束====================");
	}
	
}
