package me.exrates.scheduleservice.services.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Log4j2(topic = "Service_layer_log")
@Service(value = "Bitstamp")
public class BitstampRetrievalService implements StockExrateRetrievalService {

    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public BitstampRetrievalService(ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        List<StockExchangeStatsDto> stockExchangeStatsList = new ArrayList<>();
        stockExchange.getAliasedCurrencyPairs((name1, name2) -> name1.toLowerCase() + name2.toLowerCase())
                .forEach((currencyPairName, currencyPair) -> {
                    String url = "https://www.bitstamp.net/api/v2/ticker/" + currencyPairName + "/";
                    String jsonResponse = exchangeResponseProcessingService.sendGetRequest(url);
                    StockExchangeStatsDto stockExchangeStatsDto = exchangeResponseProcessingService.extractStatsFromSingleNode(jsonResponse,
                            stockExchange, currencyPair.getId());
                    if (nonNull(stockExchangeStatsDto)) {
                        stockExchangeStatsList.add(stockExchangeStatsDto);
                    }
                });
        return stockExchangeStatsList;
    }
}