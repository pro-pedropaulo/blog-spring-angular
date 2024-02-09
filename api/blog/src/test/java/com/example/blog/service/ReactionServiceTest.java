package com.example.blog.service;

import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReactionRepository;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ReactionService reactionService;

    private User user;
    private Post post;
    private Reaction reaction;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test content");

        reaction = new Reaction();
        reaction.setId(1L);
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setReactionLike(true);
    }

    @Test
    void saveOrUpdateReaction_NewReaction() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(Reaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post savedPost = reactionService.saveOrUpdateReaction(reaction, post.getId(), user.getUsername());

        assertNotNull(savedPost);
        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void saveOrUpdateReaction_UpdateExistingReaction() {
        reaction.setReactionLike(false);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(reaction));
        when(reactionRepository.save(any(Reaction.class))).thenReturn(reaction);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post updatedPost = reactionService.saveOrUpdateReaction(reaction, post.getId(), user.getUsername());

        assertNotNull(updatedPost);
        verify(reactionRepository, times(1)).save(reaction);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void saveOrUpdateReaction_UserNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        Post result = reactionService.saveOrUpdateReaction(reaction, post.getId(), "unknownUser");

        assertNull(result);
        verify(reactionRepository, never()).save(any(Reaction.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void saveOrUpdateReaction_PostNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        Post result = reactionService.saveOrUpdateReaction(reaction, 2L, user.getUsername());

        assertNull(result);
        verify(reactionRepository, never()).save(any(Reaction.class));
        verify(postRepository, never()).save(any(Post.class));
    }
}
