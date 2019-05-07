package me.exrates.scheduleservice.services.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@Log4j2(topic = "Service_layer_log")
@Service(value = "Binance")
public class BinanceRetrievalService implements StockExrateRetrievalService {

    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public BinanceRetrievalService(ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        List<StockExchangeStatsDto> stockExchangeStatsList = new ArrayList<>();
        stockExchange.getAliasedCurrencyPairs(String::concat)
                .forEach((currencyPairName, currencyPair) -> {
                    String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://www.binance.com/api/v1/ticker/24hr",
                            Collections.singletonMap("symbol", currencyPairName));
                    StockExchangeStatsDto stockExchangeStatsDto = exchangeResponseProcessingService.extractStatsFromSingleNode(jsonResponse,
                            stockExchange, currencyPair.getId());
                    if (nonNull(stockExchangeStatsDto)) {
                        stockExchangeStatsList.add(stockExchangeStatsDto);
                    }
                });
        return stockExchangeStatsList;
    }
}