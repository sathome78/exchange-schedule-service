package me.exrates.scheduleservice.repositories;

import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;

import java.util.List;

public interface StockExchangeDao {

    void saveStockExchangeStatsList(List<StockExchangeStatsDto> stockExchangeRates);

    List<StockExchangeDto> findAllActive();

    List<StockExchangeStatsDto> getStockExchangeStatistics(Integer currencyPairId);
}