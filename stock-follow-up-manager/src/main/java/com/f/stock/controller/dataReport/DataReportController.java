package com.f.stock.controller.dataReport;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.TutorInfoService;
import service.UserInfoService;
import vo.manager.TutorDataStatisticsSearchParamVO;
import vo.manager.TutorDataStatisticsVO;
import vo.manager.UserFollowStatisticsSearchParamVO;
import vo.manager.UserFollowStatisticsVO;

@RestController
@RequestMapping("/dataReport")
@Api(tags = "数据报表")
public class DataReportController {
	
	@Resource
	private TutorInfoService tutorInfoService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@ApiOperation("导师数据统计")
	@PostMapping("/tutorDataStatistics")
	@ResponseBody
	public  Response<Page<TutorDataStatisticsVO>> tutorDataStatistics(@RequestBody TutorDataStatisticsSearchParamVO param) {
		Page<TutorDataStatisticsVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		tutorInfoService.tutorDataStatistics(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("用户跟投统计")
	@PostMapping("/userFollowStatistics")
	@ResponseBody
	public  Response<Page<UserFollowStatisticsVO>> userFollowStatistics(@RequestBody UserFollowStatisticsSearchParamVO param) {
		Page<UserFollowStatisticsVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		userInfoService.userFollowStatistics(page, param);
		return Response.successData(page);
	}
}
