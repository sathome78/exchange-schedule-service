package me.exrates.scheduleservice.services.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.CurrencyPairDto;
import me.exrates.scheduleservice.models.dto.StockExchangeDto;
import me.exrates.scheduleservice.models.dto.StockExchangeStatsDto;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.Objects.isNull;
import static me.exrates.scheduleservice.ScheduleServiceConfiguration.JSON_MAPPER;

@Log4j2(topic = "Service_layer_log")
@Service
public class ExchangeResponseProcessingServiceImpl implements ExchangeResponseProcessingService {

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    @Autowired
    public ExchangeResponseProcessingServiceImpl(@Qualifier(JSON_MAPPER) ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    @Override
    public String sendGetRequest(String url, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        params.forEach(builder::queryParam);
        return restTemplate.getForObject(builder.toUriString(), String.class);
    }

    @Override
    public String sendGetRequest(String url) {
        return sendGetRequest(url, Collections.emptyMap());
    }


    @Override
    public List<StockExchangeStatsDto> extractAllStatsFromMapNode(StockExchangeDto stockExchange, String jsonResponse, BiFunction<String, String, String> currencyPairTransformer) {
        List<StockExchangeStatsDto> stockExchangeStatsList = new ArrayList<>();
        JsonNode root = extractNode(jsonResponse);
        if (isNull(root)) {
            return Collections.emptyList();
        }
        Map<String, CurrencyPairDto> currencyPairs = stockExchange.getAliasedCurrencyPairs(currencyPairTransformer);
        currencyPairs.keySet().forEach(currencyPairName -> {
            JsonNode currencyPairNode = root.get(currencyPairName);
            if (currencyPairNode != null) {
                StockExchangeStatsDto stockExchangeStats = stockExchange.extractStatsFromNode(currencyPairNode,
                        currencyPairs.get(currencyPairName).getId());
                stockExchangeStatsList.add(stockExchangeStats);
            }
        });
        return stockExchangeStatsList;
    }

    @Override
    public JsonNode extractNode(String source, String... targetNodes) {
        JsonNode node;
        try {
            node = objectMapper.readTree(source);
            for (String nodeName : targetNodes) {
                node = node.get(nodeName);
            }
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
        return node;
    }

    @Override
    public List<StockExchangeStatsDto> extractAllStatsFromArrayNode(StockExchangeDto stockExchange, JsonNode targetNode, String currencyPairNameField,
                                                                    BiFunction<String, String, String> currencyPairTransformer) {
        List<StockExchangeStatsDto> stockExchangeStatsList = new ArrayList<>();
        Map<String, CurrencyPairDto> currencyPairs = stockExchange.getAliasedCurrencyPairs(currencyPairTransformer);
        targetNode.elements().forEachRemaining(elem -> {
            CurrencyPairDto currencyPair = currencyPairs.get(elem.get(currencyPairNameField).asText());
            if (currencyPair != null) {
                StockExchangeStatsDto stockExchangeStats = stockExchange.extractStatsFromNode(elem,
                        currencyPair.getId());
                stockExchangeStatsList.add(stockExchangeStats);
            }
        });
        return stockExchangeStatsList;
    }

    @Override
    public StockExchangeStatsDto extractStatsFromSingleNode(String jsonResponse, StockExchangeDto stockExchange, int currencyPairId) {
        JsonNode root = extractNode(jsonResponse);
        if (isNull(root)) {
            return null;
        }
        return stockExchange.extractStatsFromNode(root, currencyPairId);
    }
}