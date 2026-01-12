package com.lostandfound.app.repositories;

import com.lostandfound.app.entities.UserSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserSearchRepository extends JpaRepository<UserSearch, Integer> {
    List<UserSearch> findByUserId(Integer userId);
}