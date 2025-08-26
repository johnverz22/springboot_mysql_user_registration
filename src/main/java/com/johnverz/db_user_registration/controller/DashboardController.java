package com.johnverz.db_user_registration.controller;

import com.johnverz.db_user_registration.entity.Database;
import com.johnverz.db_user_registration.entity.User;
import com.johnverz.db_user_registration.service.DatabaseService;
import com.johnverz.db_user_registration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("")
    public String dashboard(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Database> databases = databaseService.findByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("databases", databases);
        model.addAttribute("newDatabase", new Database());

        return "dashboard";
    }

    @PostMapping("/create-database")
    public String createDatabase(@RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            databaseService.createDatabase(name, description, user);
            redirectAttributes.addFlashAttribute("message", "Database created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create database: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/delete-database/{id}")
    public String deleteDatabase(@PathVariable Long id,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            databaseService.deleteDatabase(id, user);
            redirectAttributes.addFlashAttribute("message", "Database deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete database: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }
}