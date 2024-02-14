package com.example.blog.controller;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.security.JWTUtil;
import com.example.blog.service.CloudinaryService;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import com.example.blog.util.ExceptionMessages;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private PostController postController;

    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        openMocks(this);
        user = new User();
        user.setUsername("user");
        user.setId(1L);

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setApp_user(user);
        post.setLikes(new HashSet<>(Arrays.asList("user1")));
        post.setDislikeCount(0L);
    }

    @Test
    void getAllPosts() {
        when(postService.findAll()).thenReturn(Arrays.asList(post));
        List<Post> result = postController.getAllPosts();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
    }

    @Test
    void getPostById_Found() {
        when(postService.findById(1L)).thenReturn(post);
        ResponseEntity<Post> result = postController.getPostById(1L);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(post, result.getBody());
    }

    @Test
    void getPostById_NotFound() {
        when(postService.findById(1L)).thenThrow(new ResourceNotFoundException(ExceptionMessages.POST_NOT_FOUND_WITH_ID_ONE));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            postController.getPostById(1L);
        });
        assertTrue(exception.getMessage().contains(ExceptionMessages.POST_NOT_FOUND_WITH_ID_ONE));
    }


    @Test
    void createPost_AuthorizedAndSuccessful() {
        HttpServletRequest request = new MockHttpServletRequest();
        String token = "Bearer valid_token";
        ((MockHttpServletRequest) request).addHeader("Authorization", token);

        when(jwtUtil.getUsernameFromToken("valid_token")).thenReturn(user.getUsername());
        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(postService.save(any(Post.class))).thenReturn(post);

        ResponseEntity<Post> result = postController.createPost(post, request);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(post, result.getBody());
    }

    @Test
    void createPost_Unauthorized() {
        HttpServletRequest request = new MockHttpServletRequest();
        String token
                = "Bearer invalid_token";
        ((MockHttpServletRequest) request).addHeader("Authorization", token);

        when(jwtUtil.getUsernameFromToken("invalid_token")).thenReturn(null);
    }

    @Test
    void updatePost_Found() {
        when(postService.update(eq(1L), any(Post.class))).thenReturn(Optional.of(post));
        ResponseEntity<Post> result = postController.updatePost(1L, post);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(post, result.getBody());
    }

    @Test
    void updatePost_NotFound() {
        when(postService.update(eq(1L), any(Post.class))).thenThrow(new ResourceNotFoundException(ExceptionMessages.POST_NOT_FOUND_WITH_ID_ONE));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            postController.updatePost(1L, post);
        });
        assertTrue(exception.getMessage().contains(ExceptionMessages.POST_NOT_FOUND_WITH_ID_ONE));
    }


    @Test
    void deletePost_Success() {
        doNothing().when(postService).deletePost(1L);
        ResponseEntity<?> result = postController.deletePost(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deletePost_NotFound() {
        doThrow(new ResourceNotFoundException(ExceptionMessages.POST_NOT_FOUND_WITH_ID_ONE))
                .when(postService).deletePost(1L);
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            postController.deletePost(1L);
        });
        assertTrue(exception.getMessage().contains(ExceptionMessages.POST_NOT_FOUND_WITH_ID_ONE));
    }

    @Test
    void handleImageUpload_Success() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "test1.jpg", "image/jpeg", "Test Image 1 Content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", "image/jpeg", "Test Image 2 Content".getBytes());

        when(cloudinaryService.uploadFile(any(MultipartFile.class)))
                .thenReturn("http://image.url/test1.jpg", "http://image.url/test2.jpg");

        ResponseEntity<?> result = postController.handleImageUpload(new MultipartFile[]{file1, file2});
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertTrue(body.containsKey("imageUrls"));

        List<String> imageUrls = (List<String>) body.get("imageUrls");
        assertEquals(2, imageUrls.size());
        assertTrue(imageUrls.contains("http://image.url/test1.jpg"));
        assertTrue(imageUrls.contains("http://image.url/test2.jpg"));
    }
    @Test
    void likePost_Success() {
        HttpServletRequest request = new MockHttpServletRequest();
        String token = "Bearer valid_token";
        ((MockHttpServletRequest) request).addHeader("Authorization", token);

        when(jwtUtil.getUsernameFromToken("valid_token")).thenReturn(user.getUsername());
        ResponseEntity<?> response = postController.likePost(1L, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void likePost_Failure() {
        HttpServletRequest request = new MockHttpServletRequest();
        String token = "Bearer invalid_token";
        ((MockHttpServletRequest) request).addHeader("Authorization", token);

        when(jwtUtil.getUsernameFromToken("invalid_token")).thenReturn(null);
        ResponseEntity<?> response = postController.likePost(1L, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void dislikePost_Success() {
        HttpServletRequest request = new MockHttpServletRequest();
        String token = "Bearer valid_token";
        ((MockHttpServletRequest) request).addHeader("Authorization", token);

        when(jwtUtil.getUsernameFromToken("valid_token")).thenReturn(user.getUsername());
        ResponseEntity<?> response = postController.dislikePost(1L, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void dislikePost_Failure() {
        HttpServletRequest request = new MockHttpServletRequest();
        String token
                = " Bearer invalid_token ";
        ((MockHttpServletRequest) request).addHeader("Authorization", token);

        when(jwtUtil.getUsernameFromToken("invalid_token")).thenReturn(null);
        ResponseEntity<?> response = postController.dislikePost(1L, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
