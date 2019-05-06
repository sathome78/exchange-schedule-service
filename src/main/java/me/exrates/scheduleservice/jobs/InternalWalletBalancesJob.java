package me.exrates.scheduleservice.jobs;

import me.exrates.scheduleservice.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class InternalWalletBalancesJob {

    private final WalletService walletService;

    @Autowired
    public InternalWalletBalancesJob(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(cron = "${scheduled.update.internal-balances}")
    public void update() {
        walletService.updateInternalWalletBalances();
    }
}
