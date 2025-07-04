package com.f.stock.controller.follow;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserFollowUpRequest;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserFollowUpRequestService;
import vo.manager.UserFollowUpRequestListSearchParamVO;

@RestController
@RequestMapping("/follow/request")
@Api(tags = "跟投管理")
public class FollowRequestController extends BaseController {
	
	@Resource
	private UserFollowUpRequestService userFollowUpRequestService;
	
	@ApiOperation("用户跟投审核")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserFollowUpRequest>> list(@RequestBody UserFollowUpRequestListSearchParamVO param) {
		Page<UserFollowUpRequest> page = new Page<>(param.getPageNo(), param.getPageSize());
		userFollowUpRequestService.managerList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("用户跟投审核-状态修改")
	@PostMapping("/updateStatus")
	@ResponseBody
	public Response<Void> updateStatus(@ApiParam("审核id") @RequestParam("id") Integer id, @ApiParam("状态值，1-通过,2-拒绝") @RequestParam("status") Integer status) {	
		return userFollowUpRequestService.updateStatus(id,status,super.getIp(),super.getUser().getOperator());	
	}
}
