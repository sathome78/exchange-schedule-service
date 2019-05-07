package me.exrates.scheduleservice.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.exceptions.WalletsApiException;
import me.exrates.scheduleservice.models.dto.BalanceDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Log4j2(topic = "Service_layer_log")
@Component
public class WalletsApi {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private final String url;
    private final String username;
    private final String password;

    private final RestTemplate restTemplate;

    @Autowired
    public WalletsApi(@Value("${api.wallets.url}") String url,
                      @Value("${api.wallets.username}") String username,
                      @Value("${api.wallets.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.restTemplate = new RestTemplate();
    }

    public List<BalanceDto> getBalancesFromApi() {
        HttpEntity<WalletsData[]> requestEntity = new HttpEntity<>(getAuthHeaders());

        ResponseEntity<WalletsData[]> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, WalletsData[].class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            log.warn("Wallet service did not return valid data: server not available");
            return Collections.emptyList();
        }
        WalletsData[] body = responseEntity.getBody();
        return nonNull(body) && body.length != 0
                ? Arrays.stream(body)
                .map(wallet -> BalanceDto.builder()
                        .currencyName(wallet.name)
                        .balance(new BigDecimal(wallet.currentAmount.replace(" ", "")))
                        .lastUpdatedAt(StringUtils.isNotEmpty(wallet.date) ? LocalDateTime.parse(wallet.date, FORMATTER) : null)
                        .build())
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    public Map<String, BigDecimal> getReservedBalancesFromApi() {
        HttpEntity<ReservedWalletsData> requestEntity = new HttpEntity<>(getAuthHeaders());

        ResponseEntity<ReservedWalletsData> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url + "/balance/reserved", HttpMethod.GET, requestEntity, ReservedWalletsData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            log.warn("Wallet service did not return valid data: server not available");
            return Collections.emptyMap();
        }
        ReservedWalletsData body = responseEntity.getBody();
        return nonNull(body) && nonNull(body.balances) && body.balances.size() != 0
                ? body.balances
                : Collections.emptyMap();
    }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(this.username, this.password);
        return headers;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WalletsData {

        String name;
        String currentAmount;
        String date;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ReservedWalletsData {

        Map<String, BigDecimal> balances = Maps.newTreeMap();

        @JsonAnySetter
        void setRates(String key, BigDecimal value) {
            balances.put(key, value);
        }
    }
}