package me.exrates.scheduleservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Log4j2(topic = "Cron_job_layer_log")
@Component
public class ExternalMainWalletBalancesJob {

    private final WalletService walletService;

    @Autowired
    public ExternalMainWalletBalancesJob(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(cron = "${scheduled.update.external-balances}")
    public void update() {
        try {
            walletService.updateExternalMainWalletBalances();
        } catch (Exception ex) {
            log.error("--> In processing 'ExternalMainWalletBalancesJob' occurred error", ex);
        }
    }
}