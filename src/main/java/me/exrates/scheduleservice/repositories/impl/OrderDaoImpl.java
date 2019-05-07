package me.exrates.scheduleservice.repositories.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.CoinmarketApiDto;
import me.exrates.scheduleservice.models.enums.ActionType;
import me.exrates.scheduleservice.repositories.OrderDao;
import me.exrates.scheduleservice.utils.BigDecimalProcessingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Log4j2(topic = "Dao_layer_log")
@Repository
public class OrderDaoImpl implements OrderDao {

    private final NamedParameterJdbcOperations masterJdbcTemplate;

    @Autowired
    public OrderDaoImpl(@Qualifier("masterTemplate") NamedParameterJdbcOperations masterJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
    }

    @Override
    public List<CoinmarketApiDto> getCoinmarketData(String currencyPairName) {
        String s = "{call GET_COINMARKETCAP_STATISTICS('" + currencyPairName + "')}";
        return masterJdbcTemplate.execute(s, ps -> {
            ResultSet rs = ps.executeQuery();
            List<CoinmarketApiDto> list = new ArrayList();
            while (rs.next()) {
                CoinmarketApiDto coinmarketApiDto = new CoinmarketApiDto();
                coinmarketApiDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
                coinmarketApiDto.setCurrency_pair_name(rs.getString("currency_pair_name"));
                coinmarketApiDto.setFirst(rs.getBigDecimal("first"));
                coinmarketApiDto.setLast(rs.getBigDecimal("last"));
                coinmarketApiDto.setLowestAsk(rs.getBigDecimal("lowestAsk"));
                coinmarketApiDto.setHighestBid(rs.getBigDecimal("highestBid"));
                coinmarketApiDto.setPercentChange(BigDecimalProcessingUtil.doAction(coinmarketApiDto.getFirst(), coinmarketApiDto.getLast(), ActionType.PERCENT_GROWTH));
                coinmarketApiDto.setBaseVolume(rs.getBigDecimal("baseVolume"));
                coinmarketApiDto.setQuoteVolume(rs.getBigDecimal("quoteVolume"));
                coinmarketApiDto.setIsFrozen(rs.getInt("isFrozen"));
                coinmarketApiDto.setHigh24hr(rs.getBigDecimal("high24hr"));
                coinmarketApiDto.setLow24hr(rs.getBigDecimal("low24hr"));
                list.add(coinmarketApiDto);
            }
            rs.close();
            return list;
        });
    }
}