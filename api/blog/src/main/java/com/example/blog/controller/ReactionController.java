package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class ReactionController {

    @Autowired
    private ReactionService reactionService;

    @PostMapping("/{postId}/react")
    public ResponseEntity<?> reactToPost(@PathVariable Long postId, @RequestBody Reaction reaction) {
        String username = reaction.getUsername();
        Post updatedPost = reactionService.saveOrUpdateReaction(reaction, postId, username);

        if (updatedPost != null) {
            return ResponseEntity.ok(updatedPost);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
