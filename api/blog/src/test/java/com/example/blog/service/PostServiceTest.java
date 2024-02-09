package com.example.blog.service;

import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private PostService postService;

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setLikes(new HashSet<>(Arrays.asList("user1")));
        post.setDislikes(new HashSet<>());
    }

    @Test
    void findAll() {
        when(postRepository.findAll()).thenReturn(Arrays.asList(post));
        var result = postService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Post", result.get(0).getTitle());
    }

    @Test
    void findById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        var result = postService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test Post", result.get().getTitle());
    }

    @Test
    void save() {
        when(postRepository.save(any(Post.class))).thenReturn(post);
        var savedPost = postService.save(new Post());
        assertNotNull(savedPost);
        assertEquals("Test Post", savedPost.getTitle());
    }

    @Test
    void update() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        var updatedPost = postService.update(1L, post);
        assertTrue(updatedPost.isPresent());
        assertEquals("Test Post", updatedPost.get().getTitle());
    }

    @Test
    void delete() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(commentRepository).deleteByPostId(1L);
        doNothing().when(reactionRepository).deleteByPostId(1L);
        doNothing().when(postRepository).delete(post);
        assertTrue(postService.delete(1L));
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void likePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        var likedPost = postService.likePost(1L, "user2");
        assertNotNull(likedPost);
        assertTrue(likedPost.getLikes().contains("user2"));
        assertFalse(likedPost.getDislikes().contains("user2"));
    }

    @Test
    void dislikePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        var dislikedPost = postService.dislikePost(1L, "user3");
        assertNotNull(dislikedPost);
        assertTrue(dislikedPost.getDislikes().contains("user3"));
        assertFalse(dislikedPost.getLikes().contains("user3"));
    }
}
