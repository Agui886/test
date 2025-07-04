package com.f.stock.controller.position;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import annotation.NotNeedAuth;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserStockPendingService;
import service.UserStockPositionService;
import vo.manager.PendingListSearchParamVO;
import vo.manager.PendingListVO;
import vo.manager.PendingTransferDetailVO;
import vo.manager.TransferPositionSearchParamVO;

@RestController
@RequestMapping("/position/pending")
@Api(tags = "持仓管理")
public class PendingController extends BaseController {
	
	@Resource
	private UserStockPositionService userStockPositionService;
	@Resource
	private UserStockPendingService userStockPendingService;
	
	@ApiOperation("委托订单")
	@PostMapping("/list")
	public Response<Page<PendingListVO>> list(@RequestBody PendingListSearchParamVO param) {
		Page<PendingListVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		userStockPendingService.managerPendingList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("委托订单-转入详情（参数传委托单id）")
	@PostMapping("/transferDetail")
	@NotNeedAuth
	@ResponseBody
	public Response<PendingTransferDetailVO> transferDetail(@ApiParam("委托单ID，列表的ID") @RequestParam("id") Integer id) {
		return userStockPendingService.transferDetail(id);
	}
	
	@ApiOperation("委托订单-转入持仓（单笔和批量通用，参数传委托单id集合，格式json[1,2,3,n..]）")
	@PostMapping("/transferPosition")
	public Response<Void> transferPosition(@ApiParam("委托单ID，列表的ID") @RequestBody List<TransferPositionSearchParamVO> searchParamList) {
		return userStockPendingService.pendingTransferPosition(searchParamList, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("委托订单-拒绝订单（单笔和批量通用，参数传委托单id集合，格式json[1,2,3,n..]）")
	@PostMapping("/reject")
	public Response<Void> reject(@ApiParam("委托单ID，列表的ID") @RequestBody List<Integer> ids) {
		return userStockPendingService.reject(ids, getIp(), getUser().getOperator());
	}
}
