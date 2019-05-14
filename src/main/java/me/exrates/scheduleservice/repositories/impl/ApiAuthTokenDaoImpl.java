package me.exrates.scheduleservice.repositories.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.repositories.ApiAuthTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Log4j2(topic = "Dao_layer_log")
@Repository
public class ApiAuthTokenDaoImpl implements ApiAuthTokenDao {

    private final NamedParameterJdbcOperations namedParameterJdbcTemplate;

    @Autowired
    public ApiAuthTokenDaoImpl(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public int deleteAllExpired() {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE expired_at < now()";

        return namedParameterJdbcTemplate.update(sql, Collections.emptyMap());
    }
}