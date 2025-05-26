package dev.esteban.motuswebapp.controller;

import dev.esteban.motuswebapp.service.MotusApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private MotusApiService motusApiService;

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/game";
        }
        model.addAttribute("title", "Login");
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam String username, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {

        boolean success = motusApiService.login(username, password);

        if (success) {
            session.setAttribute("username", username);
            return "redirect:/game";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        motusApiService.logout();
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/game";
        }
        model.addAttribute("title", "Register");
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        boolean success = motusApiService.register(username, email, password);

        if (success) {
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Registration failed. Username or email may already exist.");
            return "redirect:/register";
        }
    }
}
