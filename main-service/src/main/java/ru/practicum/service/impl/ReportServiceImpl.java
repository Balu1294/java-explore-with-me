package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.enums.ReportStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Report;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.ReportRepository;
import ru.practicum.service.ReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

    @Override
    public void addReport(Integer commentId) {
        Comment comment = checkComment(commentId);
        Report report = new Report();
        report.setCreated(now());
        report.setStatus(ReportStatus.NEW);
        report.setComment(comment);
        reportRepository.save(report);
    }

    @Override
    public void deleteReport(Integer reportId) {
        reportRepository.findById(reportId).orElseThrow(() -> new NotFoundException(
                String.format("Жалоба c id=%d  не найдена", reportId)));
        reportRepository.deleteById(reportId);
    }

    @Override
    public List<Integer> getListReportsByStatus(ReportStatus status) {
        List<Report> reports = reportRepository.findAllByStatus(status);
        List<Integer> reportIds = new ArrayList<>();
        if (reports != null) {
            reportIds = reports.stream().map(Report::getId).collect(Collectors.toList());
        }
        return reportIds;
    }


    private Comment checkComment(Integer id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Комментарий c id=%d  не найден", id)));
    }
}