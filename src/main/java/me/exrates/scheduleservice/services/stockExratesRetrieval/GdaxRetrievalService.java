package me.exrates.scheduleservice.services.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import me.exrates.scheduleservice.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.exrates.scheduleservice.ScheduleServiceConfiguration.JSON_MAPPER;

@Log4j2(topic = "Service_layer_log")
@Service(value = "Gdax")
public class GdaxRetrievalService implements StockExrateRetrievalService {

    private final ObjectMapper objectMapper;
    private final ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Autowired
    public GdaxRetrievalService(@Qualifier(JSON_MAPPER) ObjectMapper objectMapper,
                                ExchangeResponseProcessingService exchangeResponseProcessingService) {
        this.objectMapper = objectMapper;
        this.exchangeResponseProcessingService = exchangeResponseProcessingService;
    }

    @Override
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        List<StockExchangeStatsDto> stockExchangeStatsList = new ArrayList<>();
        stockExchange.getAliasedCurrencyPairs((name1, name2) -> name1.concat("-").concat(name2))
                .forEach((currencyPairName, currencyPair) -> {
                    String urlTicker = String.format("https://api.gdax.com/products/%s/ticker", currencyPairName);
                    String urlStats = String.format("https://api.gdax.com/products/%s/stats", currencyPairName);
                    String jsonResponseTicker = exchangeResponseProcessingService.sendGetRequest(urlTicker);
                    String jsonResponseStats = exchangeResponseProcessingService.sendGetRequest(urlStats);
                    try {
                        JsonNode tickerRoot = objectMapper.readTree(jsonResponseTicker);
                        JsonNode statsRoot = objectMapper.readTree(jsonResponseStats);
                        StockExchangeStatsDto stockExchangeStats = new StockExchangeStatsDto();
                        stockExchangeStats.setStockExchange(stockExchange);
                        stockExchangeStats.setCurrencyPairId(currencyPair.getId());
                        stockExchangeStats.setPriceSell(BigDecimalProcessingUtil.parseNonePoint(tickerRoot.get("ask").asText()));
                        stockExchangeStats.setPriceBuy(BigDecimalProcessingUtil.parseNonePoint(tickerRoot.get("bid").asText()));
                        stockExchangeStats.setPriceLast(BigDecimalProcessingUtil.parseNonePoint(statsRoot.get("last").asText()));
                        stockExchangeStats.setPriceLow(BigDecimalProcessingUtil.parseNonePoint(statsRoot.get("low").asText()));
                        stockExchangeStats.setPriceHigh(BigDecimalProcessingUtil.parseNonePoint(statsRoot.get("high").asText()));
                        stockExchangeStats.setVolume(BigDecimalProcessingUtil.parseNonePoint(statsRoot.get("volume").asText()));
                        stockExchangeStatsList.add(stockExchangeStats);
                    } catch (IOException e) {
                        log.error(e);
                    }
                });
        return stockExchangeStatsList;
    }
}