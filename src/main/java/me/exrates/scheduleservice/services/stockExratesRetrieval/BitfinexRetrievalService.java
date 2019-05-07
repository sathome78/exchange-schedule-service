package me.exrates.scheduleservice.services.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Log4j2(topic = "Service_layer_log")
//@Service(value = "BITFINEX")
public class BitfinexRetrievalService implements StockExrateRetrievalService {

    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public BitfinexRetrievalService(ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        List<StockExchangeStatsDto> stockExchangeStatsList = new ArrayList<>();
        stockExchange.getAliasedCurrencyPairs((name1, name2) -> name1.toLowerCase() + name2.toLowerCase())
                .forEach((name, currencyPair) -> {
                    String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://api.bitfinex.com/v1/pubticker/" + name);
                    StockExchangeStatsDto stockExchangeStatsDto = exchangeResponseProcessingService.extractStatsFromSingleNode(jsonResponse,
                            stockExchange, currencyPair.getId());
                    if (nonNull(stockExchangeStatsDto)) {
                        stockExchangeStatsList.add(stockExchangeStatsDto);
                    }
                });
        return stockExchangeStatsList;
    }
}