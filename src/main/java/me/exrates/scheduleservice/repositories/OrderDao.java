package me.exrates.scheduleservice.repositories;

import me.exrates.scheduleservice.models.dto.CoinmarketApiDto;

import java.util.List;

public interface OrderDao {

    List<CoinmarketApiDto> getCoinmarketData(String currencyPairName);
}