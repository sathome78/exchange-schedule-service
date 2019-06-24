package me.exrates.scheduleservice.repositories;

import me.exrates.scheduleservice.models.dto.InOutReportDto;
import me.exrates.scheduleservice.models.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionDao {

    List<InOutReportDto> getInOutSummaryByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);
}