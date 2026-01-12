package com.lostandfound.app.services;

import com.lostandfound.app.entities.Alert;
import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.entities.Item;
import com.lostandfound.app.entities.UserSearch;
import com.lostandfound.app.repositories.AlertRepository;
import com.lostandfound.app.repositories.UserSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserSearchRepository userSearchRepository;

    /**
     * Create a new search alert for a user
     */
    public UserSearch createSearchAlert(AppUser user, Integer categoryId, String itemType,
                                        String keywords, String location) {
        if (user == null) return null;

        UserSearch search = new UserSearch();
        search.setUserId(user.getUserId());
        search.setCategoryId(categoryId);
        search.setItemType(itemType);
        search.setKeywords(keywords);
        search.setLocation(location);
        search.setIsActive(true);
        search.setCreatedAt(LocalDateTime.now());

        return userSearchRepository.save(search);
    }

    /**
     * Get all search alerts for a user
     */
    public List<UserSearch> getUserSearches(Integer userId) {
        return userSearchRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Deactivate a search alert
     */
    @Transactional
    public boolean deactivateSearch(Integer searchId, Integer userId) {
        UserSearch search = userSearchRepository.findById(searchId).orElse(null);
        if (search == null || !search.getUserId().equals(userId)) return false;

        search.setIsActive(false);
        userSearchRepository.save(search);
        return true;
    }

    /**
     * Delete a search alert
     */
    @Transactional
    public boolean deleteSearch(Integer searchId, Integer userId) {
        UserSearch search = userSearchRepository.findById(searchId).orElse(null);
        if (search == null || !search.getUserId().equals(userId)) return false;

        userSearchRepository.delete(search);
        return true;
    }

    /**
     * Check if a new item matches any active search alerts
     * Creates notifications for matching users
     */
    @Transactional
    public void checkForMatchingSearches(Item newItem) {
        if (newItem == null || newItem.getItemId() == null) return;

        // Get all active searches
        List<UserSearch> activeSearches = userSearchRepository.findByIsActive(true);

        for (UserSearch search : activeSearches) {
            // Don't notify the item's own owner
            if (search.getUserId().equals(newItem.getUserId())) continue;

            // Check if already notified
            if (alertRepository.existsByUserIdAndItemId(search.getUserId(), newItem.getItemId())) {
                continue;
            }

            // Check if item matches search criteria
            if (isMatch(newItem, search)) {
                createNotification(search.getUserId(), newItem, "MATCH_FOUND");
            }
        }
    }

    /**
     * Check if an item matches a search
     */
    private boolean isMatch(Item item, UserSearch search) {
        // Check item type (LOST vs FOUND)
        if (search.getItemType() != null && !search.getItemType().isEmpty()) {
            // If user lost something, notify when someone finds it (opposite type)
            String oppositeType = "LOST".equals(search.getItemType()) ? "FOUND" : "LOST";
            if (!oppositeType.equalsIgnoreCase(item.getItemType())) {
                return false;
            }
        }

        // Check category
        if (search.getCategoryId() != null && item.getCategoryId() != null) {
            if (!search.getCategoryId().equals(item.getCategoryId())) {
                return false;
            }
        }

        // Check keywords in item name or description
        if (search.getKeywords() != null && !search.getKeywords().trim().isEmpty()) {
            String keywords = search.getKeywords().toLowerCase();
            String itemName = (item.getItemName() != null) ? item.getItemName().toLowerCase() : "";
            String itemDesc = (item.getDescription() != null) ? item.getDescription().toLowerCase() : "";

            if (!itemName.contains(keywords) && !itemDesc.contains(keywords)) {
                return false;
            }
        }

        // Check location
        if (search.getLocation() != null && !search.getLocation().trim().isEmpty()) {
            String searchLoc = search.getLocation().toLowerCase();
            String itemLoc = (item.getLocation() != null) ? item.getLocation().toLowerCase() : "";

            if (!itemLoc.contains(searchLoc)) {
                return false;
            }
        }

        return true; // All criteria matched
    }

    /**
     * Create a notification for a user
     */
    private void createNotification(Integer userId, Item item, String alertType) {
        Alert alert = new Alert();
        alert.setUserId(userId);
        alert.setItemId(item.getItemId());
        alert.setItem(item);
        alert.setAlertType(alertType);
        alert.setCreatedAt(LocalDateTime.now());

        alertRepository.save(alert);
    }

    /**
     * Get all alerts/notifications for a user
     */
    public List<Alert> getUserAlerts(Integer userId) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Count alerts for a user
     */
    public long countUserAlerts(Integer userId) {
        return alertRepository.countByUserId(userId);
    }

    /**
     * Delete an alert
     */
    @Transactional
    public boolean deleteAlert(Integer alertId, Integer userId) {
        Alert alert = alertRepository.findById(alertId).orElse(null);
        if (alert == null || !alert.getUserId().equals(userId)) return false;

        alertRepository.delete(alert);
        return true;
    }
}
