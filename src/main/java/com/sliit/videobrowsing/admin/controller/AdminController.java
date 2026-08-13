package com.sliit.videobrowsing.admin.controller;

import com.sliit.videobrowsing.admin.entity.Report;
import com.sliit.videobrowsing.admin.entity.ReportStatus;
import com.sliit.videobrowsing.admin.entity.ViolationRule;
import com.sliit.videobrowsing.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Owner: Vaanushan - Admin Panel module. All endpoints require ROLE_ADMIN (see SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/rules")
    public ResponseEntity<ViolationRule> defineRule(@RequestParam Long definedBy,
                                                      @RequestParam String type,
                                                      @RequestParam String description) {
        return ResponseEntity.ok(adminService.defineRule(definedBy, type, description));
    }

    @GetMapping("/rules")
    public ResponseEntity<List<ViolationRule>> getAllRules() {
        return ResponseEntity.ok(adminService.getAllRules());
    }

    @PostMapping("/reports")
    public ResponseEntity<Report> fileReport(@RequestParam Long reportedBy,
                                              @RequestParam Long videoId,
                                              @RequestParam String reason) {
        return ResponseEntity.ok(adminService.fileReport(reportedBy, videoId, reason));
    }

    @GetMapping("/reports/pending")
    public ResponseEntity<List<Report>> getPendingReports() {
        return ResponseEntity.ok(adminService.getPendingReports());
    }

    @PutMapping("/reports/{id}/resolve")
    public ResponseEntity<Report> resolveReport(@PathVariable Long id,
                                                 @RequestParam ReportStatus resolution,
                                                 @RequestParam(defaultValue = "false") boolean removeVideo) {
        return ResponseEntity.ok(adminService.resolveReport(id, resolution, removeVideo));
    }
}
