package com.sliit.videobrowsing.admin.controller;

import com.sliit.videobrowsing.admin.entity.Report;
import com.sliit.videobrowsing.admin.entity.ReportStatus;
import com.sliit.videobrowsing.admin.entity.ViolationRule;
import com.sliit.videobrowsing.admin.service.AdminService;
import com.sliit.videobrowsing.user.entity.Role;
import com.sliit.videobrowsing.user.service.UserService;
import com.sliit.videobrowsing.video.entity.Video;
import com.sliit.videobrowsing.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Owner: Vaanushan V - Admin Panel module.
 * Admin dashboard with user management, reports, and violation rules.
 */
@Controller
@RequiredArgsConstructor
public class AdminPageController {

    private final AdminService adminService;
    private final UserService userService;
    private final VideoService videoService;

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAll());

        List<Map<String, Object>> reports = new ArrayList<>();
        for (Report report : adminService.getPendingReports()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", report.getId());
            row.put("videoId", report.getVideoId());
            row.put("reason", report.getReason());
            row.put("status", report.getStatus().name());
            row.put("reportedBy", resolveUserName(report.getReportedBy()));
            try {
                Video video = videoService.getById(report.getVideoId());
                row.put("videoTitle", video.getTitle());
            } catch (Exception e) {
                row.put("videoTitle", "Video #" + report.getVideoId());
            }
            reports.add(row);
        }
        model.addAttribute("reports", reports);

        List<Map<String, Object>> rules = new ArrayList<>();
        for (ViolationRule rule : adminService.getAllRules()) {
            Map<String, Object> row = new HashMap<>();
            row.put("type", rule.getType());
            row.put("description", rule.getDescription());
            rules.add(row);
        }
        model.addAttribute("rules", rules);

        // All videos for the Manage Videos tab
        List<Map<String, Object>> videos = new ArrayList<>();
        for (Video video : videoService.getAllPublished()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", video.getId());
            row.put("title", video.getTitle());
            row.put("status", video.getStatus().name());
            row.put("viewCount", video.getViewCount());
            row.put("uploaderName", resolveUserName(video.getUploaderId()));
            videos.add(row);
        }
        model.addAttribute("videos", videos);

        return "admin/admin-dashboard";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin#users";
    }

    @PostMapping("/admin/users/{id}/role")
    public String updateRole(@PathVariable Long id, @RequestParam Role role) {
        userService.updateRole(id, role);
        return "redirect:/admin#users";
    }

    @PostMapping("/admin/reports/{id}/resolve")
    public String resolveReport(@PathVariable Long id) {
        adminService.resolveReport(id, ReportStatus.REVIEWED, false);
        return "redirect:/admin#reports";
    }

    @PostMapping("/admin/videos/{videoId}/remove")
    public String removeVideo(@PathVariable Long videoId, @RequestParam Long reportId) {
        adminService.resolveReport(reportId, ReportStatus.ACTIONED, true);
        return "redirect:/admin#reports";
    }

    @PostMapping("/admin/videos/{videoId}/delete")
    public String deleteVideo(@PathVariable Long videoId, RedirectAttributes redirect) {
        try {
            videoService.delete(videoId);
            redirect.addFlashAttribute("videoDeleteSuccess", "Video #" + videoId + " has been permanently deleted.");
        } catch (Exception e) {
            redirect.addFlashAttribute("videoDeleteError", e.getMessage());
        }
        return "redirect:/admin#videos-manage";
    }

    @PostMapping("/admin/rules")
    public String addRule(@RequestParam String type,
                          @RequestParam String description,
                          Principal principal) {
        Long adminId = userService.getByEmail(principal.getName()).getId();
        adminService.defineRule(adminId, type, description);
        return "redirect:/admin#rules";
    }

    @PostMapping("/admin/users/create")
    public String createUser(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(defaultValue = "ADMIN") Role role,
                             RedirectAttributes redirect) {
        try {
            userService.createUser(name, email, password, role);
            redirect.addFlashAttribute("createSuccess", "Account created for " + email);
        } catch (Exception e) {
            redirect.addFlashAttribute("createError", e.getMessage());
        }
        return "redirect:/admin#users";
    }

    private String resolveUserName(Long userId) {
        try {
            return userService.getById(userId).getName();
        } catch (Exception e) {
            return "User #" + userId;
        }
    }
}
