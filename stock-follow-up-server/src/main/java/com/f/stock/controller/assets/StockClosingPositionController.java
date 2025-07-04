package com.f.stock.controller.assets;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserStockClosingPositionService;
import vo.server.UserStockClosingPositionDetailVO;
import vo.server.UserStockClosingPositionListVO;

@Controller
@RequestMapping("/assets/closing")
@Api(tags = "资产")
public class StockClosingPositionController extends BaseController {
	
	@Resource
	private UserStockClosingPositionService userStockClosingPositionService;
	
	@ApiOperation("平仓列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserStockClosingPositionListVO>> list (
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize
			) {
		Page<UserStockClosingPositionListVO> page = new Page<>(pageNo, pageSize);
		userStockClosingPositionService.userStockClosingPositionList(page, super.getTokenUser().getLoginId());
		return Response.successData(page);
	}
	
	@ApiOperation("平仓详情")
	@PostMapping("/detail")
	@ResponseBody
	public Response<UserStockClosingPositionDetailVO> detail (@ApiParam("平仓订单id") @RequestParam(value = "id") Integer id) {
		return userStockClosingPositionService.userStockClosingPositionDetail(id, super.getTokenUser().getLoginId());
	}
}
