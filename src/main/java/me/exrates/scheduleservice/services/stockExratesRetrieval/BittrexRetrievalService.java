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
@Service(value = "Bittrex")
public class BittrexRetrievalService implements StockExrateRetrievalService {

    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public BittrexRetrievalService(ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://bittrex.com/api/v1.1/public/getmarketsummaries");
        JsonNode root = exchangeResponseProcessingService.extractNode(jsonResponse, "result");
        if (isNull(root)) {
            return Collections.emptyList();
        }
        return exchangeResponseProcessingService.extractAllStatsFromArrayNode(stockExchange, root, "MarketName",
                (name1, name2) -> name2.concat("-").concat(name1));
    }
}