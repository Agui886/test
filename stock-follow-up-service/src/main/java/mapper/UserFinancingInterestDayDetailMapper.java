package mapper;

import entity.UserFinancingInterestDayDetail;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户融资每日利息结算 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface UserFinancingInterestDayDetailMapper extends BaseMapper<UserFinancingInterestDayDetail> {

	/**
	 * 获取用户所产生的所有利息
	 * @param userId
	 * @return
	 */
	BigDecimal getInterestGeneratedByUserId(Integer userId);
}
