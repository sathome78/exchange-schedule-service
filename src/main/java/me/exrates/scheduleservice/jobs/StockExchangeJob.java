package me.exrates.scheduleservice.jobs;

import me.exrates.scheduleservice.services.StockExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class StockExchangeJob {

    private final StockExchangeService stockExchangeService;

    @Autowired
    public StockExchangeJob(StockExchangeService stockExchangeService) {
        this.stockExchangeService = stockExchangeService;
    }

    @Scheduled(cron = "${scheduled.update.stock-exchange}")
    public void retrieveCurrencies() {
        stockExchangeService.retrieveCurrencies();
    }
}