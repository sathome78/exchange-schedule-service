package me.exrates.scheduleservice.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.CurrencyDto;
import me.exrates.scheduleservice.models.dto.ExternalWalletBalancesDto;
import me.exrates.scheduleservice.models.dto.InOutReportDto;
import me.exrates.scheduleservice.models.dto.InternalWalletBalancesDto;
import me.exrates.scheduleservice.models.dto.RateDto;
import me.exrates.scheduleservice.models.dto.WalletBalancesDto;
import me.exrates.scheduleservice.models.enums.UserRole;
import me.exrates.scheduleservice.repositories.ReportDao;
import me.exrates.scheduleservice.services.CurrencyService;
import me.exrates.scheduleservice.services.ReportService;
import me.exrates.scheduleservice.services.TransactionService;
import me.exrates.scheduleservice.services.WalletService;
import me.exrates.scheduleservice.utils.ZipUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static me.exrates.scheduleservice.ScheduleServiceConfiguration.JSON_MAPPER;

@Log4j2
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter FORMATTER_FOR_NAME = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");

    private final CurrencyService currencyService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final ReportDao reportDao;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportServiceImpl(CurrencyService currencyService,
                             WalletService walletService,
                             TransactionService transactionService,
                             ReportDao reportDao,
                             @Qualifier(JSON_MAPPER) ObjectMapper objectMapper) {
        this.currencyService = currencyService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.reportDao = reportDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public void generateInputOutputSummaryReportObject() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of generating report 2 object as byte array start...");

        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        final LocalDateTime startTime = now.minusHours(1);
        final LocalDateTime endTime = now.minusNanos(1);
        final List<UserRole> roles = Arrays.stream(UserRole.values()).collect(toList());

        List<InOutReportDto> inOut = transactionService.getInOutSummaryByPeriodAndRoles(startTime, endTime, roles);

        byte[] zippedBytes;
        try {
            byte[] inOutBytes = objectMapper.writeValueAsBytes(inOut);

            zippedBytes = ZipUtil.zip(inOutBytes);
        } catch (IOException ex) {
            log.warn("Problem with write in/out object to byte array", ex);
            return;
        }
        final String fileName = String.format("report_input_output_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME));

        reportDao.addNewInOutReportObject(zippedBytes, fileName);
        log.info("Process of generating report 2 object as byte array end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void generateWalletBalancesReportObject() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of generating report 4 object as byte array start...");

        List<CurrencyDto> currencies = currencyService.findAllCurrenciesWithHidden();

        final Map<String, ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances()
                .stream()
                .collect(Collectors.toMap(
                        ExternalWalletBalancesDto::getCurrencyName,
                        Function.identity()));
        final Map<String, List<InternalWalletBalancesDto>> internalWalletBalances = walletService.getInternalWalletBalances()
                .stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        final Map<String, RateDto> rates = currencyService.getRates();

        List<WalletBalancesDto> balances = currencies.stream()
                .map(currency ->
                        currency.isHidden()
                                ? WalletBalancesDto.buildForHiddenCurrency(currency.getId(), currency.getName())
                                : WalletBalancesDto.builder()
                                .currencyName(currency.getName())
                                .external(externalWalletBalances.get(currency.getName()))
                                .internals(internalWalletBalances.get(currency.getName()))
                                .rate(rates.get(currency.getName()))
                                .build())
                .filter(walletBalancesDto -> nonNull(walletBalancesDto.getExternal())
                        && nonNull(walletBalancesDto.getInternals())
                        && nonNull(walletBalancesDto.getRate()))
                .collect(toList());

        byte[] zippedBytes;
        try {
            byte[] balancesBytes = objectMapper.writeValueAsBytes(balances);

            zippedBytes = ZipUtil.zip(balancesBytes);
        } catch (IOException ex) {
            log.warn("Problem with write balances object to byte array", ex);
            return;
        }
        final String fileName = String.format("report_balances_%s", LocalDateTime.now().format(FORMATTER_FOR_NAME));

        reportDao.addNewBalancesReportObject(zippedBytes, fileName);
        log.info("Process of generating report 4 object as byte array end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}