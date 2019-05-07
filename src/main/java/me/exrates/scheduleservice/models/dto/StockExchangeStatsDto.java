package me.exrates.scheduleservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.scheduleservice.serializers.LocalDateTimeToLongSerializer;
import me.exrates.scheduleservice.serializers.StockExchangeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockExchangeStatsDto {

    @JsonIgnore
    private Long id;
    @JsonIgnore
    private Integer currencyPairId;

    @JsonProperty(value = "stockExchange")
    @JsonSerialize(using = StockExchangeSerializer.class)
    private StockExchangeDto stockExchange;

    @JsonProperty(value = "last")
    private BigDecimal priceLast;

    @JsonProperty(value = "buy")
    private BigDecimal priceBuy;

    @JsonProperty(value = "sell")
    private BigDecimal priceSell;

    @JsonProperty(value = "low")
    private BigDecimal priceLow;

    @JsonProperty(value = "high")
    private BigDecimal priceHigh;

    @JsonProperty(value = "volume")
    private BigDecimal volume;

    @JsonProperty(value = "timestamp")
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime date;

    public StockExchangeStatsDto(CoinmarketApiDto dto, StockExchangeDto stockExchange) {
        this.currencyPairId = dto.getCurrencyPairId();
        this.stockExchange = stockExchange;
        this.priceLast = dto.getLast();
        this.priceBuy = dto.getHighestBid();
        this.priceSell = dto.getLowestAsk();
        this.priceLow = dto.getHigh24hr();
        this.priceHigh = dto.getLow24hr();
        this.volume = dto.getBaseVolume();
        this.date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "StockExchangeStats{" +
                "id=" + id +
                ", currencyPairId=" + currencyPairId +
                ", stockExchange=" + stockExchange +
                ", priceLast=" + priceLast +
                ", priceBuy=" + priceBuy +
                ", priceSell=" + priceSell +
                ", priceLow=" + priceLow +
                ", priceHigh=" + priceHigh +
                ", volume=" + volume +
                ", date=" + date +
                '}';
    }
}