package com.example.blog.controller;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.CommentService;
import com.example.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @Valid @RequestBody Comment commentDetails) {
        Comment updatedComment = commentService.update(id, commentDetails);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.findCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Create a new comment for a post")
    @PostMapping
    public Comment createComment(@Valid @RequestBody Comment comment) {
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        comment.setPost(post);

        if (comment.getApp_user() != null && comment.getApp_user().getUsername() != null) {
            User user = userService.findByUsername(comment.getApp_user().getUsername());
            if (user != null) {
                comment.setApp_user(user);
            } else {
                comment.setApp_user(null);
            }
        } else {
            comment.setApp_user(null);
        }
        return commentService.save(comment);
    }

}
