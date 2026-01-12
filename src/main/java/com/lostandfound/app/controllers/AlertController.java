package com.lostandfound.app.controllers;

import com.lostandfound.app.entities.Alert;
import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.entities.UserSearch;
import com.lostandfound.app.services.AlertService;
import com.lostandfound.app.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Show my alerts page
     */
    @GetMapping("/my-alerts")
    public String myAlerts(HttpSession session, ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        // Get user's alerts/notifications
        List<Alert> alerts = alertService.getUserAlerts(user.getUserId());

        // Get user's search alerts
        List<UserSearch> searches = alertService.getUserSearches(user.getUserId());

        model.addAttribute("alerts", alerts);
        model.addAttribute("searches", searches);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "my-alerts";
    }

    /**
     * Create a new search alert
     */
    @PostMapping("/alerts/create")
    public String createAlert(@RequestParam(required = false) Integer categoryId,
                              @RequestParam(required = false) String itemType,
                              @RequestParam(required = false) String keywords,
                              @RequestParam(required = false) String location,
                              HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        alertService.createSearchAlert(user, categoryId, itemType, keywords, location);

        return "redirect:/my-alerts?success=created";
    }

    /**
     * Delete a search alert
     */
    @PostMapping("/alerts/search/{searchId}/delete")
    public String deleteSearchAlert(@PathVariable Integer searchId, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        alertService.deleteSearch(searchId, user.getUserId());

        return "redirect:/my-alerts?success=deleted";
    }

    /**
     * Deactivate a search alert
     */
    @PostMapping("/alerts/search/{searchId}/deactivate")
    public String deactivateSearchAlert(@PathVariable Integer searchId, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        alertService.deactivateSearch(searchId, user.getUserId());

        return "redirect:/my-alerts?success=deactivated";
    }

    /**
     * Delete a notification
     */
    @PostMapping("/alerts/{alertId}/delete")
    public String deleteAlert(@PathVariable Integer alertId, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        alertService.deleteAlert(alertId, user.getUserId());

        return "redirect:/my-alerts?success=cleared";
    }
}
