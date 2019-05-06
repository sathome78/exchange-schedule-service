package me.exrates.scheduleservice.repositories.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.repositories.ReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Repository
public class ReportDaoImpl implements ReportDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ReportDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void addNewBalancesReportObject(byte[] zippedBytes, String fileName) {
        final String sql = "INSERT INTO BALANCES_REPORT (file_name, content, created_at) VALUES (:file_name, :content, CURRENT_TIMESTAMP)";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("file_name", fileName);
                put("content", zippedBytes);
            }
        };
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void addNewInOutReportObject(byte[] zippedBytes, String fileName) {
        final String sql = "INSERT INTO INPUT_OUTPUT_REPORT (file_name, content, created_at) VALUES (:file_name, :content, CURRENT_TIMESTAMP)";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("file_name", fileName);
                put("content", zippedBytes);
            }
        };
        namedParameterJdbcTemplate.update(sql, params);
    }
}