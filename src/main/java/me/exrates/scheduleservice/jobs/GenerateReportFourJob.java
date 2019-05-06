package me.exrates.scheduleservice.jobs;

import me.exrates.scheduleservice.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class GenerateReportFourJob {

    private final ReportService reportService;

    @Autowired
    public GenerateReportFourJob(ReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "${scheduled.update.report}")
    public void update() {
        reportService.generateWalletBalancesReportObject();
    }
}
