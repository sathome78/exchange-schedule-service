package me.exrates.scheduleservice.services.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.CurrencyPairDto;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Log4j2(topic = "Service_layer_log")
@Service(value = "xBTCe")
public class XBTCeRetrievalService implements StockExrateRetrievalService {

    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public XBTCeRetrievalService(ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        Map<String, CurrencyPairDto> currencyPairs = stockExchange.getAliasedCurrencyPairs(String::concat);
        String urlBase = "https://cryptottlivewebapi.xbtce.net:8443/api/v1/public/ticker/";
        String urlFilter = String.join(" ", currencyPairs.keySet());
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest(urlBase + urlFilter);

        JsonNode root = exchangeResponseProcessingService.extractNode(jsonResponse);
        if (isNull(root)) {
            return Collections.emptyList();
        }

        List<StockExchangeStatsDto> stockExchangeStatsList = new ArrayList<>();
        root.elements().forEachRemaining(jsonNode -> {
            CurrencyPairDto currencyPair = currencyPairs.get(root.get("Symbol").asText());
            if (currencyPair != null) {
                stockExchange.setLastFieldName(extractLastPriceField(jsonNode));
                StockExchangeStatsDto stockExchangeStats = stockExchange.extractStatsFromNode(jsonNode, currencyPair.getId());
                stockExchangeStatsList.add(stockExchangeStats);
            }
        });
        return stockExchangeStatsList;
    }

    private String extractLastPriceField(JsonNode jsonNode) {
        long lastBuyTimestamp = jsonNode.get("LastBuyTimestamp").longValue();
        long lastSellTimestamp = jsonNode.get("LastSellTimestamp").longValue();
        return lastBuyTimestamp > lastSellTimestamp ? "LastBuyPrice" : "LastSellPrice";
    }
}