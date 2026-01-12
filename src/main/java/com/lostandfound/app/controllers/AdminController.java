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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    // ✅ Helper to check admin
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

        // ✅ FIXED: Use getItemType() instead of getType()
        long lostItems = allItems.stream()
                .filter(i -> i.getItemType() != null && "LOST".equals(i.getItemType()))
                .count();

        long foundItems = allItems.stream()
                .filter(i -> i.getItemType() != null && "FOUND".equals(i.getItemType()))
                .count();

        long activeItems = allItems.stream()
                .filter(i -> i.getStatus() != null && "ACTIVE".equals(i.getStatus()))
                .count();

        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalItems", allItems.size());
        model.addAttribute("lostItems", lostItems);
        model.addAttribute("foundItems", foundItems);
        model.addAttribute("activeItems", activeItems);
        model.addAttribute("items", allItems);
        model.addAttribute("users", allUsers);

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
