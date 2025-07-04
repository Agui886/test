package com.f.stock.controller.agent;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserInfo;
import entity.UserLevelChangeRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserInfoService;
import service.UserLevelChangeRecordService;
import vo.manager.AgentListSearchParamVO;
import vo.manager.AgentListVO;
import vo.manager.UserLevelChangeRecordParamVO;

@RestController
@RequestMapping("/agent")
@Api(tags = "代理管理")
public class AgentController extends BaseController {
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserLevelChangeRecordService userLevelChangeRecordService;
	
	@ApiOperation("代理列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<AgentListVO>> list(@RequestBody AgentListSearchParamVO param) {
		Page<AgentListVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		userInfoService.managerAgentList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("代理列表-修改是否允许邀请下级")
	@PostMapping("/updatePromotionEnable")
	@ResponseBody
	public Response<Void> updatePromotionEnable(@RequestParam("id") @ApiParam("用户id") Integer id, @RequestParam("enable") @ApiParam("1-开启,0-关闭") Boolean enable) {
		userInfoService.lambdaUpdate().set(UserInfo::getPromotionEnable, enable).eq(UserInfo::getId, id).update();
		return Response.success();
	}
	
	@ApiOperation("代理升降级记录")
	@PostMapping("/userLevelChangeRecord")
	@ResponseBody
	public Response<Page<UserLevelChangeRecord>> userLevelChangeRecord(@RequestBody UserLevelChangeRecordParamVO param) {
		Page<UserLevelChangeRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		userLevelChangeRecordService.managerList(page, param);
		return Response.successData(page);
	}
	

}
