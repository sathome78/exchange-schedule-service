package me.exrates.scheduleservice.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.scheduleservice.models.enums.CurrencyPairType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyPairDto implements Serializable {
    private int id;
    private String name;
    private CurrencyDto currency1;
    private CurrencyDto currency2;
    private String market;
    private String marketName;
    private CurrencyPairType pairType;
    private boolean hidden;
    private boolean permittedLink;

    public CurrencyPairDto(CurrencyDto currency1, CurrencyDto currency2) {
        this.currency1 = currency1;
        this.currency2 = currency2;
    }

    public CurrencyPairDto(String currencyPairName) {
        this.name = currencyPairName;
    }

    public CurrencyDto getAnotherCurrency(CurrencyDto currency) {
        return currency.equals(currency1) ? currency2 : currency1;
    }
}