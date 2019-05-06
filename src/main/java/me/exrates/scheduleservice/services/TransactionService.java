package me.exrates.scheduleservice.services;

import me.exrates.scheduleservice.models.dto.InOutReportDto;
import me.exrates.scheduleservice.models.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    List<InOutReportDto> getInOutSummaryByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);
}
