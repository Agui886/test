package service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.common.Response;
import entity.common.StockInfo;
import vo.common.StockDayTransactionParamVO;
import vo.common.StockDayTransactionVO;
import vo.manager.SignListSearchParamVO;
import vo.server.StockData;
import vo.server.StockDetailVO;

/**
 * <p>
 * 股票产品信息表 服务类
 * </p>
 *
 * @author 
 * @since 2024-11-07
 */
public interface StockInfoService {
	
	/**
	 * 获取股票信息，根据股票类型和股票代码
	 * @param stockType
	 * @param stockCode
	 * @return
	 */
	StockInfo getStockInfoByStockTypeAndStockCode(String stockType, String stockCode);
	
	/**
	 * 获取股票涨幅排行榜数据
	 * @param pageNo 
	 * @param pageSize
	 * @param sortTerm 排序条件
	 * @param sortType 排序类型
	 * @param stockMarketType 市场类型
	 * @return
	 */
	Page<StockData> getStockQuoteListPage(Integer pageNo, Integer pageSize, String sortTerm, String sortType,
			Integer stockMarketTypeCode);
	
	/**
	 * 首页-股票榜单-热门
	 * @return
	 */
	Response<List<StockData>> getHotRankFromApi();
	
//	/**
//	 * 短期活跃股票
//	 * @param pageNo
//	 * @param pageSize
//	 * @param stockMarketTypeCode 股票市场类型
//	 * @return
//	 */
//	Page<StockData> getActiveStockListFromApi(Integer pageNo, Integer pageSize,Integer stockMarketTypeCode);


	/**
	 * 股票信息
	 * @param userId
	 * @param stockCode
	 * @param stockType
	 * @return
	 */
	Response<StockDetailVO> getStockDetail(Integer userId, String stockCode, String stockType);

//	/**
//	 * 股票数据初始化
//	 * @return
//	 */
//	Response<String> stockDataInitialize();
	
	/**
	 * 近1个月涨幅榜
	 * @param pageNo
	 * @param pageSize
	 * @param stockMarketTypeCode
	 * @return
	 */
	List<StockData> getIncreaseRateRank(Integer pageNo, Integer pageSize,Integer stockMarketTypeCode);

	/**
	 * 跟投信号-跟投信号详情-获取股票(分时成交记录)
	 * @param param
	 * @return
	 */
	StockDayTransactionVO getStockDayTransaction(StockDayTransactionParamVO param);


	

	
}
