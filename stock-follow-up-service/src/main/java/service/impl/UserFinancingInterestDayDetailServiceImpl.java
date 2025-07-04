package service.impl;

import entity.UserFinancingInterestDayDetail;
import mapper.UserFinancingInterestDayDetailMapper;
import service.UserFinancingInterestDayDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户融资每日利息结算 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
@Service
public class UserFinancingInterestDayDetailServiceImpl extends ServiceImpl<UserFinancingInterestDayDetailMapper, UserFinancingInterestDayDetail> implements UserFinancingInterestDayDetailService {

	@Resource
	private UserFinancingInterestDayDetailMapper userFinancingInterestDayDetailMapper;
	
	/**
	 * 获取用户所产生的所有利息
	 */
	@Override
	public BigDecimal getInterestGeneratedByUserId(Integer userId) {
		return userFinancingInterestDayDetailMapper.getInterestGeneratedByUserId(userId);
	}

}
