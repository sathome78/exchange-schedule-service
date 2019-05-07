package me.exrates.scheduleservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.scheduleservice.utils.BigDecimalProcessingUtil;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinmarketApiDto {

    private Integer currencyPairId;
    private String currency_pair_name;
    private BigDecimal first;
    private BigDecimal last;
    private BigDecimal lowestAsk;
    private BigDecimal highestBid;
    private BigDecimal percentChange;
    private BigDecimal baseVolume;
    private BigDecimal quoteVolume;
    private Integer isFrozen;
    private BigDecimal high24hr;
    private BigDecimal low24hr;

    public CoinmarketApiDto(CurrencyPairDto currencyPair) {
        this.currency_pair_name = currencyPair.getName();
    }

    @Override
    public String toString() {
        return '"' + currency_pair_name.replace('/', '_') + "\":" +
                "{\"last\":" + BigDecimalProcessingUtil.formatNonePointQuoted(last, true) +
                ", \"lowestAsk\":" + BigDecimalProcessingUtil.formatNonePointQuoted(lowestAsk, true) +
                ", \"highestBid\":" + BigDecimalProcessingUtil.formatNonePointQuoted(highestBid, true) +
                ", \"percentChange\":" + BigDecimalProcessingUtil.formatNonePointQuoted(percentChange, true) +
                ", \"baseVolume\":" + BigDecimalProcessingUtil.formatNonePointQuoted(baseVolume, true) +
                ", \"quoteVolume\":" + BigDecimalProcessingUtil.formatNonePointQuoted(quoteVolume, true) +
                ", \"isFrozen\":" + '"' + isFrozen + '"' +
                ", \"high24hr\":" + BigDecimalProcessingUtil.formatNonePointQuoted(high24hr, true) +
                ", \"low24hr\":" + BigDecimalProcessingUtil.formatNonePointQuoted(low24hr, true) +
                '}';
    }
}
