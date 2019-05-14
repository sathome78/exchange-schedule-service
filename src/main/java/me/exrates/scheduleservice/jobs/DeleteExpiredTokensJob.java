package me.exrates.scheduleservice.jobs;

import me.exrates.scheduleservice.services.ApiAuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class DeleteExpiredTokensJob {

    private final ApiAuthTokenService apiAuthTokenService;

    @Autowired
    public DeleteExpiredTokensJob(ApiAuthTokenService apiAuthTokenService) {
        this.apiAuthTokenService = apiAuthTokenService;
    }

    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 12 * 60 * 60 * 1000)
    public void delete() {
        apiAuthTokenService.deleteAllExpired();
    }
}