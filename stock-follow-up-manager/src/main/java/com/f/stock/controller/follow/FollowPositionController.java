package com.f.stock.controller.follow;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserFollowUpPosition;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserFollowUpPendingService;
import service.UserFollowUpPositionService;
import vo.manager.FollowPositionListSearchParamVO;

@RestController
@RequestMapping("/follow/position")
@Api(tags = "跟投管理")
public class FollowPositionController extends BaseController {
	
	@Resource
	private UserFollowUpPositionService userFollowUpPositionService;
	
	@ApiOperation("用户跟投持仓")
	@PostMapping("/list")
	public Response<Page<UserFollowUpPosition>> list(@RequestBody FollowPositionListSearchParamVO param) {
		Page<UserFollowUpPosition> page = new Page<>(param.getPageNo(), param.getPageSize());
		userFollowUpPositionService.managerList(page, param);
		return Response.successData(page);
	}
	
}
