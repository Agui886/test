package com.f.stock.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import entity.BankInfo;
import entity.Country;
import entity.LevelDef;
import entity.common.Response;
import enums.AmtDeTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.BankInfoService;
import service.CountryService;
import service.LevelDefService;
import vo.common.TypeVO;

@Controller
@RequestMapping("/list")
@Api(tags = "列表数据")
public class ListController {
	
	@Resource
	private CountryService countryService;
	
	@Resource
	private BankInfoService bankInfoService;	
	
	@Resource
	private LevelDefService levelDefService;	
	
	@ApiOperation("国家地区")
	@PostMapping("/country")
	@ResponseBody
	public Response<List<Country>> country() {
		List<Country> list = countryService.countryList();
		return Response.successData(list);
	}
	
	@ApiOperation("银行列表")
	@PostMapping("/bank")
	@ResponseBody
	public Response<List<BankInfo>> bank() {
		List<BankInfo> list = bankInfoService.lambdaQuery().eq(BankInfo::getIsShow, true).list();
		return Response.successData(list);
	}
	
	@ApiOperation("代理等级列表")
	@PostMapping("/levelDef")
	@ResponseBody
	public Response<List<LevelDef>> levelDef() {
		List<LevelDef> list = levelDefService.lambdaQuery().list();
		return Response.successData(list);
	}
	
	@ApiOperation("资金变化类型列表")
	@PostMapping("/amtDeType")
	@ResponseBody
	public Response<List<TypeVO>> amtDeType() {
		List<TypeVO> list = new ArrayList<>();
		for(AmtDeTypeEnum e : AmtDeTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	
}
