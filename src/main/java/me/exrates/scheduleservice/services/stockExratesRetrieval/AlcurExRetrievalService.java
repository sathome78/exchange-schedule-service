package me.exrates.scheduleservice.services.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2(topic = "Service_layer_log")
@Service(value = "alcurEX")
public class AlcurExRetrievalService implements StockExrateRetrievalService {

    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public AlcurExRetrievalService(ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://alcurex.com/api/tickerapi");
        return exchangeResponseProcessingService.extractAllStatsFromMapNode(stockExchange, jsonResponse, (name1, name2) -> name1.concat("_").concat(name2));
    }
}