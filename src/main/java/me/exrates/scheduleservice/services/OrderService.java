package me.exrates.scheduleservice.services;

import me.exrates.scheduleservice.models.dto.BackDealIntervalDto;
import me.exrates.scheduleservice.models.dto.CoinmarketApiDto;

import java.util.List;

public interface OrderService {

    List<CoinmarketApiDto> getCoinmarketDataForActivePairs(String currencyPairName, BackDealIntervalDto backDealInterval);
}