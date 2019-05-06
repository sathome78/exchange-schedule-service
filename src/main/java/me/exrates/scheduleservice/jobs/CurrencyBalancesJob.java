package me.exrates.scheduleservice.jobs;

import me.exrates.scheduleservice.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class CurrencyBalancesJob {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyBalancesJob(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1000)
    public void update() {
        currencyService.updateCurrencyBalances();
    }
}