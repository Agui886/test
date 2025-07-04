package com.f.stock.controller.risk;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import entity.common.Response;
import entity.common.StockParamConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.SysParamConfigService;

@RestController
@RequestMapping("/riskControlSettings")
@Api(tags = "风控管理")
public class StockSettingsController {
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@ApiOperation("股票风控")
	@PostMapping("/stockParamConfig")
	@ResponseBody
	public Response<StockParamConfig> stockParamConfig() {
		sysParamConfigService.getSysParamConfig();
		return Response.successData(sysParamConfigService.getSysParamConfig());
	}
	
	@ApiOperation("股票风控-保存")
	@PostMapping("/stockParamConfig/save")
	@ResponseBody
	public Response<Void> stockParamConfigSave(@RequestBody StockParamConfig param) {
		return sysParamConfigService.saveStockParamConfig(param);
	}
	
}
