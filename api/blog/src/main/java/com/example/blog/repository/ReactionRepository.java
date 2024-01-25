package com.example.blog.repository;

import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndPost(User user, Post post);
    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.post = :post AND r.reactionLike = :reactionLike")
    long countByPostAndReactionLike(@Param("post") Post post, @Param("reactionLike") boolean reactionLike);
}

