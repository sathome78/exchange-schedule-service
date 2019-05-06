package me.exrates.scheduleservice.jobs;

import me.exrates.scheduleservice.services.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class MerchantCommissionsLimitsJob {

    private final MerchantService merchantService;

    @Autowired
    public MerchantCommissionsLimitsJob(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @Scheduled(cron = "${scheduled.update.withdraw-commissions-limits}")
    public void update() {
        merchantService.updateMerchantCommissionsLimits();
    }
}