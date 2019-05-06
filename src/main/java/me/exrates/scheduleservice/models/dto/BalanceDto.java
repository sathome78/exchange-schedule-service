package me.exrates.scheduleservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {

    private String currencyName;
    private BigDecimal balance;
    private LocalDateTime lastUpdatedAt;

    public static BalanceDto zeroBalance(String currencyName) {
        return BalanceDto.builder()
                .currencyName(currencyName)
                .balance(BigDecimal.ZERO)
                .lastUpdatedAt(null)
                .build();
    }
}