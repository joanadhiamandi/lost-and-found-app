package com.lostandfound.app.repositories;

import com.lostandfound.app.entities.Comment;
import com.lostandfound.app.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // Find all comments for a specific item, ordered by newest first
    List<Comment> findByItemOrderByCreatedAtDesc(Item item);

    // Count comments for an item
    long countByItem(Item item);

}
