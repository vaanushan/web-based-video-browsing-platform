package com.sliit.videobrowsing.common.config;

import com.sliit.videobrowsing.user.entity.User;
import com.sliit.videobrowsing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/**
 * Controller advice that automatically loads the logged-in User's database details
 * and makes it available as "currentUser" in all Thymeleaf views.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;

    @ModelAttribute
    public void addCurrentUser(Principal principal, Model model) {
        if (principal != null) {
            try {
                User user = userService.getByEmail(principal.getName());
                model.addAttribute("currentUser", user);
            } catch (Exception e) {
                // User not found or DB not populated yet
            }
        }
    }
}
