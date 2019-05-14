package me.exrates.scheduleservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Log4j2(topic = "Cron_job_layer_log")
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
