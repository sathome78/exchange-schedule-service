package me.exrates.scheduleservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MerchantCurrencyOptionsDto {
    private Integer merchantId;
    private Integer currencyId;
    private String merchantName;
    private String currencyName;
    private BigDecimal inputCommission;
    private BigDecimal outputCommission;
    private BigDecimal transferCommission;
    private BigDecimal minFixedCommission;
    private BigDecimal minFixedCommissionUsdRate;
    private BigDecimal currencyUsdRate;
    private Boolean isRefillBlocked;
    private Boolean isWithdrawBlocked;
    private Boolean isTransferBlocked;
    private Boolean withdrawAutoEnabled;
    private Integer withdrawAutoDelaySeconds;
    private BigDecimal withdrawAutoThresholdAmount;
    private Boolean isMerchantCommissionSubtractedForWithdraw;
    private boolean recalculateToUsd;
}
