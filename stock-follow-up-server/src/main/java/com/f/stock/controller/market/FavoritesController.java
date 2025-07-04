package com.f.stock.controller.market;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserFavoriteStock;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserFavoriteStockService;
import vo.common.TokenUserVO;
import vo.server.AddFavoriteStockParamVO;

@Controller
@RequestMapping("/market/favorite")
@Api(tags = "交易和市场")
public class FavoritesController extends BaseController {
	
	@Resource
	private UserFavoriteStockService userFavoriteStockService;
	
	@ApiOperation("自选-股票列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserFavoriteStock>> list(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize
			) {
		Page<UserFavoriteStock> page = new Page<>(pageNo, pageSize);
		userFavoriteStockService.favoriteList(page, super.getTokenUser().getLoginId());
		return Response.successData(page);
	}
	
	@ApiOperation("自选-添加自选，可批量")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody List<AddFavoriteStockParamVO> param) {
		return userFavoriteStockService.addStocks(super.getTokenUser().getLoginId(), param);
	}
	
	@ApiOperation("自选-删除自选，可批量")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@RequestBody List<AddFavoriteStockParamVO> param) {
		Integer userId = super.getTokenUser().getLoginId();
		param.forEach(i->{
			userFavoriteStockService.lambdaUpdate()
				.eq(UserFavoriteStock::getUserId, userId)
				.eq(UserFavoriteStock::getStockCode, i.getStockCode())
				.eq(UserFavoriteStock::getStockType, i.getStockType()).remove();
		});
		return Response.success();
	}
}
