package me.exrates.scheduleservice.services.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.BackDealIntervalDto;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import me.exrates.scheduleservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2(topic = "Service_layer_log")
//@Service(value = "Exrates")
public class ExratesRetrievalService implements StockExrateRetrievalService {

    private final OrderService orderService;

    @Autowired
    public ExratesRetrievalService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange) {
        return orderService.getCoinmarketDataForActivePairs("", new BackDealIntervalDto("24 HOUR"))
                .stream()
                .map(dto -> new StockExchangeStatsDto(dto, stockExchange))
                .collect(Collectors.toList());
    }
}