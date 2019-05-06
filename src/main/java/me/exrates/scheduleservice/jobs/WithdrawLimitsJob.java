package me.exrates.scheduleservice.jobs;

import me.exrates.scheduleservice.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class WithdrawLimitsJob {

    private final CurrencyService currencyService;

    @Autowired
    public WithdrawLimitsJob(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(cron = "${scheduled.update.withdraw-limits}")
    public void update() {
        currencyService.updateWithdrawLimits();
    }
}