package lissa.trading.user.service.feign.moex;

import lissa.trading.user.service.config.InternalFeignConfig;
import lissa.trading.user.service.dto.tinkoff.stock.StocksDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="moex-service",
        url="${integration.rest.services.moex-api.url}/v1/internal",
        configuration = InternalFeignConfig.class)
public interface MoexServiceClient {

    @PostMapping("/stocks")
    StocksDto getStocksByTicker(@RequestBody TickersDto tickers);

    @PostMapping("/stocks/prices")
    StocksPricesDto getStocksPrices(@RequestBody TickersDto tickers);
}
