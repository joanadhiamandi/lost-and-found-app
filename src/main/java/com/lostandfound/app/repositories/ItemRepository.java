package com.lostandfound.app.repositories;

import com.lostandfound.app.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByUserId(Integer userId);

    List<Item> findByStatusOrderByCreatedAtDesc(String status);

    List<Item> findByStatusAndItemTypeOrderByCreatedAtDesc(String status, String itemType);

    List<Item> findByStatusAndCategoryIdOrderByCreatedAtDesc(String status, Integer categoryId);

    List<Item> findByStatusAndItemTypeAndCategoryIdOrderByCreatedAtDesc(String status, String itemType, Integer categoryId);

    @Modifying
    @Query("update Item i set i.viewCount = coalesce(i.viewCount, 0) + 1 where i.itemId = :itemId")
    void incrementViewCount(@Param("itemId") Integer itemId);
}
