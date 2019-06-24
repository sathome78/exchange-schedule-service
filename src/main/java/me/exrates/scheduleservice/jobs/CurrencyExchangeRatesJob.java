package me.exrates.scheduleservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Log4j2(topic = "Cron_job_layer_log")
@Component
public class CurrencyExchangeRatesJob {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyExchangeRatesJob(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1000)
    public void update() {
        try {
            currencyService.updateCurrencyExchangeRates();
        } catch (Exception ex) {
            log.error("--> In processing 'CurrencyExchangeRatesJob' occurred error", ex);
        }
    }
}