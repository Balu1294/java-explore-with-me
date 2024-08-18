package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.ReportStatus;
import ru.practicum.model.Report;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findAllByStatus(ReportStatus status);
}
