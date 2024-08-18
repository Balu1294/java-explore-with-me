package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.ReportDto;
import ru.practicum.model.Report;

@UtilityClass
public class ReportMapper {
    public static ReportDto toReportDto(Report report) {
        return ReportDto.builder()
                .commentDto(CommentMapper.toCommentDto(report.getComment()))
                .build();
    }

    public static Report toReport(Long commentId, ReportDto reportDto) {
        return Report.builder()
                .comment(CommentMapper.toComment(reportDto.getCommentDto()))
                .build();
    }
}
