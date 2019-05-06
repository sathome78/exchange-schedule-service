package me.exrates.scheduleservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.api.ExchangeApi;
import me.exrates.scheduleservice.api.WalletsApi;
import me.exrates.scheduleservice.models.dto.BalanceDto;
import me.exrates.scheduleservice.models.dto.CurrencyDto;
import me.exrates.scheduleservice.models.dto.CurrencyLimitDto;
import me.exrates.scheduleservice.models.dto.RateDto;
import me.exrates.scheduleservice.repositories.CurrencyDao;
import me.exrates.scheduleservice.services.CurrencyService;
import me.exrates.scheduleservice.utils.BigDecimalConverter;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static me.exrates.scheduleservice.configurations.CacheConfiguration.ALL_BALANCES_CACHE;
import static me.exrates.scheduleservice.configurations.CacheConfiguration.ALL_RATES_CACHE;
import static me.exrates.scheduleservice.configurations.CacheConfiguration.CURRENCY_CACHE;
import static me.exrates.scheduleservice.utils.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyDao currencyDao;
    private final ExchangeApi exchangeApi;
    private final WalletsApi walletsApi;
    private final BigDecimalConverter converter;
    private final Cache currencyCache;
    private final Cache ratesCache;
    private final Cache balancesCache;

    @Autowired
    public CurrencyServiceImpl(CurrencyDao currencyDao,
                               ExchangeApi exchangeApi,
                               WalletsApi walletsApi,
                               BigDecimalConverter converter,
                               @Qualifier(CURRENCY_CACHE) Cache currencyCache,
                               @Qualifier(ALL_RATES_CACHE) Cache ratesCache,
                               @Qualifier(ALL_BALANCES_CACHE) Cache balancesCache) {
        this.currencyDao = currencyDao;
        this.exchangeApi = exchangeApi;
        this.walletsApi = walletsApi;
        this.converter = converter;
        this.currencyCache = currencyCache;
        this.ratesCache = ratesCache;
        this.balancesCache = balancesCache;
    }

    @Transactional(readOnly = true)
    @Override
    public CurrencyDto findByName(String name) {
        return currencyCache.get(CURRENCY_CACHE, () -> currencyDao.findByName(name));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CurrencyDto> getAllCurrencies() {
        return currencyDao.getAllCurrencies();
    }

    @Override
    public Map<String, RateDto> getRates() {
        return Objects.requireNonNull(ratesCache.get(ALL_RATES_CACHE, this::getCurrencyRates)).stream()
                .collect(toMap(RateDto::getCurrencyName, Function.identity()));
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, BalanceDto> getBalances() {
        return Objects.requireNonNull(balancesCache.get(ALL_BALANCES_CACHE, this::getCurrencyBalances)).stream()
                .collect(toMap(BalanceDto::getCurrencyName, Function.identity()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<RateDto> getCurrencyRates() {
        return currencyDao.getCurrencyRates();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BalanceDto> getCurrencyBalances() {
        return currencyDao.getCurrencyBalances();
    }

    @Override
    public Map<String, BigDecimal> getCurrencyReservedBalances() {
        return walletsApi.getReservedBalancesFromApi();
    }

    @Override
    public void updateCurrencyExchangeRates() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating currency exchange rates start...");

        final List<RateDto> rates = exchangeApi.getRatesFromApi();
        if (isEmpty(rates)) {
            return;
        }
        currencyDao.updateCurrencyExchangeRates(rates);
        log.info("Process of updating currency exchange rates end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void updateCurrencyBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating currency balances start...");

        final List<BalanceDto> balances = walletsApi.getBalancesFromApi();
        if (isEmpty(balances)) {
            return;
        }
        currencyDao.updateCurrencyBalances(balances);
        log.info("Process of updating currency balances end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public List<CurrencyDto> findAllCurrenciesWithHidden() {
        return currencyDao.findAllCurrenciesWithHidden();
    }

    @Override
    public void updateWithdrawLimits() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating withdraw limits start...");

        List<CurrencyLimitDto> currencyLimits = currencyDao.getAllCurrencyLimits();
        if (isEmpty(currencyLimits)) {
            return;
        }

        final Map<String, RateDto> rates = this.getRates();
        if (rates.isEmpty()) {
            log.info("Exchange api did not return data");
            return;
        }

        for (CurrencyLimitDto currencyLimit : currencyLimits) {
            final String currencyName = currencyLimit.getCurrency().getName();
            final boolean recalculateToUsd = currencyLimit.isRecalculateToUsd();
            BigDecimal minSumUsdRate = currencyLimit.getMinSumUsdRate();
            BigDecimal minSum = currencyLimit.getMinSum();

            RateDto rateDto = rates.get(currencyName);
            if (isNull(rateDto)) {
                continue;
            }

            final BigDecimal usdRate = rateDto.getUsdRate();
            if (usdRate.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            currencyLimit.setCurrencyUsdRate(usdRate);

            if (recalculateToUsd) {
                minSum = converter.convert(minSumUsdRate.divide(usdRate, RoundingMode.HALF_UP));
                currencyLimit.setMinSum(minSum);
            } else {
                minSumUsdRate = minSum.multiply(usdRate);
                currencyLimit.setMinSumUsdRate(minSumUsdRate);
            }
        }
        currencyDao.updateWithdrawLimits(currencyLimits);

        log.info("Process of updating withdraw limits end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}