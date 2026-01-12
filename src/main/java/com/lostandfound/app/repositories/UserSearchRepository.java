package com.lostandfound.app.repositories;

import com.lostandfound.app.entities.UserSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserSearchRepository extends JpaRepository<UserSearch, Integer> {

    // Find all active searches
    List<UserSearch> findByIsActive(Boolean isActive);

    // Find searches by user
    List<UserSearch> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Find active searches by user
    List<UserSearch> findByUserIdAndIsActive(Integer userId, Boolean isActive);
}
