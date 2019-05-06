package me.exrates.scheduleservice.repositories.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.MerchantCurrencyOptionsDto;
import me.exrates.scheduleservice.repositories.MerchantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Log4j2(topic = "Dao_layer_log")
@Repository
public class MerchantDaoImpl implements MerchantDao {

    private final NamedParameterJdbcOperations slaveJdbcTemplate;
    private final JdbcOperations jdbcTemplate;

    @Autowired
    public MerchantDaoImpl(@Qualifier(value = "slaveTemplate") NamedParameterJdbcOperations slaveJdbcTemplate,
                           @Qualifier(value = "jMasterTemplate") JdbcOperations jdbcTemplate) {
        this.slaveJdbcTemplate = slaveJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MerchantCurrencyOptionsDto> getAllMerchantCommissionsLimits() {
        String sql = "SELECT " +
                "MERCHANT_CURRENCY.merchant_id, " +
                "MERCHANT_CURRENCY.currency_id, " +
                "CURRENCY.name AS currency_name, " +
                "MERCHANT_CURRENCY.merchant_fixed_commission, " +
                "MERCHANT_CURRENCY.merchant_fixed_commission_usd, " +
                "MERCHANT_CURRENCY.usd_rate, " +
                "MERCHANT_CURRENCY.recalculate_to_usd " +
                "FROM MERCHANT_CURRENCY " +
                "JOIN CURRENCY ON MERCHANT_CURRENCY.currency_id = CURRENCY.id";

        return slaveJdbcTemplate.query(sql, (rs, row) -> MerchantCurrencyOptionsDto.builder()
                .merchantId(rs.getInt("merchant_id"))
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .minFixedCommission(rs.getBigDecimal("merchant_fixed_commission"))
                .minFixedCommissionUsdRate(rs.getBigDecimal("merchant_fixed_commission_usd"))
                .currencyUsdRate(rs.getBigDecimal("usd_rate"))
                .recalculateToUsd(rs.getBoolean("recalculate_to_usd"))
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateMerchantCommissionsLimits(List<MerchantCurrencyOptionsDto> merchantCommissionsLimits) {
        String sql = "UPDATE MERCHANT_CURRENCY " +
                "SET merchant_fixed_commission = ?, " +
                "merchant_fixed_commission_usd = ?, " +
                "usd_rate = ? " +
                "WHERE merchant_id = ? AND currency_id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MerchantCurrencyOptionsDto dto = merchantCommissionsLimits.get(i);
                ps.setBigDecimal(1, dto.getMinFixedCommission());
                ps.setBigDecimal(2, dto.getMinFixedCommissionUsdRate());
                ps.setBigDecimal(3, dto.getCurrencyUsdRate());
                ps.setInt(4, dto.getMerchantId());
                ps.setInt(5, dto.getCurrencyId());
            }

            @Override
            public int getBatchSize() {
                return merchantCommissionsLimits.size();
            }
        });
    }
}