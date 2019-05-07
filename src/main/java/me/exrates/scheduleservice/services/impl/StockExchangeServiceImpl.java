package me.exrates.scheduleservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import me.exrates.scheduleservice.repositories.StockExchangeDao;
import me.exrates.scheduleservice.services.StockExchangeService;
import me.exrates.scheduleservice.services.stockExratesRetrieval.StockExrateRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Log4j2(topic = "Service_layer_log")
@Service
@Transactional
public class StockExchangeServiceImpl implements StockExchangeService {

    private final Map<String, StockExrateRetrievalService> stockExrateRetrievalServices;
    private final StockExchangeDao stockExchangeDao;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private CompletionService<List<StockExchangeStatsDto>> completionService = new ExecutorCompletionService<>(executorService);

    @Autowired
    public StockExchangeServiceImpl(Map<String, StockExrateRetrievalService> stockExrateRetrievalServices,
                                    StockExchangeDao stockExchangeDao) {
        this.stockExrateRetrievalServices = stockExrateRetrievalServices;
        this.stockExchangeDao = stockExchangeDao;
    }

    @PostConstruct
    void init() {
        retrieveCurrencies();
    }

    @Override
    public void retrieveCurrencies() {
        log.debug("Start retrieving stock exchange statistics at: " + LocalDateTime.now());
        List<StockExchangeStatsDto> stockExchangeStatsList = new ArrayList<>();
        List<StockExchangeDto> activeExchanges = stockExchangeDao.findAllActive();
        int tasksSubmitted = 0;
        for (StockExchangeDto stockExchange : activeExchanges) {
            try {
                StockExrateRetrievalService retrievalService = stockExrateRetrievalServices.get(stockExchange.getName());
                if (retrievalService != null) {
                    completionService.submit(() -> retrievalService.retrieveStats(stockExchange));
                    tasksSubmitted++;
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }

        for (int i = 0; i < tasksSubmitted; i++) {
            try {
                Future<List<StockExchangeStatsDto>> nextResult = completionService.take();
                stockExchangeStatsList.addAll(nextResult.get());

            } catch (InterruptedException | ExecutionException e) {
                log.warn(e.getMessage());
            }
        }
        stockExchangeDao.saveStockExchangeStatsList(stockExchangeStatsList);
    }

    @Override
    public List<StockExchangeStatsDto> getStockExchangeStatistics(Integer currencyPairId) {
        return stockExchangeDao.getStockExchangeStatistics(currencyPairId);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }
}