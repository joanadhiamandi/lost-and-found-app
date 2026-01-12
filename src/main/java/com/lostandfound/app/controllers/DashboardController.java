package com.lostandfound.app.controllers;

import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.entities.Item;
import com.lostandfound.app.repositories.ItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, ModelMap model) {
        // Get logged in user
        AppUser user = (AppUser) session.getAttribute("loggedinuser");

        if (user == null) {
            return "redirect:/login";
        }

        // Get all items
        List<Item> allItems = itemRepository.findAll();

        // Calculate statistics
        long totalItems = allItems.size();

        long lostItems = allItems.stream()
                .filter(item -> item.getItemType() != null && "LOST".equals(item.getItemType()))
                .count();

        long foundItems = allItems.stream()
                .filter(item -> item.getItemType() != null && "FOUND".equals(item.getItemType()))
                .count();

        long activeItems = allItems.stream()
                .filter(item -> item.getStatus() != null && "ACTIVE".equals(item.getStatus()))
                .count();

        // Add statistics to model
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("lostItems", lostItems);
        model.addAttribute("foundItems", foundItems);
        model.addAttribute("activeItems", activeItems);

        return "dashboard";
    }
}
