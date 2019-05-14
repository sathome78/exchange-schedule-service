package me.exrates.scheduleservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.repositories.ApiAuthTokenDao;
import me.exrates.scheduleservice.services.ApiAuthTokenService;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Log4j2(topic = "Service_layer_log")
@Service
@Transactional
public class ApiAuthTokenServiceImpl implements ApiAuthTokenService {

    private final ApiAuthTokenDao apiAuthTokenDao;

    @Autowired
    public ApiAuthTokenServiceImpl(ApiAuthTokenDao apiAuthTokenDao) {
        this.apiAuthTokenDao = apiAuthTokenDao;
    }

    @Override
    public void deleteAllExpired() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of deleting expired tokens start...");

        apiAuthTokenDao.deleteAllExpired();

        log.info("Process of deleting expired tokens end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}