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
public class WithdrawLimitsJob {

    private final CurrencyService currencyService;

    @Autowired
    public WithdrawLimitsJob(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(cron = "${scheduled.update.withdraw-limits}")
    public void update() {
        try {
            currencyService.updateWithdrawLimits();
        } catch (Exception ex) {
            log.error("--> In processing 'WithdrawLimitsJob' occurred error", ex);
        }
    }
}