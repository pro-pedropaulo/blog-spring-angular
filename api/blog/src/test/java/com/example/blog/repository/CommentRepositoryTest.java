package com.example.blog.repository;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setEmail("test@example.com");
        user = entityManager.persistAndFlush(user);

        post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test content");
        post.setApp_user(user);
        post = entityManager.persistAndFlush(post);

        Comment comment = new Comment();
        comment.setContent("Test Comment");
        comment.setPost(post);
        entityManager.persistAndFlush(comment);
    }

    @Test
    void findByPostId() {
        List<Comment> comments = commentRepository.findByPostId(post.getId());
        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals("Test Comment", comments.get(0).getContent());
    }

    @Test
    void deleteByPostId() {
        commentRepository.deleteByPostId(post.getId());
        entityManager.flush();

        List<Comment> comments = commentRepository.findByPostId(post.getId());
        assertTrue(comments.isEmpty());
    }
}
