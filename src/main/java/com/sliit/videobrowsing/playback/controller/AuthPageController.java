package com.sliit.videobrowsing.playback.controller;

import com.sliit.videobrowsing.user.dto.AuthResponse;
import com.sliit.videobrowsing.user.dto.LoginRequest;
import com.sliit.videobrowsing.user.dto.RegisterRequest;
import com.sliit.videobrowsing.user.entity.Role;
import com.sliit.videobrowsing.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Owner: Maldeniya P.A.D.M.P - Playback & User Management module.
 *
 * Renders auth pages and handles form-based login/register (sets JWT cookie).
 */
@Controller
@RequiredArgsConstructor
public class AuthPageController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "playback/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              HttpServletResponse response,
                              Model model) {
        try {
            LoginRequest request = new LoginRequest();
            request.setEmail(email);
            request.setPassword(password);
            setJwtCookie(response, userService.login(request));
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Incorrect email or password.");
            return "playback/login";
        }
    }

    @GetMapping("/register")
    public String register() {
        return "playback/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 HttpServletResponse response,
                                 Model model) {
        try {
            RegisterRequest request = new RegisterRequest();
            request.setName(name);
            request.setEmail(email);
            request.setPassword(password);
            request.setRole(Role.END_USER);
            setJwtCookie(response, userService.register(request));
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Registration failed.");
            return "playback/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin/admin-login";
    }

    @PostMapping("/admin/login")
    public String adminLoginSubmit(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpServletResponse response,
                                   Model model) {
        try {
            LoginRequest request = new LoginRequest();
            request.setEmail(email);
            request.setPassword(password);
            AuthResponse auth = userService.login(request);
            if (!Role.ADMIN.name().equals(auth.getRole())) {
                model.addAttribute("error", "This account is not an administrator.");
                return "admin/admin-login";
            }
            setJwtCookie(response, auth);
            return "redirect:/admin";
        } catch (Exception e) {
            model.addAttribute("error", "Incorrect email or password.");
            return "admin/admin-login";
        }
    }

    private void setJwtCookie(HttpServletResponse response, AuthResponse auth) {
        Cookie cookie = new Cookie("jwt", auth.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);
    }
}
