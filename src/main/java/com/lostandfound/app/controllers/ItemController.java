package com.lostandfound.app.controllers;

import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.entities.Category;
import com.lostandfound.app.entities.Item;
import com.lostandfound.app.services.CategoryService;
import com.lostandfound.app.services.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    // ---------- Add Item ----------
    @GetMapping("/items/add")
    public String showAddItem(HttpSession session, ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add-item";
    }

    @PostMapping("/items/save")
    public String saveItem(@RequestParam String itemType,
                           @RequestParam Integer categoryId,
                           @RequestParam String itemName,
                           @RequestParam(required = false) String description,
                           @RequestParam String location,
                           @RequestParam String dateLostFound,
                           @RequestParam(required = false) String contactInfo,
                           HttpSession session,
                           ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(dateLostFound);
        } catch (Exception e) {
            model.addAttribute("error", "Invalid date format.");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "add-item";
        }

        Item saved = itemService.createItem(
                user, itemType, categoryId, itemName, description, location, parsedDate, contactInfo
        );

        if (saved == null || saved.getItemId() == null) {
            model.addAttribute("error", "Please fill all required fields.");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "add-item";
        }

        return "redirect:/items/" + saved.getItemId();
    }

    // ---------- Browse ----------
    @GetMapping("/items/browse")
    public String browse(@RequestParam(required = false) String itemType,
                         @RequestParam(required = false) Integer categoryId,
                         @RequestParam(required = false) String q,
                         @RequestParam(required = false) String location,
                         HttpSession session,
                         ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        List<Item> items = itemService.browseItems(itemType, categoryId, q, location);
        model.addAttribute("items", items);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedType", itemType);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedLocation", location);
        model.addAttribute("searchQuery", q);
        return "browse";
    }

    // ---------- Item Detail ----------
    @GetMapping("/items/{itemId}")
    public String itemDetails(@PathVariable Integer itemId,
                              HttpSession session,
                              ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        Item item = itemService.getItemDetailsAndIncrementViews(itemId);
        if (item == null) return "redirect:/items/browse";

        boolean isOwner = item.getUserId() != null && item.getUserId().equals(user.getUserId());
        Integer viewCount = (item.getViewCount() == null) ? 0 : item.getViewCount();

        String categoryName = null;
        if (item.getCategory() != null && item.getCategory().getCategoryName() != null) {
            categoryName = item.getCategory().getCategoryName();
        } else if (item.getCategoryId() != null) {
            List<Category> categories = categoryService.getAllCategories();
            for (Category c : categories) {
                if (c.getCategoryId() != null && c.getCategoryId().equals(item.getCategoryId())) {
                    categoryName = c.getCategoryName();
                    break;
                }
            }
        }

        String postedByUsername;
        if (item.getUser() != null && item.getUser().getUsername() != null) {
            postedByUsername = item.getUser().getUsername();
        } else {
            postedByUsername = String.valueOf(item.getUserId());
        }

        model.addAttribute("item", item);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("viewCount", viewCount);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("postedByUsername", postedByUsername);
        model.addAttribute("similarItems", itemService.getSimilarItems(item, 4));
        return "item-detail";
    }

    // ---------- Backward compatibility ----------
    @GetMapping("/items/detail")
    public String detailLegacy(@RequestParam Integer itemId, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";
        return "redirect:/items/" + itemId;
    }

    // ---------- My Items ----------
    @GetMapping("/my-items")
    public String myItems(HttpSession session, ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";
        model.addAttribute("items", itemService.getItemsForUser(user.getUserId()));
        return "my-items";
    }

    // Called from item-detail page (owner-only button)
    @PostMapping("/items/{itemId}/resolve")
    public String resolveFromDetail(@PathVariable Integer itemId, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";
        itemService.markItemResolved(itemId, user);
        return "redirect:/items/" + itemId;
    }

    @PostMapping("/items/resolve")
    public String resolveItem(@RequestParam Integer itemId, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";
        itemService.markItemResolved(itemId, user);
        return "redirect:/my-items";
    }

    @PostMapping("/items/delete")
    public String deleteItem(@RequestParam Integer itemId, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";
        itemService.deleteItem(itemId, user);
        return "redirect:/my-items";
    }

    // ---------- Edit / Update ----------

    // ✅ FIX: Support both query param and path variable for edit
    @GetMapping("/items/edit/{itemId}")
    public String editItemWithPath(@PathVariable Integer itemId, HttpSession session, ModelMap model) {
        return editItem(itemId, session, model);
    }

    @GetMapping("/items/edit")
    public String editItem(@RequestParam Integer itemId, HttpSession session, ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        Item item = itemService.getItemForEdit(itemId, user);
        if (item == null) return "redirect:/my-items";

        model.addAttribute("item", item);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "edit-item";
    }

    @PostMapping("/items/update")
    public String updateItem(@RequestParam Integer itemId,
                             @RequestParam String itemType,
                             @RequestParam Integer categoryId,
                             @RequestParam String itemName,
                             @RequestParam(required = false) String description,
                             @RequestParam String location,
                             @RequestParam String dateLostFound,
                             @RequestParam(required = false) String contactInfo,
                             HttpSession session,
                             ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(dateLostFound);
        } catch (Exception e) {
            model.addAttribute("error", "Invalid date format.");
            model.addAttribute("item", itemService.getItemForEdit(itemId, user));
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit-item";
        }

        boolean ok = itemService.updateItem(
                itemId, user, itemName, description, location, contactInfo, categoryId, itemType, parsedDate
        );

        if (!ok) {
            model.addAttribute("error", "Update failed. Please check required fields.");
            model.addAttribute("item", itemService.getItemForEdit(itemId, user));
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit-item";
        }

        return "redirect:/my-items";
    }
}
