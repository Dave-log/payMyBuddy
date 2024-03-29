package com.payMyBuddy.payMyBuddy.repository;

import com.payMyBuddy.payMyBuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.buddies FROM User u WHERE u.id = :userId")
    List<User> findBuddiesByUserId(@Param("userId") long userId);

    @Query("SELECT COUNT(b) > 0 FROM User u JOIN u.buddies b WHERE u.id = :currentUserId AND b.id = :recipientId")
    boolean isRecipientBuddyOfCurrentUser(Long currentUserId, Long recipientId);

    boolean existsByEmail(String email);
}
