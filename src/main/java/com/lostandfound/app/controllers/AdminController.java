package com.lostandfound.app.controllers;

import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.entities.Item;
import com.lostandfound.app.repositories.ItemRepository;
import com.lostandfound.app.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private boolean isAdmin(HttpSession session) {
        Object obj = session.getAttribute("loggedinuser");
        if (!(obj instanceof AppUser)) return false;
        AppUser user = (AppUser) obj;
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    @GetMapping("/admin-panel")
    public String adminPanel(HttpSession session, ModelMap model) {
        if (!isAdmin(session)) {
            return "redirect:/dashboard";
        }

        List<Item> allItems = itemRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<AppUser> allUsers = userRepository.findAll();

        // ==========================================
        // BASIC STATISTICS
        // ==========================================
        long lostItems = allItems.stream()
                .filter(i -> i.getItemType() != null && "LOST".equals(i.getItemType()))
                .count();

        long foundItems = allItems.stream()
                .filter(i -> i.getItemType() != null && "FOUND".equals(i.getItemType()))
                .count();

        long activeItems = allItems.stream()
                .filter(i -> i.getStatus() != null && "ACTIVE".equals(i.getStatus()))
                .count();

        long resolvedItems = allItems.stream()
                .filter(i -> i.getStatus() != null && "RESOLVED".equals(i.getStatus()))
                .count();


        // 1. MOST ACTIVE MEMBERS (Top 5 users by item count)
        Map<String, Long> userItemCounts = allItems.stream()
                .filter(item -> item.getUser() != null && item.getUser().getFullName() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getUser().getFullName(),
                        Collectors.counting()
                ));

        // Sort by count and get top 5
        List<Map.Entry<String, Long>> topUsers = userItemCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Prepare data for chart
        List<String> topUserNames = topUsers.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Long> topUserCounts = topUsers.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        // 2. ITEMS BY CATEGORY
        Map<String, Long> categoryItemCounts = allItems.stream()
                .filter(item -> item.getCategory() != null && item.getCategory().getCategoryName() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getCategory().getCategoryName(),
                        Collectors.counting()
                ));

        List<String> categoryNames = new ArrayList<>(categoryItemCounts.keySet());
        List<Long> categoryCounts = new ArrayList<>(categoryItemCounts.values());

        // 3. LOST vs FOUND TREND (by month - last 6 months)
        Map<String, Integer> lostByMonth = new LinkedHashMap<>();
        Map<String, Integer> foundByMonth = new LinkedHashMap<>();

        // Simple monthly grouping (you can enhance this)
        for (Item item : allItems) {
            if (item.getCreatedAt() != null) {
                String month = item.getCreatedAt().getMonth().toString().substring(0, 3);

                if ("LOST".equals(item.getItemType())) {
                    lostByMonth.put(month, lostByMonth.getOrDefault(month, 0) + 1);
                } else if ("FOUND".equals(item.getItemType())) {
                    foundByMonth.put(month, foundByMonth.getOrDefault(month, 0) + 1);
                }
            }
        }

        // ADD ALL DATA TO MODEL

        // Basic stats
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalItems", allItems.size());
        model.addAttribute("lostItems", lostItems);
        model.addAttribute("foundItems", foundItems);
        model.addAttribute("activeItems", activeItems);
        model.addAttribute("resolvedItems", resolvedItems);

        // Tables data
        model.addAttribute("items", allItems);
        model.addAttribute("users", allUsers);

        // Advanced analytics data for charts
        model.addAttribute("topUserNames", topUserNames);
        model.addAttribute("topUserCounts", topUserCounts);
        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("categoryCounts", categoryCounts);

        // Lost vs Found comparison
        model.addAttribute("lostCount", lostItems);
        model.addAttribute("foundCount", foundItems);

        return "admin-panel";
    }

    @PostMapping("/admin/delete-item")
    @ResponseBody
    public Map<String, Object> deleteItem(@RequestParam("itemId") Integer itemId,
                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "❌ Unauthorized");
            return response;
        }

        try {
            itemRepository.deleteById(itemId);
            response.put("success", true);
            response.put("message", "✅ Item deleted successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "❌ Error: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/admin/delete-user")
    @ResponseBody
    public Map<String, Object> deleteUser(@RequestParam("userId") Integer userId,
                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "❌ Unauthorized");
            return response;
        }

        try {
            AppUser admin = (AppUser) session.getAttribute("loggedinuser");
            if (admin.getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "❌ Cannot delete your own account");
                return response;
            }

            userRepository.deleteById(userId);
            response.put("success", true);
            response.put("message", "✅ User deleted successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "❌ Error: " + e.getMessage());
        }

        return response;
    }
}
