package com.lostandfound.app.repositories;

import com.lostandfound.app.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("""
        SELECT m
        FROM Message m
        WHERE (
            (m.sender.userId = :userA AND m.recipient.userId = :userB)
            OR
            (m.sender.userId = :userB AND m.recipient.userId = :userA)
        )
        AND m.item.itemId = :itemId
        ORDER BY m.createdAt ASC
        """)
    List<Message> getThread(@Param("userA") Integer userA,
                            @Param("userB") Integer userB,
                            @Param("itemId") Integer itemId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Message m
        SET m.readAt = CURRENT_TIMESTAMP
        WHERE m.recipient.userId = :me
        AND m.sender.userId = :other
        AND m.readAt IS NULL
        AND m.item.itemId = :itemId
        """)
    int markThreadAsRead(@Param("me") Integer me,
                         @Param("other") Integer other,
                         @Param("itemId") Integer itemId);


    @Query("""
        SELECT m
        FROM Message m
        WHERE m.sender.userId = :userId
        OR m.recipient.userId = :userId
        ORDER BY m.createdAt DESC
        """)
    List<Message> findBySenderUserIdOrRecipientUserId(@Param("userId") Integer userId,
                                                      @Param("userId") Integer userId2);
}
