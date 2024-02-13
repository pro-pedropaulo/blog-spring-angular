//package com.example.blog.service;
//
//import com.example.blog.model.Comment;
//import com.example.blog.repository.CommentRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//class CommentServiceTest {
//
//    @Mock
//    private CommentRepository commentRepository;
//
//    @InjectMocks
//    private CommentService commentService;
//
//    @Captor
//    private ArgumentCaptor<Comment> commentCaptor;
//
//    private Comment comment;
//
//    @BeforeEach
//    void setUp() {
//        comment = new Comment();
//        comment.setId(1L);
//        comment.setContent("Test Comment");
//        comment.setPostId(1L);
//    }
//
//    @Test
//    void findAll() {
//        when(commentRepository.findAll()).thenReturn(Arrays.asList(comment));
//        List<Comment> result = commentService.findAll();
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        assertEquals(comment.getContent(), result.get(0).getContent());
//    }
//
//    @Test
//    void findById() {
//        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
//        Optional<Comment> result = commentService.findById(1L);
//        assertTrue(result.isPresent());
//        assertEquals(comment.getContent(), result.get().getContent());
//    }
//
//    @Test
//    void save() {
//        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
//        Comment savedComment = commentService.save(new Comment());
//        assertNotNull(savedComment);
//        assertEquals(comment.getContent(), savedComment.getContent());
//    }
//
//    @Test
//    void update() {
//        Comment updatedDetails = new Comment();
//        updatedDetails.setContent("Updated Comment");
//        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
//        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
//
//        Optional<Comment> updatedComment = commentService.update(1L, updatedDetails);
//
//        assertTrue(updatedComment.isPresent());
//        assertEquals(updatedDetails.getContent(), updatedComment.get().getContent());
//        verify(commentRepository).save(commentCaptor.capture());
//        assertEquals("Updated Comment", commentCaptor.getValue().getContent());
//    }
//
//    @Test
//    void delete() {
//        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
//        doNothing().when(commentRepository).delete(any(Comment.class));
//
//        boolean isDeleted = commentService.delete(1L);
//        assertTrue(isDeleted);
//        verify(commentRepository, times(1)).delete(comment);
//    }
//
//    @Test
//    void findCommentsByPostId() {
//        when(commentRepository.findByPostId(1L)).thenReturn(Arrays.asList(comment));
//        List<Comment> result = commentService.findCommentsByPostId(1L);
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        assertEquals(comment.getContent(), result.get(0).getContent());
//    }
//}
