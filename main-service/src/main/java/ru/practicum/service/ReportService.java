package ru.practicum.service;

import ru.practicum.enums.ReportStatus;

import java.util.List;

public interface ReportService {
    void addReport(Integer commentId);

    void deleteReport(Integer reportId);

    List<Integer> getListReportsByStatus(ReportStatus status);
}
