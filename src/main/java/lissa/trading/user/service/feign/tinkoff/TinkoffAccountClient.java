package lissa.trading.user.service.feign.tinkoff;

import lissa.trading.lissa.auth.lib.dto.UpdateTinkoffTokenResponce;
import lissa.trading.user.service.config.InternalFeignConfig;
import lissa.trading.user.service.dto.tinkoff.Stock;
import lissa.trading.user.service.dto.tinkoff.account.AccountInfoDto;
import lissa.trading.user.service.dto.tinkoff.account.BalanceDto;
import lissa.trading.user.service.dto.tinkoff.account.FavouriteStocksDto;
import lissa.trading.user.service.dto.tinkoff.account.MarginAttributesDto;
import lissa.trading.user.service.dto.tinkoff.account.SecurityPositionsDto;
import lissa.trading.user.service.dto.tinkoff.account.TinkoffTokenDto;
import lissa.trading.user.service.dto.tinkoff.stock.FigiesDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksDto;
import lissa.trading.user.service.dto.tinkoff.stock.StocksPricesDto;
import lissa.trading.user.service.dto.tinkoff.stock.TickersDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tinkoff-service",
        url = "${integration.rest.services.tinkoff-api.url}/v1/internal",
        configuration = InternalFeignConfig.class)
public interface TinkoffAccountClient {

    @PostMapping("/set-token")
    UpdateTinkoffTokenResponce setTinkoffToken(@RequestBody TinkoffTokenDto tinkoffToken);

    @GetMapping("/{ticker}")
    Stock getStockByTicker(@PathVariable("ticker") String ticker);

    @PostMapping("/getStocksByTickers")
    StocksDto getStocksByTickers(@RequestBody TickersDto tickers);

    @PostMapping("/prices")
    StocksPricesDto getPricesStocksByFigies(@RequestBody FigiesDto figiesDto);

    @GetMapping("/accounts")
    AccountInfoDto getAccountsInfo();

    @GetMapping("/favourites")
    FavouriteStocksDto getFavouriteStocks();

    @GetMapping("/portfolio/{accountId}")
    BalanceDto getPortfolio(@PathVariable("accountId") String accountId);

    @GetMapping("/margin/{accountId}")
    MarginAttributesDto getMarginAttributes(@PathVariable("accountId") String accountId);

    @GetMapping("/positions/{accountId}")
    SecurityPositionsDto getPositionsById(@PathVariable("accountId") String accountId);
}