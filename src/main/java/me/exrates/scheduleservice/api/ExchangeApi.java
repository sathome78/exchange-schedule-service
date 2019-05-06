package me.exrates.scheduleservice.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.exceptions.ExchangeApiException;
import me.exrates.scheduleservice.models.dto.RateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Log4j2
@Component
public class ExchangeApi {

    private final String url;
    private final String username;
    private final String password;

    private final RestTemplate restTemplate;

    @Autowired
    public ExchangeApi(@Value("${api.exchange.url}") String url,
                       @Value("${api.exchange.username}") String username,
                       @Value("${api.exchange.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.restTemplate = new RestTemplate();
    }

    public List<RateDto> getRatesFromApi() {
        HttpEntity<ExchangeData> requestEntity = new HttpEntity<>(getAuthHeaders());

        ResponseEntity<ExchangeData> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url + "/all", HttpMethod.GET, requestEntity, ExchangeData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ExchangeApiException("Exchange server is not available");
            }
        } catch (Exception ex) {
            log.warn("Exchange service did not return valid data: server not available");
            return Collections.emptyList();
        }
        ExchangeData body = responseEntity.getBody();
        return nonNull(body) && nonNull(body.rates) && !body.rates.isEmpty()
                ? body.rates.entrySet().stream()
                .map(entry -> RateDto.builder()
                        .currencyName(entry.getKey())
                        .usdRate(BigDecimal.valueOf(entry.getValue().usdRate))
                        .btcRate(BigDecimal.valueOf(entry.getValue().btcRate))
                        .build())
                .collect(toList())
                : Collections.emptyList();
    }

    public Map<String, BigDecimal> getRatesByCurrencyType(String type) {
        HttpEntity<ExchangeData> requestEntity = new HttpEntity<>(getAuthHeaders());

        ResponseEntity<ExchangeData> responseEntity;
        try {
            responseEntity = restTemplate.exchange(String.format(url + "/type/%s", type), HttpMethod.GET, requestEntity, ExchangeData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ExchangeApiException("Exchange server is not available");
            }
        } catch (Exception ex) {
            log.warn("Exchange service did not return valid data: server not available");
            return Collections.emptyMap();
        }
        ExchangeData body = responseEntity.getBody();
        return nonNull(body) && nonNull(body.rates) && !body.rates.isEmpty()
                ? body.rates.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> BigDecimal.valueOf(entry.getValue().usdRate)))
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
    private static class ExchangeData {

        Map<String, Rates> rates = Maps.newTreeMap();

        @JsonAnySetter
        void setRates(String key, Rates value) {
            rates.put(key, value);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Rates {

        @JsonProperty("usd_rate")
        double usdRate;
        @JsonProperty("btc_rate")
        double btcRate;
    }
}