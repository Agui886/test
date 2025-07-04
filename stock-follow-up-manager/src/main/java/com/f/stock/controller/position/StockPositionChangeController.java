package com.f.stock.controller.position;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserPositionChangeRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserPositionChangeRecordService;
import utils.StringUtil;
import vo.manager.PositionChangeParamVO;

@RestController
@RequestMapping("/position/changeRecord")
@Api(tags = "持仓管理")
public class StockPositionChangeController {

	@Resource
	private UserPositionChangeRecordService userStockPositionChangeRecordService;

	@ApiOperation("持仓操作记录")
	@PostMapping("/list")
	public Response<Page<UserPositionChangeRecord>> list(@RequestBody PositionChangeParamVO param) {
		Page<UserPositionChangeRecord> page = new Page<UserPositionChangeRecord>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<UserPositionChangeRecord> lqw = new LambdaQueryWrapper<>();
		if(param.getUserId() != null && param.getUserId() > 0) {
			lqw.eq(UserPositionChangeRecord::getUserId, param.getUserId());
		}
		if(param.getPositionId() != null && param.getPositionId() > 0) {
			lqw.eq(UserPositionChangeRecord::getPositionId, param.getPositionId());
		}
		if(!StringUtil.isEmpty(param.getStockCodeOrName())) {
			lqw.or(l->l.like(UserPositionChangeRecord::getStockCode, param.getStockCodeOrName()).or().like(UserPositionChangeRecord::getStockName, param.getStockCodeOrName()));
		}
		if(!StringUtil.isEmpty(param.getDataChangeTypeCode())) {
			lqw.eq(UserPositionChangeRecord::getDataChangeTypeCode, param.getDataChangeTypeCode());
		}
		if(param.getStartTime() != null) {
			lqw.ge(UserPositionChangeRecord::getCreateTime, param.getStartTime());
		}
		if(param.getEndTime() != null) {
			lqw.le(UserPositionChangeRecord::getCreateTime, param.getEndTime());
		}
		lqw.orderByDesc(UserPositionChangeRecord::getId);
		userStockPositionChangeRecordService.page(page, lqw);
		return Response.successData(page);
	}
}
