package com.example.blog.service;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReactionRepository;
import com.example.blog.util.ExceptionMessages;
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
        assertNotNull(result);
        assertEquals("Test Post", result.getTitle());
    }

    @Test
    void findById_NotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenThrow(new ResourceNotFoundException(ExceptionMessages.POST_NOT_FOUND_WITH_ID + postId));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> postService.findById(postId));
        assertTrue(exception.getMessage().contains(ExceptionMessages.POST_NOT_FOUND_WITH_ID + postId));
    }


    @Test
    void save() {
        when(postRepository.save(any(Post.class))).thenReturn(post);
        var savedPost = postService.save(new Post());
        assertNotNull(savedPost);
        assertEquals("Test Post", savedPost.getTitle());
    }

    @Test
    void save_notSaved() {
        when(postRepository.save(any(Post.class))).thenReturn(null);
        var savedPost = postService.save(new Post());
        assertNull(savedPost);
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
    void update_NotFound() {
        Long postId = 1L;
        Post updatedDetails = new Post();
        updatedDetails.setTitle("Updated Post");

        when(postRepository.findById(postId)).thenThrow(new ResourceNotFoundException(ExceptionMessages.POST_NOT_FOUND_WITH_ID + postId));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> postService.update(postId, updatedDetails));
        assertTrue(exception.getMessage().contains(ExceptionMessages.POST_NOT_FOUND_WITH_ID + postId));
    }


    @Test
    void deletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.deletePost(1L);
        verify(commentRepository, times(1)).deleteByPostId(1L);
        verify(reactionRepository, times(1)).deleteByPostId(1L);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void deletePost_NotFound() {
        when(postRepository.findById(1L)).thenThrow(new ResourceNotFoundException(ExceptionMessages.POST_NOT_FOUND_WITH_ID + 1L));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1L));
        assertTrue(exception.getMessage().contains(ExceptionMessages.POST_NOT_FOUND_WITH_ID + 1L));
        verify(commentRepository, never()).deleteByPostId(1L);
        verify(reactionRepository, never()).deleteByPostId(1L);
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void likePost() {
        final String USER_2 = "user2";
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        var likedPost = postService.likePost(1L, USER_2);
        assertNotNull(likedPost);
        assertTrue(likedPost.getLikes().contains(USER_2));
        assertFalse(likedPost.getDislikes().contains(USER_2));
    }

    @Test
    void likePost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> postService.likePost(1L, "user2"));
        assertTrue(exception.getMessage().contains(ExceptionMessages.POST_NOT_FOUND));
    }
    @Test
    void dislikePost() {
        final String USER_3 = "user3";
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        var dislikedPost = postService.dislikePost(1L, USER_3);
        assertNotNull(dislikedPost);
        assertTrue(dislikedPost.getDislikes().contains(USER_3));
        assertFalse(dislikedPost.getLikes().contains(USER_3));
    }

    @Test
    void dislikePost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> postService.dislikePost(1L, "user3"));
        assertTrue(exception.getMessage().contains(ExceptionMessages.POST_NOT_FOUND));
    }
}
