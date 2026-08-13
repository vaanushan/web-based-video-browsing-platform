package com.sliit.videobrowsing.admin.service;

import com.sliit.videobrowsing.admin.entity.Report;
import com.sliit.videobrowsing.admin.entity.ReportStatus;
import com.sliit.videobrowsing.admin.entity.ViolationRule;
import com.sliit.videobrowsing.admin.repository.ReportRepository;
import com.sliit.videobrowsing.admin.repository.ViolationRuleRepository;
import com.sliit.videobrowsing.common.exception.ResourceNotFoundException;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.entity.VideoStatus;
import com.sliit.videobrowsing.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Owner: Vaanushan - Admin Panel module.
 * Coordinates moderation: reviewing reports, removing videos, defining violation rules.
 * Reuses VideoRepository from the Video Management module to actually take content down.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ViolationRuleRepository violationRuleRepository;
    private final ReportRepository reportRepository;
    private final VideoRepository videoRepository;

    public ViolationRule defineRule(Long definedBy, String type, String description) {
        ViolationRule rule = ViolationRule.builder()
                .definedBy(definedBy).type(type).description(description).build();
        return violationRuleRepository.save(rule);
    }

    public List<ViolationRule> getAllRules() {
        return violationRuleRepository.findAll();
    }

    public Report fileReport(Long reportedBy, Long videoId, String reason) {
        Report report = Report.builder()
                .reportedBy(reportedBy).videoId(videoId).reason(reason).build();
        return reportRepository.save(report);
    }

    public List<Report> getPendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING);
    }

    public Report resolveReport(Long reportId, ReportStatus resolution, boolean removeVideo) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + reportId));
        report.setStatus(resolution);
        reportRepository.save(report);

        if (removeVideo) {
            Video video = videoRepository.findById(report.getVideoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Video not found: " + report.getVideoId()));
            video.setStatus(VideoStatus.REMOVED);
            videoRepository.save(video);
        }
        return report;
    }
}
