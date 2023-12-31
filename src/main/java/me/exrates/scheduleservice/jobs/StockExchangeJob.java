package me.exrates.scheduleservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.services.StockExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Log4j2(topic = "Cron_job_layer_log")
@Component
public class StockExchangeJob {

    private final StockExchangeService stockExchangeService;

    @Autowired
    public StockExchangeJob(StockExchangeService stockExchangeService) {
        this.stockExchangeService = stockExchangeService;
    }

    @Scheduled(cron = "${scheduled.update.stock-exchange}")
    public void retrieveCurrencies() {
        try {
            stockExchangeService.retrieveCurrencies();
        } catch (Exception ex) {
            log.error("--> In processing 'StockExchangeJob' occurred error", ex);
        }
    }
}