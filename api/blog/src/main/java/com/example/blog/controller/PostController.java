package com.example.blog.controller;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.security.JWTUtil;
import com.example.blog.service.CloudinaryService;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    public CloudinaryService cloudinaryService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                User user = userService.findByUsername(username);

                if (user != null) {
                    post.setApp_user(user);
                    post = postService.save(post);
                    return ResponseEntity.status(HttpStatus.CREATED).body(post);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        return postService.update(id, postDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-images")
    public ResponseEntity<?> handleImageUpload(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadFile(file);
                    imageUrls.add(imageUrl);
                }
            }
            return ResponseEntity.ok().body(Map.of("imageUrls", imageUrls));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@Valid @PathVariable Long id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            postService.likePost(id, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing like");
        }
    }


    @PostMapping("/{id}/dislike")
    public ResponseEntity<?> dislikePost(@Valid @PathVariable Long id, HttpServletRequest request) {
        String username = jwtUtil.getUsernameFromToken(request.getHeader("Authorization").substring(7));
        try {
            postService.dislikePost(id, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing dislike");
        }
    }

}
