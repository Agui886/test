package vo.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchStockResultVO extends StockData {
	
	@ApiModelProperty("是否自选")
	private boolean isFavorite;
}
