package me.exrates.scheduleservice.services.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface ExchangeResponseProcessingService {

    String sendGetRequest(String url, Map<String, String> params);

    String sendGetRequest(String url);

    List<StockExchangeStatsDto> extractAllStatsFromMapNode(StockExchangeDto stockExchange, String jsonResponse, BiFunction<String, String, String> currencyPairTransformer);

    JsonNode extractNode(String source, String... targetNodes);

    List<StockExchangeStatsDto> extractAllStatsFromArrayNode(StockExchangeDto stockExchange, JsonNode targetNode, String currencyPairNameField,
                                                             BiFunction<String, String, String> currencyPairTransformer);

    StockExchangeStatsDto extractStatsFromSingleNode(String jsonResponse, StockExchangeDto stockExchange, int currencyPairId);
}