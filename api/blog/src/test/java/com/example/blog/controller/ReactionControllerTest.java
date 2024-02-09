package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ReactionControllerTest {

    @Mock
    private ReactionService reactionService;

    @InjectMocks
    private ReactionController reactionController;

    private Reaction reaction;
    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test content");

        reaction = new Reaction();
        reaction.setId(1L);
        reaction.setPost(post);
        reaction.setUsername("user");
        reaction.setReactionLike(true);
    }

    @Test
    void reactToPost_Success() {
        when(reactionService.saveOrUpdateReaction(any(Reaction.class), any(Long.class), any(String.class))).thenReturn(post);
        ResponseEntity<?> response = reactionController.reactToPost(post.getId(), reaction);
        assertEquals(ResponseEntity.ok(post), response);
        assertEquals(post, response.getBody());
    }

    @Test
    void reactToPost_Failure() {
        when(reactionService.saveOrUpdateReaction(any(Reaction.class), any(Long.class), any(String.class))).thenReturn(null);
        ResponseEntity<?> response = reactionController.reactToPost(post.getId(), reaction);
        assertEquals(ResponseEntity.badRequest().build(), response);
    }
}
