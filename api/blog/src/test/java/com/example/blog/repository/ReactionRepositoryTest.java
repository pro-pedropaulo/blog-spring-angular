package com.example.blog.repository;

import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReactionRepository reactionRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setEmail("testeUser@example.com");
        entityManager.persist(user);

        post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post.");
        entityManager.persist(post);

        entityManager.flush();
    }

    @Test
    void findByUserAndPost() {
        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setReactionLike(true);
        entityManager.persist(reaction);
        entityManager.flush();

        Optional<Reaction> foundReaction = reactionRepository.findByUserAndPost(user, post);
        assertThat(foundReaction).isPresent();
        assertThat(foundReaction.get().isReactionLike()).isTrue();
    }

    @Test
    void countByPostAndReactionLike() {
        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setReactionLike(true);
        entityManager.persist(reaction);
        entityManager.flush();

        long count = reactionRepository.countByPostAndReactionLike(post, true);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void deleteByPostId() {
        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setReactionLike(true);
        entityManager.persist(reaction);
        entityManager.flush();

        reactionRepository.deleteByPostId(post.getId());
        entityManager.flush();

        Optional<Reaction> foundReaction = reactionRepository.findByUserAndPost(user, post);
        assertThat(foundReaction).isNotPresent();
    }
}
