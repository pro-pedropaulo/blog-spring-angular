package com.example.blog.controller;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.CommentService;
import com.example.blog.service.UserService;
import com.example.blog.util.ExceptionMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentController commentController;

    private Comment comment;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        openMocks(this);

        post = new Post();
        post.setId(1L);

        user = new User();
        user.setUsername("user");

        comment = new Comment();
        comment.setId(1L);
        comment.setPostId(post.getId());
        comment.setContent("Test Comment");
        comment.setApp_user(user);
    }

    @Test
    void getAllComments() {
        when(commentService.findAll()).thenReturn(Arrays.asList(comment));
        List<Comment> result = commentController.getAllComments();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllComments_Empty() {
        when(commentService.findAll()).thenReturn(Arrays.asList());
        List<Comment> result = commentController.getAllComments();
        assertTrue(result.isEmpty());
    }

    @Test
    void getCommentById_Found() {
        when(commentService.findById(1L)).thenReturn(comment);
        ResponseEntity<Comment> result = commentController.getCommentById(1L);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(comment, result.getBody());
    }

    @Test
    void getCommentById_NotFound() {
        when(commentService.findById(1L)).thenThrow(new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentController.getCommentById(1L);
        });
        assertTrue(exception.getMessage().contains(ExceptionMessages.COMMENT_NOT_FOUND));
    }

    @Test
    void updateComment_Found() {
        when(commentService.update(any(Long.class), any(Comment.class))).thenReturn(comment);
        ResponseEntity<Comment> result = commentController.updateComment(1L, comment);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(comment, result.getBody());
    }

    @Test
    void updateComment_NotFound() {
        when(commentService.update(any(Long.class), any(Comment.class))).thenThrow(new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentController.updateComment(1L, comment);
        });
        assertTrue(exception.getMessage().contains(ExceptionMessages.COMMENT_NOT_FOUND));
    }

    @Test
    void deleteComment_Success() {
        when(commentService.findById(1L)).thenReturn(comment);
        assertDoesNotThrow(() -> commentController.deleteComment(1L));
    }

    @Test
    void deleteComment_NotFound() {
        doThrow(new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND)).when(commentService).deleteComment(anyLong());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentController.deleteComment(1L);
        });
        assertTrue(exception.getMessage().contains(ExceptionMessages.COMMENT_NOT_FOUND));
    }

    @Test
    void getCommentsByPostId() {
        when(commentService.findCommentsByPostId(post.getId())).thenReturn(Arrays.asList(comment));
        ResponseEntity<List<Comment>> result = commentController.getCommentsByPostId(post.getId());
        assertFalse(result.getBody().isEmpty());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getCommentsByPostId_Empty() {
        when(commentService.findCommentsByPostId(post.getId())).thenReturn(Arrays.asList());
        ResponseEntity<List<Comment>> result = commentController.getCommentsByPostId(post.getId());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void createComment_PostNotFound() {
        when(postRepository.findById(comment.getPostId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> commentController.createComment(comment));
        assertEquals(ExceptionMessages.POST_NOT_FOUND, exception.getMessage());
    }

    @Test
    void createComment_Success() {
        when(postRepository.findById(comment.getPostId())).thenReturn(Optional.of(post));
        when(userService.findByUsername(any(String.class))).thenReturn(user);
        when(commentService.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentController.createComment(comment);
        assertNotNull(result);
        assertEquals(comment.getContent(), result.getContent());
        assertEquals(comment.getPostId(), result.getPost().getId());
        assertEquals(comment.getApp_user().getUsername(), result.getApp_user().getUsername());
    }
}
