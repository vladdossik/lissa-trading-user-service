package lissa.trading.user.service.feign.moex;

import lissa.trading.user.service.dto.tinkoff.stock.StocksDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="moex-service", url="${integration.rest.moex-api-service-url}/v1/moex")
public interface MoexServiceClient {

    @PostMapping("/stocks")
    StocksDto getStocksByTicker(@RequestBody TickersDto tickers);

    @PostMapping("/stocks/prices")
    StocksPricesDto getStocksPrices(@RequestBody TickersDto tickers);
}
