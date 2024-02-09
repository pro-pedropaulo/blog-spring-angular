package com.example.blog.repository;

import com.example.blog.model.Post;
import com.example.blog.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("userForPost");
        user.setPassword("pass");
        user.setEmail("userForPost@example.com");
        user = entityManager.persistFlushFind(user);

        post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post content");
        post.setImageUrl("http://example.com/image.jpg");
        post.addImageUrl("http://example.com/image1.jpg");
        post.setApp_user(user);
        post = entityManager.persistFlushFind(post);
    }

    @Test
    void whenSavePost_thenRetrieveSamePost() {
        Post savedPost = postRepository.saveAndFlush(post);
        Post foundPost = postRepository.findById(savedPost.getId()).orElse(null);
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(foundPost.getContent()).isEqualTo(post.getContent());
        assertThat(foundPost.getImageUrl()).isEqualTo(post.getImageUrl());
        assertThat(foundPost.getImageUrls()).containsExactlyElementsOf(post.getImageUrls());
        assertThat(foundPost.getApp_user()).isEqualTo(post.getApp_user());
    }
}
