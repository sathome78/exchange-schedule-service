package me.exrates.scheduleservice.models.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.scheduleservice.serializers.LocalDateTimeDeserializer;
import me.exrates.scheduleservice.serializers.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExternalWalletBalancesDto {

    private Integer currencyId;
    private String currencyName;

    private BigDecimal usdRate;
    private BigDecimal btcRate;

    private BigDecimal mainBalance;
    private BigDecimal reservedBalance;

    private BigDecimal totalBalance;
    private BigDecimal totalBalanceUSD;
    private BigDecimal totalBalanceBTC;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdatedDate;

    private boolean signOfCertainty;


    public static ExternalWalletBalancesDto getZeroBalances(Integer currencyId, String currencyName) {
        return ExternalWalletBalancesDto
                .builder()
                .currencyId(currencyId)
                .currencyName(currencyName)
                .usdRate(BigDecimal.ZERO)
                .btcRate(BigDecimal.ZERO)
                .mainBalance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .totalBalance(BigDecimal.ZERO)
                .totalBalanceUSD(BigDecimal.ZERO)
                .totalBalanceBTC(BigDecimal.ZERO)
                .build();

    }


}
