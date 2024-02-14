package com.example.blog.service;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Comment;
import com.example.blog.repository.CommentRepository;
import com.example.blog.util.ExceptionMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");
        comment.setPostId(1L);
    }

    @Test
    void findAll() {
        when(commentRepository.findAll()).thenReturn(Arrays.asList(comment));
        List<Comment> result = commentService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(comment.getContent(), result.get(0).getContent());
    }

    @Test
    void findById() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Comment result = commentService.findById(commentId);
        assertNotNull(result);
        assertEquals(comment.getContent(), result.getContent());
    }

    @Test
    void findById_NotFound() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenThrow(new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + commentId));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> commentService.findById(commentId));
        assertTrue(exception.getMessage().contains(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + commentId));
    }

    @Test
    void save() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Comment savedComment = commentService.save(new Comment());
        assertNotNull(savedComment);
        assertEquals(comment.getContent(), savedComment.getContent());
    }

    @Test
    void save_notSaved() {
        when(commentRepository.save(any(Comment.class))).thenReturn(null);
        Comment savedComment = commentService.save(new Comment());
        assertNull(savedComment);
    }

    @Test
    void update() {
        Long commentId = 1L;
        Comment updatedDetails = new Comment();
        updatedDetails.setContent("Updated Comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.update(commentId, updatedDetails);
        assertNotNull(result);
        assertEquals(updatedDetails.getContent(), result.getContent());
    }
    @Test
    void update_NotFound() {
        Long commentId = 1L;
        Comment updatedDetails = new Comment();
        updatedDetails.setContent("Updated Comment");

        when(commentRepository.findById(commentId)).thenThrow(new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + commentId));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> commentService.update(commentId, updatedDetails));
        assertTrue(exception.getMessage().contains(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + commentId));
    }

    @Test
    void deleteComment_Success() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);
        commentService.deleteComment(commentId);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_NotFound() {
        Long commentId = 1L;
        doThrow(new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + commentId)).when(commentRepository).findById(commentId);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(commentId));
        assertTrue(exception.getMessage().contains(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + commentId));
    }

    @Test
    void findCommentsByPostId() {
        when(commentRepository.findByPostId(1L)).thenReturn(Arrays.asList(comment));
        List<Comment> result = commentService.findCommentsByPostId(1L);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(comment.getContent(), result.get(0).getContent());
    }

    @Test
    void findCommentsByPostId_Empty() {
        when(commentRepository.findByPostId(1L)).thenReturn(Arrays.asList());
        List<Comment> result = commentService.findCommentsByPostId(1L);
        assertTrue(result.isEmpty());
    }
}
