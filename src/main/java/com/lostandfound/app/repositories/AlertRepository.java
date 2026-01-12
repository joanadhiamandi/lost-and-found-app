package com.lostandfound.app.repositories;

import com.lostandfound.app.entities.Alert;
import com.lostandfound.app.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Integer> {

    // Find alerts by user (ordered by newest first)
    List<Alert> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Count unread alerts for user
    long countByUserId(Integer userId);

    // Check if alert already exists for user+item
    boolean existsByUserIdAndItemId(Integer userId, Integer itemId);

    // Delete alert by ID
    @Modifying
    @Query("DELETE FROM Alert a WHERE a.alertId = ?1 AND a.userId = ?2")
    void deleteByAlertIdAndUserId(Integer alertId, Integer userId);
}
