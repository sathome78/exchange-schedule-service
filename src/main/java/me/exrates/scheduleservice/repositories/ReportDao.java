package me.exrates.scheduleservice.repositories;

public interface ReportDao {

    void addNewBalancesReportObject(byte[] zippedBytes, String fileName);

    void addNewInOutReportObject(byte[] zippedBytes, String fileName);
}