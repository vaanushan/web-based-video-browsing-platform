package com.sliit.videobrowsing.admin.repository;

import com.sliit.videobrowsing.admin.entity.Report;
import com.sliit.videobrowsing.admin.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatus(ReportStatus status);
    List<Report> findByVideoId(Long videoId);
}
