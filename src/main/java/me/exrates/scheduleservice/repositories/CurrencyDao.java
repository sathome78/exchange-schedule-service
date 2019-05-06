package me.exrates.scheduleservice.repositories;

import me.exrates.scheduleservice.models.dto.BalanceDto;
import me.exrates.scheduleservice.models.dto.CurrencyDto;
import me.exrates.scheduleservice.models.dto.CurrencyLimitDto;
import me.exrates.scheduleservice.models.dto.RateDto;

import java.util.List;

public interface CurrencyDao {

    CurrencyDto findByName(String name);

    List<CurrencyDto> getAllCurrencies();

    void updateCurrencyExchangeRates(List<RateDto> rates);

    void updateCurrencyBalances(List<BalanceDto> balances);

    List<RateDto> getCurrencyRates();

    List<BalanceDto> getCurrencyBalances();

    List<CurrencyDto> findAllCurrenciesWithHidden();

    List<CurrencyLimitDto> getAllCurrencyLimits();

    void updateWithdrawLimits(List<CurrencyLimitDto> currencyLimits);
}