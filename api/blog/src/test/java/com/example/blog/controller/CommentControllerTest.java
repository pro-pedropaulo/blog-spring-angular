//package com.example.blog.controller;
//
//import com.example.blog.model.Comment;
//import com.example.blog.model.Post;
//import com.example.blog.model.User;
//import com.example.blog.repository.PostRepository;
//import com.example.blog.service.CommentService;
//import com.example.blog.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.test.context.support.WithMockUser;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.mockito.MockitoAnnotations.openMocks;
//
//class CommentControllerTest {
//
//    @Mock
//    private CommentService commentService;
//
//    @Mock
//    private PostRepository postRepository;
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private CommentController commentController;
//
//    private Comment comment;
//    private Post post;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        openMocks(this);
//
//        post = new Post();
//        post.setId(1L);
//
//        user = new User();
//        user.setUsername("user");
//
//        comment = new Comment();
//        comment.setId(1L);
//        comment.setPostId(post.getId());
//        comment.setContent("Test Comment");
//        comment.setApp_user(user);
//    }
//
//    @Test
//    void getAllComments() {
//        when(commentService.findAll()).thenReturn(Arrays.asList(comment));
//        List<Comment> result = commentController.getAllComments();
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void getCommentById_Found() {
//        when(commentService.findById(1L)).thenReturn(Optional.of(comment));
//        ResponseEntity<Comment> result = commentController.getCommentById(1L);
//        assertTrue(result.getStatusCode().is2xxSuccessful());
//        assertEquals(comment, result.getBody());
//    }
//
//    @Test
//    void getCommentById_NotFound() {
//        when(commentService.findById(1L)).thenReturn(Optional.empty());
//        ResponseEntity<Comment> result = commentController.getCommentById(1L);
//        assertTrue(result.getStatusCode().is4xxClientError());
//    }
//
//    @Test
//    void updateComment_Found() {
//        when(commentService.update(any(Long.class), any(Comment.class))).thenReturn(Optional.of(comment));
//        ResponseEntity<Comment> result = commentController.updateComment(1L, comment);
//        assertTrue(result.getStatusCode().is2xxSuccessful());
//        assertEquals(comment, result.getBody());
//    }
//
//    @Test
//    void updateComment_NotFound() {
//        when(commentService.update(any(Long.class), any(Comment.class))).thenReturn(Optional.empty());
//        ResponseEntity<Comment> result = commentController.updateComment(1L, comment);
//        assertTrue(result.getStatusCode().is4xxClientError());
//    }
//
//    @Test
//    void deleteComment_Success() {
//        when(commentService.findById(1L)).thenReturn(Optional.of(comment));
//        assertDoesNotThrow(() -> commentController.deleteComment(1L));
//    }
//
//    @Test
//    void getCommentsByPostId() {
//        when(commentService.findCommentsByPostId(post.getId())).thenReturn(Arrays.asList(comment));
//        ResponseEntity<List<Comment>> result = commentController.getCommentsByPostId(post.getId());
//        assertFalse(result.getBody().isEmpty());
//        assertEquals(1, result.getBody().size());
//    }
//
//    @Test
//    void createComment_PostNotFound() {
//        when(postRepository.findById(comment.getPostId())).thenReturn(Optional.empty());
//        Exception exception = assertThrows(RuntimeException.class, () -> commentController.createComment(comment));
//        assertEquals("Post not found", exception.getMessage());
//    }
//
//    @Test
//    void createComment_Success() {
//        when(postRepository.findById(comment.getPostId())).thenReturn(Optional.of(post));
//        when(userService.findByUsername(any(String.class))).thenReturn(user);
//        when(commentService.save(any(Comment.class))).thenReturn(comment);
//
//        Comment result = commentController.createComment(comment);
//        assertNotNull(result);
//        assertEquals(comment.getContent(), result.getContent());
//        assertEquals(comment.getPostId(), result.getPost().getId());
//        assertEquals(comment.getApp_user().getUsername(), result.getApp_user().getUsername());
//    }
//}
