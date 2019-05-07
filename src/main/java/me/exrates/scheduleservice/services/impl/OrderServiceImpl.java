package me.exrates.scheduleservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.BackDealIntervalDto;
import me.exrates.scheduleservice.models.dto.CoinmarketApiDto;
import me.exrates.scheduleservice.repositories.OrderDao;
import me.exrates.scheduleservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2(topic = "Service_layer_log")
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public List<CoinmarketApiDto> getCoinmarketDataForActivePairs(String currencyPairName, BackDealIntervalDto backDealInterval) {
        return orderDao.getCoinmarketData(currencyPairName);
    }
}