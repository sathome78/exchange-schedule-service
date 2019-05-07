package me.exrates.scheduleservice.services.stockExratesRetrieval;

import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;

import java.util.List;

public interface StockExrateRetrievalService {

    List<StockExchangeStatsDto> retrieveStats(StockExchangeDto stockExchange);
}