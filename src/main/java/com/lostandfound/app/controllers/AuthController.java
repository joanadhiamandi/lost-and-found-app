package com.lostandfound.app.controllers;

import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.services.AppUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AppUserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/dologin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          HttpSession session,
                          ModelMap model) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            model.addAttribute("error", "Username and password are required");
            return "login";
        }

        AppUser user = userService.authenticateUser(username, password);
        if (user != null) {
            session.setAttribute("loggedinuser", user);

            // ✅ Check if admin and redirect accordingly
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin-panel";
            }

            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @PostMapping("/doregister")
    public String doRegister(@RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("fullName") String fullName,
                             @RequestParam("password") String password,
                             @RequestParam("confirmPassword") String confirmPassword,
                             HttpSession session,
                             ModelMap model) {
        try {
            if (username == null || username.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    fullName == null || fullName.trim().isEmpty() ||
                    password == null || password.trim().isEmpty() ||
                    confirmPassword == null || confirmPassword.trim().isEmpty()) {
                model.addAttribute("error", "❌ All fields are required!");
                model.addAttribute("fullName", fullName);
                model.addAttribute("username", username);
                model.addAttribute("email", email);
                return "register";
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                model.addAttribute("error", "❌ Invalid email format!");
                model.addAttribute("fullName", fullName);
                model.addAttribute("username", username);
                model.addAttribute("email", email);
                return "register";
            }

            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "❌ Passwords do not match!");
                model.addAttribute("fullName", fullName);
                model.addAttribute("username", username);
                model.addAttribute("email", email);
                return "register";
            }

            AppUserService.RegistrationResult result =
                    userService.registerUser(username, email, fullName, password);

            switch (result.getStatus()) {
                case SUCCESS:
                    return "redirect:/login";
                case USERNAME_TAKEN:
                    model.addAttribute("error", "❌ Username '" + username + "' is already taken!");
                    model.addAttribute("fullName", fullName);
                    model.addAttribute("email", email);
                    return "register";
                case EMAIL_TAKEN:
                    model.addAttribute("error", "❌ Email '" + email + "' is already registered!");
                    model.addAttribute("fullName", fullName);
                    model.addAttribute("username", username);
                    return "register";
                case FAILED:
                default:
                    model.addAttribute("error", "❌ Registration failed! Please try again.");
                    model.addAttribute("fullName", fullName);
                    model.addAttribute("username", username);
                    model.addAttribute("email", email);
                    return "register";
            }

        } catch (Exception e) {
            model.addAttribute("error", "❌ An error occurred: " + e.getMessage());
            model.addAttribute("fullName", fullName);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedinuser");
        session.invalidate();
        return "redirect:/login";
    }
}
