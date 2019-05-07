package me.exrates.scheduleservice.services.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Log4j2(topic = "Service_layer_log")
@Service(value = "CEXio")
public class CexIoRetrievalService implements StockExrateRetrievalService {

    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public CexIoRetrievalService(ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://cex.io/api/tickers/USD/BTC/EUR/");
        JsonNode root = exchangeResponseProcessingService.extractNode(jsonResponse, "data");
        if (isNull(root)) {
            return Collections.emptyList();
        }
        return exchangeResponseProcessingService.extractAllStatsFromArrayNode(stockExchange, root, "pair",
                (name1, name2) -> name1.concat(":").concat(name2));
    }
}