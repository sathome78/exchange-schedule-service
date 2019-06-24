package me.exrates.scheduleservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.scheduleservice.models.enums.OperationType;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyLimitDto {

    private int id;
    private CurrencyDto currency;
    private OperationType operationType;
    private BigDecimal minSum;
    private BigDecimal maxSum;
    private Integer maxDailyRequest;
    private BigDecimal currencyUsdRate;
    private BigDecimal minSumUsdRate;
    private boolean recalculateToUsd;
}