package service;

import entity.UserBankInfo;
import entity.common.Response;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员银行卡信息表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
public interface UserBankInfoService extends IService<UserBankInfo> {

	Response<Void> add(Integer userId, UserBankInfo userBankInfo, String ip, String operator);
	
	Response<Void> edit(UserBankInfo info, String ip, String operator);

}
