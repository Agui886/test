package com.f.stock.controller.follow;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserFollowUpRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mapper.UserFollowUpRecordMapper;
import service.UserFollowUpRecordService;
import vo.manager.FollowRecordParamVO;

@RestController
@RequestMapping("/follow/record")
@Api(tags = "跟投管理")
public class FollowRecordController {

	@Resource
	private UserFollowUpRecordMapper userFollowUpRecordMapper;
	
	@Resource
	private UserFollowUpRecordService userFollowUpRecordService;
	
	@ApiOperation("用户跟投记录")
	@PostMapping("/list")
	public Response<Page<UserFollowUpRecord>> list(@RequestBody FollowRecordParamVO param) {
		Page<UserFollowUpRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		userFollowUpRecordService.managerList(page, param);
		return Response.successData(page);
	}
	
}
