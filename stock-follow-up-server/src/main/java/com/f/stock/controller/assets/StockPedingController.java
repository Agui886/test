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
import service.UserStockPendingService;
import vo.common.TokenUserVO;
import vo.server.StockPendingDetailVO;
import vo.server.StockPendingListVO;

@Controller
@RequestMapping("/assets/stock/pending")
@Api(tags = "资产")
public class StockPedingController extends BaseController {
	
	@Resource
	private UserStockPendingService userStockPendingService;
	
	@ApiOperation("挂单列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<StockPendingListVO>> list(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize, 
			@ApiParam("持仓状态，1-已委托，2-已成交，3-已撤销，4-已拒绝, 默认1") @RequestParam(value = "positionStatus", defaultValue = "1", required = false) Integer positionStatus
			) {
		Page<StockPendingListVO> page = new Page<>(pageNo, pageSize);
		userStockPendingService.pendingList(page, super.getTokenUser().getLoginId(), positionStatus);
		return Response.successData(page);
	}
	
	@ApiOperation("挂单详情")
	@PostMapping("/detail")
	@ResponseBody
	public Response<StockPendingDetailVO> detail(@ApiParam("委托订单id") @RequestParam(value = "id") Integer id) {
		return userStockPendingService.pendingDetail(id, super.getTokenUser().getLoginId());
	}
	
	@ApiOperation("挂单取消、撤单")
	@PostMapping("/cancel")
	@ResponseBody
	public Response<Void> cannel(@ApiParam("委托订单id") @RequestParam(value = "id") Integer id) {
		TokenUserVO tu = super.getTokenUser();
		return userStockPendingService.cancel(id, tu.getLoginId(), super.getIp(), tu.getOperator());
	}
}
