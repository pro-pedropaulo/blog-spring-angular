package com.example.blog.controller;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.CommentService;
import com.example.blog.service.UserService;
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
        return commentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment commentDetails) {
        return commentService.update(id, commentDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
        }

        Comment comment = commentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Post post = comment.getPost();
        if (post == null || !post.getApp_user().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to delete this comment");
        }

        commentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.findCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public Comment createComment(@RequestBody Comment comment) {
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
