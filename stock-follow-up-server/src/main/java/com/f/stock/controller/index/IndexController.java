 package com.f.stock.controller.index;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import annotation.NotNeedAuth;
import annotation.RequestLimit;
import entity.SiteAnnouncement;
import entity.SiteCarousel;
import entity.SiteNews;
import entity.TutorInfo;
import entity.common.Response;
import enums.StockMarketTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SiteAnnouncementService;
import service.SiteCarouselService;
import service.SiteNewsService;
import service.StockInfoService;
import service.TutorInfoService;
import vo.common.TokenUserVO;
import vo.server.FollowTutorListParamVO;
import vo.server.FollowTutorListVO;
import vo.server.IndexTutorRecommendVo;
import vo.server.StockData;

@Controller
@RequestMapping("/index")
@Api(tags = "首页")
public class IndexController {
	
	@Resource
	private SiteCarouselService siteCarouselService;
	
	@Resource
	private SiteNewsService siteNewsService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private SiteAnnouncementService announcementService;
	
	@Resource
	private TutorInfoService tutorInfoService;
	
	@ApiOperation("首页-轮播图，返回图片数据列表")
	@PostMapping("/carouseList")
	@RequestLimit
	@NotNeedAuth
	@ResponseBody
	public Response<LinkedHashSet<String>> carouseList() {
		List<SiteCarousel> list = siteCarouselService.lambdaQuery()
				.select(SiteCarousel::getImageData)
				.eq(SiteCarousel::getIsShow, true)
				.orderByAsc(SiteCarousel::getSort)
				.orderByDesc(SiteCarousel::getAddTime).list();
		LinkedHashSet<String> set = new LinkedHashSet<>();
		list.forEach(i->{
			set.add(i.getImageData());
		});
		return Response.successData(set);
	}
	
	@ApiOperation("首页-股票榜单")
	@PostMapping("/stockRank")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockData>> stockRank(
			@ApiParam("榜单类型：1、热门，2、涨幅榜") @RequestParam(value = "rankType", defaultValue = "1", required = false) Integer rankType
			) {
		if (rankType == 1) {//热门
			return stockInfoService.getHotRankFromApi();
		}else if (rankType == 2) {//涨幅榜
			Page<StockData> page = stockInfoService.getStockQuoteListPage(1, 10, "percentageIncrease", "desc",null);
			return Response.successData(page.getRecords());
		}else {
			return Response.fail("rankType类型不符");
		}
		
	}
	
	@ApiOperation("首页-智讯-公告列表")
	@PostMapping("/siteAnnouncementList")
	@NotNeedAuth
	@ResponseBody
	public Response<List<SiteAnnouncement>> siteAnnouncementList() {
		List<SiteAnnouncement> list = announcementService.lambdaQuery()
					.eq(SiteAnnouncement::getIsShow, true)
					.orderByAsc(SiteAnnouncement::getSort).orderByDesc(SiteAnnouncement::getAddTime)
					.select(SiteAnnouncement::getTitle, SiteAnnouncement::getSource, SiteAnnouncement::getContent).list();
		return Response.successData(list);
	}
	
