package me.exrates.scheduleservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {

    @JsonIgnore
    private String currencyName;
    private BigDecimal usdRate;
    private BigDecimal btcRate;

    public static RateDto zeroRate(String currencyName) {
        return RateDto.builder()
                .currencyName(currencyName)
                .usdRate(BigDecimal.ZERO)
                .btcRate(BigDecimal.ZERO)
                .build();
    }
}