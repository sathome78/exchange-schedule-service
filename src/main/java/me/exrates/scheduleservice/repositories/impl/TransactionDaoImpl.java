package me.exrates.scheduleservice.repositories.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.InOutReportDto;
import me.exrates.scheduleservice.models.enums.UserRole;
import me.exrates.scheduleservice.repositories.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Log4j2(topic = "Dao_layer_log")
@Repository
public class TransactionDaoImpl implements TransactionDao {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Autowired
    public TransactionDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<InOutReportDto> getInOutSummaryByPeriodAndRoles(LocalDateTime startTime,
                                                                LocalDateTime endTime,
                                                                List<UserRole> userRoles) {
        String sql = "SELECT MIN(cur.id) AS currency_id, " +
                "cur.name AS currency_name, " +
                "SUM(refill_count) AS input_count, " +
                "SUM(refill) AS input, " +
                "SUM(commission_refill) AS commission_in, " +
                "SUM(withdraw_count) AS output_count, " +
                "SUM(withdraw) AS output, " +
                "SUM(commission_withdraw) AS commission_out" +
                " FROM (" +
                " SELECT tx.currency_id, 1 AS refill_count, tx.amount AS refill, tx.commission_amount AS commission_refill, 0 AS withdraw_count, 0 AS withdraw, 0 AS commission_withdraw" +
                " FROM TRANSACTION tx" +
                " JOIN WALLET w ON w.id = tx.user_wallet_id" +
                " JOIN USER u ON u.id = w.user_id AND u.roleid IN (:user_roles)" +
                " WHERE tx.operation_type_id = 1" +
                " AND tx.source_type = 'REFILL'" +
                " AND tx.status_id = 1" +
                " AND tx.provided = 1" +
                " AND tx.datetime BETWEEN :start_time AND :end_time" +
                " UNION ALL" +
                " SELECT tx.currency_id, 0 AS refill_count, 0 AS refill, 0 AS commission_refill, 1 AS withdraw_count, tx.amount AS withdraw, tx.commission_amount AS commission_withdraw" +
                " FROM TRANSACTION tx" +
                " JOIN WALLET w ON w.id = tx.user_wallet_id" +
                " JOIN USER u ON u.id = w.user_id AND u.roleid IN (:user_roles)" +
                " WHERE tx.operation_type_id = 2" +
                " AND tx.source_type = 'WITHDRAW'" +
                " AND tx.status_id = 1" +
                " AND tx.provided = 1" +
                " AND tx.datetime BETWEEN :start_time AND :end_time" +
                ") AGGR" +
                " LEFT JOIN CURRENCY cur ON cur.id = AGGR.currency_id" +
                " GROUP BY currency_name" +
                " ORDER BY currency_id ASC";

        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("start_time", Timestamp.valueOf(startTime));
            put("end_time", Timestamp.valueOf(endTime));
            put("user_roles", userRoles.stream().map(UserRole::getRole).collect(toList()));
        }};

        try {
            return jdbcTemplate.query(sql, namedParameters, (resultSet, i) -> InOutReportDto.builder()
                    .orderNum(i + 1)
                    .currencyId(resultSet.getInt("currency_id"))
                    .currencyName(resultSet.getString("currency_name"))
                    .inputCount(resultSet.getInt("input_count"))
                    .input(resultSet.getBigDecimal("input"))
                    .inputCommission(resultSet.getBigDecimal("commission_in"))
                    .outputCount(resultSet.getInt("output_count"))
                    .output(resultSet.getBigDecimal("output"))
                    .outputCommission(resultSet.getBigDecimal("commission_out"))
                    .build());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }
}