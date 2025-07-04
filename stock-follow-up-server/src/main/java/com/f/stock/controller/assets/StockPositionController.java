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
import service.UserStockPositionService;
import vo.server.UserStockPositionListVO;
import vo.server.StockPositionDetailVO;

@Controller
@RequestMapping("/assets/stock/position")
@Api(tags = "资产")
public class StockPositionController extends BaseController {
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@ApiOperation("持仓列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserStockPositionListVO>> list(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize) {
		Page<UserStockPositionListVO> page = new Page<>(pageNo, pageSize);
		userStockPositionService.stockPositionList(page, super.getTokenUser().getLoginId(), null, null);
		return Response.successData(page);
	}
	
	@ApiOperation("持仓详情")
	@PostMapping("/detail")
	@ResponseBody
	public Response<StockPositionDetailVO> detail(@ApiParam("持仓id") @RequestParam(value = "id") Integer id) {
		return userStockPositionService.stockPositionDetail(id, super.getTokenUser().getLoginId());
	}
}
