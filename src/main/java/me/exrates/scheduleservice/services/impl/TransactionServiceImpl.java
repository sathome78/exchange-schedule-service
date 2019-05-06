package me.exrates.scheduleservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.models.dto.InOutReportDto;
import me.exrates.scheduleservice.models.enums.UserRole;
import me.exrates.scheduleservice.repositories.TransactionDao;
import me.exrates.scheduleservice.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2(topic = "Service_layer_log")
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;

    @Autowired
    public TransactionServiceImpl(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Transactional(readOnly = true)
    @Override
    public List<InOutReportDto> getInOutSummaryByPeriodAndRoles(LocalDateTime startTime,
                                                                LocalDateTime endTime,
                                                                List<UserRole> userRoles) {
        return transactionDao.getInOutSummaryByPeriodAndRoles(startTime, endTime, userRoles);
    }
}