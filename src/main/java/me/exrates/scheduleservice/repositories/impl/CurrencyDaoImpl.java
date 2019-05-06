package me.exrates.scheduleservice.repositories.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.BalanceDto;
import me.exrates.scheduleservice.models.dto.CurrencyDto;
import me.exrates.scheduleservice.models.dto.CurrencyLimitDto;
import me.exrates.scheduleservice.models.dto.RateDto;
import me.exrates.scheduleservice.repositories.CurrencyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Log4j2(topic = "Dao_layer_log")
@Repository
public class CurrencyDaoImpl implements CurrencyDao {

    private final JdbcOperations jdbcTemplate;
    private final NamedParameterJdbcOperations masterJdbcTemplate;

    @Autowired
    public CurrencyDaoImpl(@Qualifier("jMasterTemplate") JdbcOperations jdbcTemplate,
                           @Qualifier("masterTemplate") NamedParameterJdbcOperations masterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.masterJdbcTemplate = masterJdbcTemplate;
    }

    @Override
    public CurrencyDto findByName(String name) {
        final String sql = "SELECT * FROM CURRENCY WHERE name = :name";

        try {
            return masterJdbcTemplate.queryForObject(sql, Collections.singletonMap("name", name), new BeanPropertyRowMapper<>(CurrencyDto.class));
        } catch (Exception ex) {
            log.warn("Failed to find currency for name " + name, ex);
            throw ex;
        }
    }

    @Override
    public List<CurrencyDto> getAllCurrencies() {
        String sql = "SELECT id, name FROM CURRENCY";

        return masterJdbcTemplate.query(sql, (rs, row) -> CurrencyDto.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build());
    }

    @Override
    public void updateCurrencyExchangeRates(List<RateDto> rates) {
        final String sql = "UPDATE CURRENT_CURRENCY_RATES " +
                "SET usd_rate = ?, btc_rate = ? " +
                "WHERE currency_name = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RateDto rateDto = rates.get(i);
                ps.setBigDecimal(1, rateDto.getUsdRate());
                ps.setBigDecimal(2, rateDto.getBtcRate());
                ps.setString(3, rateDto.getCurrencyName());
            }

            @Override
            public int getBatchSize() {
                return rates.size();
            }
        });
    }

    @Override
    public void updateCurrencyBalances(List<BalanceDto> balances) {
        final String sql = "UPDATE CURRENT_CURRENCY_BALANCES " +
                "SET balance = ?, last_updated_at = ? " +
                "WHERE currency_name = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                BalanceDto balanceDto = balances.get(i);
                ps.setBigDecimal(1, balanceDto.getBalance());
                ps.setTimestamp(2, Timestamp.valueOf(balanceDto.getLastUpdatedAt()));
                ps.setString(3, balanceDto.getCurrencyName());
            }

            @Override
            public int getBatchSize() {
                return balances.size();
            }
        });
    }

    @Override
    public List<RateDto> getCurrencyRates() {
        final String sql = "SELECT currency_name, usd_rate, btc_rate FROM CURRENT_CURRENCY_RATES";

        return masterJdbcTemplate.query(sql, (rs, row) -> RateDto.builder()
                .currencyName(rs.getString("currency_name"))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .build());
    }

    @Override
    public List<BalanceDto> getCurrencyBalances() {
        final String sql = "SELECT currency_name, balance, last_updated_at FROM CURRENT_CURRENCY_BALANCES";

        return masterJdbcTemplate.query(sql, (rs, row) -> BalanceDto.builder()
                .currencyName(rs.getString("currency_name"))
                .balance(rs.getBigDecimal("balance"))
                .lastUpdatedAt(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .build());
    }

    @Override
    public List<CurrencyDto> findAllCurrenciesWithHidden() {
        final String sql = "SELECT * FROM CURRENCY";
        return masterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CurrencyDto.class));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CurrencyLimitDto> getAllCurrencyLimits() {
        String sql = "SELECT DISTINCT " +
                "CURRENCY_LIMIT.id, " +
                "CURRENCY_LIMIT.currency_id, " +
                "CURRENCY.name, " +
                "CURRENCY_LIMIT.min_sum, " +
                "CURRENCY_LIMIT.min_sum_usd, " +
                "CURRENCY_LIMIT.usd_rate, " +
                "CURRENCY_LIMIT.recalculate_to_usd " +
                "FROM CURRENCY_LIMIT " +
                "JOIN CURRENCY ON CURRENCY_LIMIT.currency_id = CURRENCY.id";

        return masterJdbcTemplate.query(sql, (rs, row) -> {
            CurrencyDto currency = new CurrencyDto(rs.getInt("currency_id"));
            currency.setName(rs.getString("name"));

            return CurrencyLimitDto.builder()
                    .id(rs.getInt("id"))
                    .currency(currency)
                    .minSum(rs.getBigDecimal("min_sum"))
                    .minSumUsdRate(rs.getBigDecimal("min_sum_usd"))
                    .currencyUsdRate(rs.getBigDecimal("usd_rate"))
                    .recalculateToUsd(rs.getBoolean("recalculate_to_usd"))
                    .build();
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateWithdrawLimits(List<CurrencyLimitDto> currencyLimits) {
        String sql = "UPDATE CURRENCY_LIMIT " +
                "SET min_sum = ?, min_sum_usd = ?, usd_rate = ? " +
                "WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CurrencyLimitDto dto = currencyLimits.get(i);
                ps.setBigDecimal(1, dto.getMinSum());
                ps.setBigDecimal(2, dto.getMinSumUsdRate());
                ps.setBigDecimal(3, dto.getCurrencyUsdRate());
                ps.setInt(4, dto.getId());
            }

            @Override
            public int getBatchSize() {
                return currencyLimits.size();
            }
        });
    }
}