	@ApiOperation("首页-导师推荐")
	@PostMapping("/tutorRecommend")
	@NotNeedAuth
	@ResponseBody
	public Response<IndexTutorRecommendVo> tutorRecommend() {
		IndexTutorRecommendVo indexTutorRecommendVo = new IndexTutorRecommendVo();
		
		Page<FollowTutorListVO> page = new Page<>(1, 1);
		FollowTutorListParamVO param = new FollowTutorListParamVO();
		param.setIsEnabled(true);
		param.setRecommendType(0);
		tutorInfoService.followTutorList(page, param);
		
		System.out.println(page.getRecords().size());
		
		indexTutorRecommendVo.setTodayRecommend(page.getRecords().get(0));
		
		param.setRecommendType(1);
		indexTutorRecommendVo.setDailyPreferred(page.getRecords().get(0));
		
		param.setRecommendType(2);
		indexTutorRecommendVo.setOneClickPreferred(page.getRecords().get(0));
		
		return Response.successData(indexTutorRecommendVo);
	}
	
//	@ApiOperation("近期活跃股票(首页三条)")
//	@PostMapping("/activeStocks")
//	@NotNeedAuth
//	@ResponseBody
//	public Response<List<StockData>> activeStocks() {
//		List<StockData> list = new ArrayList<StockData>();
//		Page<StockData> aStockPage = stockInfoService.getActiveStockListFromApi(1,1,StockMarketTypeEnum.Market_A.getCode());
//		if (aStockPage != null && aStockPage.getRecords() != null && aStockPage.getRecords().size() > 0) {
//			list.add(aStockPage.getRecords().get(0));
//		}
//		Page<StockData> hkStockPage = stockInfoService.getActiveStockListFromApi(1,1,StockMarketTypeEnum.Market_Hk.getCode());
//		if (hkStockPage != null && hkStockPage.getRecords() != null && hkStockPage.getRecords().size() > 0) {
//			list.add(hkStockPage.getRecords().get(0));
//		}
//		Page<StockData> usStockPage = stockInfoService.getActiveStockListFromApi(1,1,StockMarketTypeEnum.Market_Us.getCode());
//		if (usStockPage != null && usStockPage.getRecords() != null && usStockPage.getRecords().size() > 0) {
//			list.add(usStockPage.getRecords().get(0));
//		}
//		return Response.successData(list);
//	}
	
//	@ApiOperation("近期活跃股票列表")
//	@PostMapping("/activeStockList")
//	@NotNeedAuth
//	@ResponseBody
//	public Response<Page<StockData>> activeStockList(@ApiParam("股票市场类型：[0:A股，1:港股，2:美股]") @RequestParam(value = "stockMarketTypeCode", required = false, defaultValue = "0") Integer stockMarketTypeCode) {
//		Page<StockData> page = stockInfoService.getActiveStockListFromApi(1,100,stockMarketTypeCode);
//		return Response.successData(page);
//	}
	
	@ApiOperation("新闻列表")
	@NotNeedAuth
	@PostMapping("/news")
	@ResponseBody
	public Response<Page<SiteNews>> news (
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
			@ApiParam("新闻类型：1、财经要闻，2、经济数据，3、全球股市，4、7*24全球，5、商品资讯，6、上市公司，7、全球央行") @RequestParam(value = "type", defaultValue = "1", required = false) Integer type
			) {
		if(pageSize > 50) {
			pageSize = 50;
		}
		Page<SiteNews> page = new Page<>(pageNo, pageSize);
		LambdaQueryWrapper<SiteNews> lqw = new LambdaQueryWrapper<>();
		lqw.select(SiteNews::getId, SiteNews::getTitle, SiteNews::getShowTime, SiteNews::getDescription, SiteNews::getSourceName, SiteNews::getViews, SiteNews::getImgUrl, SiteNews::getType);
		lqw.eq(SiteNews::getType, type);
		lqw.orderByDesc(SiteNews::getShowTime);
		siteNewsService.page(page, lqw);
		return Response.successData(page);
	}
	
	@ApiOperation("新闻内容详情")
	@NotNeedAuth
	@PostMapping("/newsDetail")
	@ResponseBody
	public Response<SiteNews> newsDetail (@ApiParam("新闻id") @RequestParam(value = "id") Integer id) {
		LambdaQueryWrapper<SiteNews> lqw = new LambdaQueryWrapper<>();
		lqw.select(SiteNews::getId, SiteNews::getTitle, SiteNews::getShowTime, SiteNews::getDescription, SiteNews::getSourceName, SiteNews::getViews, SiteNews::getImgUrl, SiteNews::getContent, SiteNews::getType);
		lqw.eq(SiteNews::getId, id);
		return Response.successData(siteNewsService.getOne(lqw));
	}
	
	@ApiOperation("ping接口")
	@PostMapping("/ping")
	@NotNeedAuth
	@ResponseBody
	public Response ping() {
		return Response.success();
	}
}
