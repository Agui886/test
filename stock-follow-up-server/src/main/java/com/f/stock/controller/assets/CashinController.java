package com.f.stock.controller.assets;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserInfo;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.CashinRecordService;
import service.UserInfoService;
import vo.common.TokenUserVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashinRecordVO;

@Controller
@RequestMapping("/assets/cashin")
@Api(tags = "资产")
public class CashinController extends BaseController {
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource 
	private CashinRecordService cashinRecordService;
	
	@ApiOperation("入金-客服链接")
	@PostMapping("/serviceUrl")
	@ResponseBody
	public Response<String> serviceUrl() {
		TokenUserVO tu = super.getTokenUser();
		UserInfo ui = userInfoService.getById(tu.getLoginId());
//		AgentInfo ai = agentInfoService.getById(ui.getAgentId());
		String url = "";
//		if(ai != null) {
//			url = ai.getServiceUrl();
//		}
		return Response.successData(url);
	}
	
	@ApiOperation("入金-记录")
	@PostMapping("/record")
	@ResponseBody
	public Response<Page<CashinRecordVO>> record(@RequestBody CashinAndOutRecordParamVO param) {
		Page<CashinRecordVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		TokenUserVO tu = super.getTokenUser();
		param.setUserId(tu.getLoginId());
		cashinRecordService.record(page, param);
		return Response.successData(page);
	}
}
