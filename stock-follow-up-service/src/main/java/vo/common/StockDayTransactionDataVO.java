package vo.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockDayTransactionDataVO {

	@ApiModelProperty("分时成交,随机数据")
	private  StockDayTransactionDataPageVO randomStockDayTransactionDataPage;
	
	@ApiModelProperty("分时成交-分页数据")
	private Page<StockDayTransactionDataPageVO> stockDayTransactionDataPage;
	
}
