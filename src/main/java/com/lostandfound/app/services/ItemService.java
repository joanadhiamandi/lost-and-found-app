package com.lostandfound.app.services;

import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.entities.Item;
import com.lostandfound.app.repositories.CategoryRepository;
import com.lostandfound.app.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AlertService alertService; // ⭐ ADD THIS

    public Item createItem(AppUser loggedInUser,
                           String itemType,
                           Integer categoryId,
                           String itemName,
                           String description,
                           String location,
                           LocalDate dateLostFound,
                           String contactInfo) {
        if (loggedInUser == null) return null;
        if (itemType == null || itemType.trim().isEmpty()) return null;
        if (categoryId == null || !categoryRepository.existsById(categoryId)) return null;
        if (itemName == null || itemName.trim().isEmpty()) return null;
        if (location == null || location.trim().isEmpty()) return null;
        if (dateLostFound == null) return null;

        Item item = new Item();
        item.setUserId(loggedInUser.getUserId());
        item.setCategoryId(categoryId);
        item.setItemType(itemType.trim().toUpperCase()); // LOST / FOUND
        item.setItemName(itemName.trim());
        item.setDescription(description);
        item.setLocation(location.trim());
        item.setDateLostFound(dateLostFound);
        item.setContactInfo(contactInfo);
        item.setStatus("ACTIVE");
        item.setViewCount(0);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);

        // CHECK FOR MATCHING SEARCH ALERTS
        if (savedItem != null && savedItem.getItemId() != null) {
            alertService.checkForMatchingSearches(savedItem);
        }

        return savedItem;
    }

    // browse with type + category + free-text q + location
    public List<Item> browseItems(String itemType, Integer categoryId, String q, String location) {
        String status = "ACTIVE";
        String type = (itemType == null || itemType.trim().isEmpty()) ? null : itemType.trim().toUpperCase();
        List<Item> items;

        if (type != null && categoryId != null) {
            items = itemRepository.findByStatusAndItemTypeAndCategoryIdOrderByCreatedAtDesc(status, type, categoryId);
        } else if (type != null) {
            items = itemRepository.findByStatusAndItemTypeOrderByCreatedAtDesc(status, type);
        } else if (categoryId != null) {
            items = itemRepository.findByStatusAndCategoryIdOrderByCreatedAtDesc(status, categoryId);
        } else {
            items = itemRepository.findByStatusOrderByCreatedAtDesc(status);
        }

        if (location != null && !location.trim().isEmpty()) {
            String loc = location.trim().toLowerCase();
            items = items.stream()
                    .filter(i -> i.getLocation() != null && i.getLocation().toLowerCase().contains(loc))
                    .collect(Collectors.toList());
        }

        if (q != null && !q.trim().isEmpty()) {
            String query = q.trim().toLowerCase();
            items = items.stream()
                    .filter(i ->
                            (i.getItemName() != null && i.getItemName().toLowerCase().contains(query)) ||
                                    (i.getDescription() != null && i.getDescription().toLowerCase().contains(query))
                    )
                    .collect(Collectors.toList());
        }

        return items;
    }

    public List<Item> getActiveItems(String itemType, Integer categoryId) {
        return browseItems(itemType, categoryId, null, null);
    }

    public Item getItemById(Integer itemId) {
        return itemRepository.findById(itemId).orElse(null);
    }

    public List<Item> getItemsForUser(Integer userId) {
        return itemRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Item::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    public boolean markItemResolved(Integer itemId, AppUser user) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null || user == null) return false;

        boolean isOwner = item.getUserId() != null && item.getUserId().equals(user.getUserId());
        boolean isAdmin = user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) return false;

        item.setStatus("RESOLVED");
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
        return true;
    }

    public boolean deleteItem(Integer itemId, AppUser user) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null || user == null) return false;

        boolean isOwner = item.getUserId() != null && item.getUserId().equals(user.getUserId());
        boolean isAdmin = user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) return false;

        // Soft delete
        item.setStatus("DELETED");
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
        return true;
    }

    public Item getItemForEdit(Integer itemId, AppUser user) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null || user == null) return null;

        boolean isOwner = item.getUserId() != null && item.getUserId().equals(user.getUserId());
        boolean isAdmin = user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) return null;

        return item;
    }

    public boolean updateItem(Integer itemId,
                              AppUser user,
                              String itemName,
                              String description,
                              String location,
                              String contactInfo,
                              Integer categoryId,
                              String itemType,
                              LocalDate dateLostFound) {
        Item item = getItemForEdit(itemId, user);
        if (item == null) return false;
        if (itemName == null || itemName.trim().isEmpty()) return false;
        if (location == null || location.trim().isEmpty()) return false;
        if (dateLostFound == null) return false;
        if (categoryId == null || !categoryRepository.existsById(categoryId)) return false;
        if (itemType == null || itemType.trim().isEmpty()) return false;

        item.setItemName(itemName.trim());
        item.setDescription(description);
        item.setLocation(location.trim());
        item.setContactInfo(contactInfo);
        item.setCategoryId(categoryId);
        item.setItemType(itemType.trim().toUpperCase());
        item.setDateLostFound(dateLostFound);
        item.setUpdatedAt(LocalDateTime.now());

        itemRepository.save(item);
        return true;
    }

    @Transactional
    public Item getItemDetailsAndIncrementViews(Integer itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) return null;

        itemRepository.incrementViewCount(itemId);

        // Re-fetch so controller/template sees updated viewCount
        return itemRepository.findById(itemId).orElse(item);
    }

    public List<Item> getSimilarItems(Item baseItem, int limit) {
        if (baseItem == null || baseItem.getCategoryId() == null) return List.of();

        List<Item> items = itemRepository.findByStatusAndCategoryIdOrderByCreatedAtDesc("ACTIVE", baseItem.getCategoryId());
        return items.stream()
                .filter(i -> i.getItemId() != null && !i.getItemId().equals(baseItem.getItemId()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
