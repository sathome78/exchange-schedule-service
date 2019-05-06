package me.exrates.scheduleservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.BalanceDto;
import me.exrates.scheduleservice.models.dto.CurrencyDto;
import me.exrates.scheduleservice.models.dto.ExternalWalletBalancesDto;
import me.exrates.scheduleservice.models.dto.InternalWalletBalancesDto;
import me.exrates.scheduleservice.models.dto.RateDto;
import me.exrates.scheduleservice.repositories.WalletDao;
import me.exrates.scheduleservice.services.CurrencyService;
import me.exrates.scheduleservice.services.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Log4j2(topic = "Service_layer_log")
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private final WalletDao walletDao;
    private final CurrencyService currencyService;

    @Autowired
    public WalletServiceImpl(WalletDao walletDao, CurrencyService currencyService) {
        this.walletDao = walletDao;
        this.currencyService = currencyService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExternalWalletBalancesDto> getExternalWalletBalances() {
        return walletDao.getExternalMainWalletBalances();
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        return walletDao.getInternalWalletBalances();
    }

    @Override
    public void updateExternalMainWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating external main wallets start...");

        List<CurrencyDto> currencies = currencyService.getAllCurrencies();

        final Map<String, RateDto> rates = currencyService.getRates();
        final Map<String, BalanceDto> balances = currencyService.getBalances();
        final Map<String, ExternalWalletBalancesDto> mainBalancesMap = walletDao.getExternalMainWalletBalances()
                .stream()
                .collect(toMap(
                        ExternalWalletBalancesDto::getCurrencyName,
                        Function.identity()
                ));

        if (rates.isEmpty() || balances.isEmpty() || mainBalancesMap.isEmpty()) {
            log.info("Exchange or wallet api did not return any data");
            return;
        }

        for (CurrencyDto currency : currencies) {
            final String currencyName = currency.getName();

            RateDto rateDto = rates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));
            BalanceDto balanceDto = balances.getOrDefault(currencyName, BalanceDto.zeroBalance(currencyName));

            BigDecimal usdRate = rateDto.getUsdRate();
            BigDecimal btcRate = rateDto.getBtcRate();

            BigDecimal mainBalance = balanceDto.getBalance();
            LocalDateTime lastBalanceUpdate = balanceDto.getLastUpdatedAt();

            ExternalWalletBalancesDto exWallet = mainBalancesMap.get(currencyName);

            if (isNull(exWallet)) {
                continue;
            }
            ExternalWalletBalancesDto.Builder builder = exWallet.toBuilder()
                    .usdRate(usdRate)
                    .btcRate(btcRate)
                    .mainBalance(mainBalance);

            if (nonNull(lastBalanceUpdate)) {
                builder.lastUpdatedDate(lastBalanceUpdate);
            }
            exWallet = builder.build();
            walletDao.updateExternalMainWalletBalances(exWallet);
        }
        log.info("Process of updating external main wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void updateExternalReservedWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating external reserved wallets start...");

        final Map<String, BigDecimal> reservedBalances = currencyService.getCurrencyReservedBalances();

        if (reservedBalances.isEmpty()) {
            log.info("Wallet api did not return any data");
            return;
        }

        for (Map.Entry<String, BigDecimal> entry : reservedBalances.entrySet()) {
            final String compositeKey = entry.getKey();
            final BigDecimal balance = entry.getValue();

            String[] data = compositeKey.split("\\|\\|");
            final String currencySymbol = data[0];
            final String walletAddress = data[1];
            final LocalDateTime lastReservedBalanceUpdate = StringUtils.isNotEmpty(data[3])
                    ? LocalDateTime.parse(data[3], FORMATTER)
                    : null;

            CurrencyDto currency = currencyService.findByName(currencySymbol);
            if (isNull(currency)) {
                return;
            }

            walletDao.updateExternalReservedWalletBalances(currency.getId(), walletAddress, balance, lastReservedBalanceUpdate);
        }
        log.info("Process of updating external reserved wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void updateInternalWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating internal wallets start...");

        List<CurrencyDto> currencies = currencyService.getAllCurrencies();

        final Map<String, RateDto> rates = currencyService.getRates();
        final Map<String, List<InternalWalletBalancesDto>> balances = this.getWalletBalances()
                .stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        if (rates.isEmpty() || balances.isEmpty()) {
            log.info("Exchange or wallet api did not return any data");
            return;
        }

        for (CurrencyDto currency : currencies) {
            final String currencyName = currency.getName();

            RateDto rateDto = rates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));
            List<InternalWalletBalancesDto> balancesByRoles = balances.get(currencyName);

            if (isNull(balancesByRoles)) {
                continue;
            }
            final BigDecimal usdRate = rateDto.getUsdRate();
            final BigDecimal btcRate = rateDto.getBtcRate();

            for (InternalWalletBalancesDto balance : balancesByRoles) {
                balance = balance.toBuilder()
                        .usdRate(usdRate)
                        .btcRate(btcRate)
                        .build();
                walletDao.updateInternalWalletBalances(balance);
            }
        }
        log.info("Process of updating internal wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        return walletDao.getWalletBalances();
    }
}