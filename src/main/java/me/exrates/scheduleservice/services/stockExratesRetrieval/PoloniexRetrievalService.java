package me.exrates.scheduleservice.services.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Log4j2(topic = "Service_layer_log")
@Service(value = "Poloniex")
public class PoloniexRetrievalService implements StockExrateRetrievalService {

    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public PoloniexRetrievalService(ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://poloniex.com/public", Collections.singletonMap("command", "returnTicker"));
        return exchangeResponseProcessingService.extractAllStatsFromMapNode(stockExchange, jsonResponse,
                (name1, name2) -> name2.concat("_").concat(name1));// IMPORTANT! In POLONIEX API currencies in pairs are inverted - i.e. DASH/BTC looks like BTC_DASH
    }
}