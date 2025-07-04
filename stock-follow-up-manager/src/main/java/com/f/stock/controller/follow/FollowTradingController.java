package com.f.stock.controller.follow;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserFollowUpTrading;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserFollowUpTradingService;
import vo.manager.TradingListSearchParamVO;

@RestController
@RequestMapping("/follow/trading")
@Api(tags = "跟投管理")
public class FollowTradingController extends BaseController {
	
	@Resource
	private UserFollowUpTradingService userFollowUpTradingService;
	
	@ApiOperation("用户交易记录")
	@PostMapping("/list")
	public Response<Page<UserFollowUpTrading>> list(@RequestBody TradingListSearchParamVO param) {
		Page<UserFollowUpTrading> page = new Page<>(param.getPageNo(), param.getPageSize());
		userFollowUpTradingService.managerList(page, param);
		return Response.successData(page);
	}
	
}
