package me.exrates.scheduleservice.models.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.scheduleservice.utils.BigDecimalProcessingUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockExchangeDto {

    private Integer id;
    private String name;
    private String lastFieldName;
    private String buyFieldName;
    private String sellFieldName;
    private String lowFieldName;
    private String highFieldName;
    private String volumeFieldName;
    private List<CurrencyPairDto> availableCurrencyPairs = new ArrayList<>();
    private Map<String, String> currencyAliases = new HashMap<>();

    public Map<String, CurrencyPairDto> getAliasedCurrencyPairs(BiFunction<String, String, String> transformer) {
        return availableCurrencyPairs
                .stream()
                .collect(Collectors.toMap(
                        currencyPair -> transformer.apply(
                                currencyAliases.getOrDefault(currencyPair.getCurrency1().getName(), currencyPair.getCurrency1().getName()),
                                currencyAliases.getOrDefault(currencyPair.getCurrency2().getName(), currencyPair.getCurrency2().getName())),
                        currencyPair -> currencyPair));
    }

    public StockExchangeStatsDto extractStatsFromNode(JsonNode jsonNode, int currencyPairId) {
        StockExchangeStatsDto stockExchangeStats = new StockExchangeStatsDto();
        stockExchangeStats.setPriceLast(extractNumberForExistingName(jsonNode, lastFieldName));
        stockExchangeStats.setPriceBuy(extractNumberForExistingName(jsonNode, buyFieldName));
        stockExchangeStats.setPriceSell(extractNumberForExistingName(jsonNode, sellFieldName));
        stockExchangeStats.setPriceLow(extractNumberForExistingName(jsonNode, lowFieldName));
        stockExchangeStats.setPriceHigh(extractNumberForExistingName(jsonNode, highFieldName));
        stockExchangeStats.setVolume(extractNumberForExistingName(jsonNode, volumeFieldName));
        stockExchangeStats.setDate(LocalDateTime.now());
        stockExchangeStats.setStockExchange(this);
        stockExchangeStats.setCurrencyPairId(currencyPairId);
        return stockExchangeStats;
    }

    private BigDecimal extractNumberForExistingName(JsonNode jsonNode, String fieldName) {
        return fieldName == null ? null : BigDecimalProcessingUtil.parseNonePoint(jsonNode.get(fieldName).asText());
    }

    @Override
    public String toString() {
        return "StockExchange{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", availableCurrencyPairs=" + availableCurrencyPairs +
                '}';
    }
}