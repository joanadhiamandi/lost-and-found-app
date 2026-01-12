package com.lostandfound.app.repositories;

import com.lostandfound.app.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItemId(Integer itemId);
    List<Comment> findByUserId(Integer userId);
}