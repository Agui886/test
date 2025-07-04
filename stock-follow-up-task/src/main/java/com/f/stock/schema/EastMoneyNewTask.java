package com.f.stock.schema;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import entity.SiteNews;
import lombok.extern.slf4j.Slf4j;
import service.SiteNewsService;
import utils.StringUtil;

@Component
@Slf4j
public class EastMoneyNewTask {
	
	private final String baseUrl = "http://eminfo.eastmoney.com";
	private final String[] typeUrls = {
			"/pc_news/FastNews/GetImportantNewsList",
			"/pc_news/FastNews/GetInfoList?code=125&pageNumber=1&pagesize=20&condition=&r=",
			"/pc_news/FastNews/GetInfoList?code=105&pageNumber=1&pagesize=20&condition=&r=",
			"/pc_news/FastNews/GetInfoList?code=100&pageNumber=1&pagesize=20&condition=&r=",
			"/pc_news/FastNews/GetInfoList?code=106&pageNumber=1&pagesize=20&condition=&r=",
			"/pc_news/FastNews/GetInfoList?code=103&pageNumber=1&pagesize=20&condition=&r=",
			"/pc_news/FastNews/GetInfoList?code=118&pageNumber=1&pagesize=20&condition=&r="
	};
	@Resource
	private SiteNewsService siteNewsService;
	
	/**
	 * 新闻类型：1、财经要闻，2、经济数据，3、全球股市，4、7*24全球，5、商品资讯，6、上市公司，7、全球央行
	 */
	@Async("stockTask")
	@Scheduled(cron = "0 0/30 9-20 * * ?")
	//@PostConstruct
	public void getNews() {
		log.info("新聞資訊抓取任务开始");
		String ret = "";
		try {
			for(int i = 0; i < typeUrls.length; i++) {
				ret = HttpRequest.get(baseUrl +  typeUrls[i]).execute().body();
				if(!StringUtil.isEmpty(ret)) {
					JSONObject json = new JSONObject(ret);
					JSONArray items = json.getJSONArray("items");
					if(items != null && items.size() > 0) {
						for(Object o : items) {
							JSONObject item = new JSONObject(o);
							String code = item.getStr("code");
							if(this.siteNewsService.lambdaQuery().eq(SiteNews::getSourceId, code).count() > 0) {
								continue;
							}
							ret = HttpRequest.get(baseUrl +  "/PC_News/Detail/GetDetailContent?id="+ code +"&type=1").execute().body();
							//替换所有引号
							ret = ret.replace("\\\"","\"");
							ret = StringUtil.decodeUnicode(ret);
							//ret = StringUtil.delHTMLTag(ret);/*******不清理html标签，用于客户端显示富文本新闻内容*******/
							ret = ret.substring(1, ret.length() - 1);
							JSONObject detail = new JSONObject(ret);
							JSONObject data = detail.getJSONObject("data");
							SiteNews sn = new SiteNews();
							sn.setType(i + 1);
							sn.setTitle(item.getStr("title"));
							sn.setSourceId(code);
							sn.setSourceName(item.getStr("source"));
							sn.setViews(item.getInt("commentCount"));
							sn.setShowTime(item.getDate("updateTime"));
							sn.setDescription(item.getStr("digest"));
							sn.setImgUrl(data.getStr("smallImage"));
							sn.setContent(data.getStr("content"));
							sn.insert();
						}
					}
				}
			}
		} catch(Exception ex) {
			log.info("新聞資訊抓取任务异常{}",ret,  ex);
		}
		log.info("新聞資訊抓取任务结束");
	}
}
