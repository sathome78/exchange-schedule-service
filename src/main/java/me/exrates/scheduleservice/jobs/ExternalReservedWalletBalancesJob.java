package me.exrates.scheduleservice.jobs;

import me.exrates.scheduleservice.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class ExternalReservedWalletBalancesJob {

    private final WalletService walletService;

    @Autowired
    public ExternalReservedWalletBalancesJob(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(cron = "${scheduled.update.external-balances}")
    public void update() {
        walletService.updateExternalReservedWalletBalances();
    }
}
