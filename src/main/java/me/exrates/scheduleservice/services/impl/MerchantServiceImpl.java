package me.exrates.scheduleservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.MerchantCurrencyOptionsDto;
import me.exrates.scheduleservice.models.dto.RateDto;
import me.exrates.scheduleservice.repositories.MerchantDao;
import me.exrates.scheduleservice.services.CurrencyService;
import me.exrates.scheduleservice.services.MerchantService;
import me.exrates.scheduleservice.utils.BigDecimalConverter;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static me.exrates.scheduleservice.utils.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class MerchantServiceImpl implements MerchantService {

    private final CurrencyService currencyService;
    private final MerchantDao merchantDao;
    private final BigDecimalConverter converter;

    @Autowired
    public MerchantServiceImpl(CurrencyService currencyService,
                               MerchantDao merchantDao,
                               BigDecimalConverter converter) {
        this.currencyService = currencyService;
        this.merchantDao = merchantDao;
        this.converter = converter;
    }

    @Override
    public void updateMerchantCommissionsLimits() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating merchant commissions limits start...");

        List<MerchantCurrencyOptionsDto> merchantCommissionsLimits = merchantDao.getAllMerchantCommissionsLimits();
        if (isEmpty(merchantCommissionsLimits)) {
            return;
        }

        final Map<String, RateDto> rates = currencyService.getRates();
        if (rates.isEmpty()) {
            log.info("Exchange api did not return data");
            return;
        }

        for (MerchantCurrencyOptionsDto merchantCommissionsLimit : merchantCommissionsLimits) {
            final String currencyName = merchantCommissionsLimit.getCurrencyName();
            final boolean recalculateToUsd = merchantCommissionsLimit.isRecalculateToUsd();
            BigDecimal minFixedCommissionUsdRate = merchantCommissionsLimit.getMinFixedCommissionUsdRate();
            BigDecimal minFixedCommission = merchantCommissionsLimit.getMinFixedCommission();

            RateDto rateDto = rates.get(currencyName);
            if (isNull(rateDto)) {
                continue;
            }

            final BigDecimal usdRate = rateDto.getUsdRate();
            if (usdRate.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            merchantCommissionsLimit.setCurrencyUsdRate(usdRate);

            if (recalculateToUsd) {
                minFixedCommission = converter.convert(minFixedCommissionUsdRate.divide(usdRate, RoundingMode.HALF_UP));
                merchantCommissionsLimit.setMinFixedCommission(minFixedCommission);
            } else {
                minFixedCommissionUsdRate = minFixedCommission.multiply(usdRate);
                merchantCommissionsLimit.setMinFixedCommissionUsdRate(minFixedCommissionUsdRate);
            }
        }
        merchantDao.updateMerchantCommissionsLimits(merchantCommissionsLimits);
        log.info("Process of updating merchant commissions limits end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}