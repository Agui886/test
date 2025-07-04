package com.f.stock.controller.follow;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.SignInfo;
import entity.TutorInfo;
import entity.UserFollowUpRecord;
import entity.UserFollowUpRequest;
import entity.common.Response;
import enums.SignTypeEnum;
import enums.UserFollowRequestStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import mapper.UserFollowUpPendingMapper;
import service.SignInfoService;
import service.TutorInfoService;
import service.UserFollowUpPendingService;
import service.UserFollowUpRecordService;
import service.UserFollowUpRequestService;
import utils.StringUtil;
import vo.common.TokenUserVO;
import vo.server.FollowTutorDetailSignInfoVO;
import vo.server.FollowTutorDetailVO;
import vo.server.FollowTutorListParamVO;
import vo.server.FollowTutorListVO;
import vo.server.UserFollowRecordVO;

@Controller
@RequestMapping("/follow/tutor")
@Api(tags = "跟投")
public class TutorController extends BaseController {
	
	@Resource
	private TutorInfoService tutorInfoService;
	
	@Resource
	private UserFollowUpRecordService userFollowUpRecordService;
	
	@Resource
	private UserFollowUpPendingService userFollowUpPendingService;
	
	@Resource
	private UserFollowUpPendingMapper userFollowUpPendingMapper;
	
	@Resource
	private SignInfoService signInfoService;
	
	@Resource
	private UserFollowUpRequestService userFollowUpRequestService;
	
	@ApiOperation("跟投-导师-列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<FollowTutorListVO>> list(@RequestBody FollowTutorListParamVO param) {
		Page<FollowTutorListVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		TokenUserVO tu = super.getTokenUser();
		param.setUserId(tu.getLoginId());
		param.setIsEnabled(true);
		tutorInfoService.followTutorList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("跟投-导师信息")
	@PostMapping("/tutorDetail")
	@ResponseBody
	public Response<FollowTutorDetailVO> tutorDetail(
			@ApiParam("导师id") @RequestParam(value = "tutorId") Integer tutorId,
			@ApiParam("项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投") @RequestParam(value = "itemType",defaultValue = "", required = false) Integer itemType
			) {
		if (tutorId == null) {
			return Response.fail("tutorId必传");
		}
		TutorInfo tutorInfo = tutorInfoService.getById(tutorId);
		if (tutorInfo != null) {
			FollowTutorDetailVO followTutorDetailVO = new FollowTutorDetailVO();
			//tutorInfo对象相同属性，赋值进followTutorDetailVO中
			BeanUtils.copyProperties(tutorInfo, followTutorDetailVO);
			//随机百分比相关参数
			followTutorDetailVO.setTotalYield(StringUtil.scopeRandomValue());
			//followTutorDetailVO.setMonthYield(StringUtil.scopeRandomValue());
			followTutorDetailVO.setPositionRate(StringUtil.scopeRandomValue());
			//followTutorDetailVO.setWinRate(StringUtil.scopeRandomValue());
			//上月-信号跟随-人数（用户去重）
			Integer lastMonthSignTraceUser = userFollowUpPendingMapper.getLastMonthSignTraceUser(tutorId,itemType);
			followTutorDetailVO.setLastMonthSignTraceUser(lastMonthSignTraceUser);
			//上月-信号跟随-卖出总次数
			Integer lastMonthSignTraceSaleTotal = userFollowUpPendingMapper.getLastMonthSignTraceSaleTotal(tutorId,itemType);
			followTutorDetailVO.setLastMonthSignTraceSaleTotal(lastMonthSignTraceSaleTotal);
			
			//查询当前导师，最近的3条的买入信号
			List<SignInfo> saleSignInfoList = signInfoService.lambdaQuery()
					.eq(SignInfo :: getTutorId, tutorId)
					.eq(SignInfo :: getSignType, SignTypeEnum.SALE.getCode())
					.eq(SignInfo :: getIsConfigured, true)
					.eq(itemType != null, SignInfo :: getItemType, itemType)
					.orderByDesc(SignInfo :: getDisplayReleaseTime)
					.last("LIMIT 3")
					.list();
			//迭代查询，买入信号对应的卖出信号
			List<FollowTutorDetailSignInfoVO> FollowTutorDetailSignInfoVOList = new ArrayList<FollowTutorDetailSignInfoVO>();
			for(SignInfo saleSignInfo : saleSignInfoList) {
				FollowTutorDetailSignInfoVO followTutorDetailSignInfoVO = new FollowTutorDetailSignInfoVO();
				//通过买入id查1条卖出信号
				SignInfo buySignInfo = signInfoService.lambdaQuery()
						.eq(SignInfo :: getBuySignId, saleSignInfo.getBuySignId())
						.eq(SignInfo :: getIsConfigured, true)
						.orderByDesc(SignInfo :: getDisplayReleaseTime)
						.last("LIMIT 1")// 强制限制1条结果
						.one();
				//有卖出信号，处理卖出信号数据
				if (buySignInfo != null) {
					//buySignInfo对象相同属性，赋值进followTutorDetailSignInfoVO中
					BeanUtils.copyProperties(buySignInfo, followTutorDetailSignInfoVO);
					followTutorDetailSignInfoVO.setSaleSignInfoId(saleSignInfo.getId());
					followTutorDetailSignInfoVO.setSalePrice1(saleSignInfo.getPrice1());
					followTutorDetailSignInfoVO.setSaledisplayReleaseTime(saleSignInfo.getDisplayReleaseTime());
				}
				FollowTutorDetailSignInfoVOList.add(followTutorDetailSignInfoVO);
			}
			//近期买入信号记录
			followTutorDetailVO.setFollowTutorDetailSignInfoVO(FollowTutorDetailSignInfoVOList);
			
			//用户项目跟投状态，默认，0未跟上
			Integer userItemTypeFollowStatus = 0;
			
			//查询用户跟投记录，不包含已结束的记录
			UserFollowUpRecord param = new UserFollowUpRecord();
			param.setUserId(super.getTokenUser().getLoginId());
			param.setTutorId(tutorId);
			param.setItemType(itemType);
			List<UserFollowRecordVO> userFollowRecordVOList = userFollowUpRecordService.userFollowRecord(param);
			if (userFollowRecordVOList.size() > 0) {
				userItemTypeFollowStatus = 2;//用户项目跟投状态，跟投进行中
				followTutorDetailVO.setUserFollowRecordVOList(userFollowRecordVOList);//存入用户跟投记录
			}else {
				//查询审核单，只查审核中的数据
				List<UserFollowUpRequest> userFollowUpRequestList = userFollowUpRequestService.lambdaQuery()
						.eq(UserFollowUpRequest :: getUserId, super.getTokenUser().getLoginId())
						.eq(UserFollowUpRequest :: getTutorId, tutorId)
						.eq(UserFollowUpRequest :: getStatus, UserFollowRequestStatusEnum.AUDIT.getCode())
						.eq(itemType != null, UserFollowUpRequest :: getItemType ,itemType)
						.list();
				if(userFollowUpRequestList.size() > 0) {
					userItemTypeFollowStatus = 1;//用户项目跟投状态，审核中
					followTutorDetailVO.setUserFollowRequest(userFollowUpRequestList);//存入用户审核记录
				}
			}
			//设置用户项目跟投状态
			followTutorDetailVO.setUserItemTypeFollowStatus(userItemTypeFollowStatus);
			
			return Response.successData(followTutorDetailVO);
		}else {
			return Response.fail("导师信息不存在");
		}
	}
	
}
