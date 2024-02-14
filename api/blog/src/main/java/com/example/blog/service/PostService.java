package com.example.blog.service;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReactionRepository;
import com.example.blog.util.ExceptionMessages;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ExceptionMessages.POST_NOT_FOUND_WITH_ID + id));
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> update(Long id, Post postDetails) {
        return postRepository.findById(id)
                .map(existingPost -> {
                    existingPost.setTitle(postDetails.getTitle());
                    existingPost.setContent(postDetails.getContent());
                    return postRepository.save(existingPost);
                });
    }

    @Operation(summary = "first delete all comments and reactions associated with the post")
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.POST_NOT_FOUND_WITH_ID + id));

        commentRepository.deleteByPostId(id);
        reactionRepository.deleteByPostId(id);

        postRepository.delete(post);
    }

    @Operation(summary = "Like a post")
    public Post likePost(Long postId, String username) {
        return postRepository.findById(postId).map(post -> {
            post.getDislikes().remove(username);

            if (!post.getLikes().add(username)) {
                post.getLikes().remove(username);
            }

            return postRepository.save(post);
        }).orElseThrow(() -> new RuntimeException(ExceptionMessages.POST_NOT_FOUND));
    }

    @Operation(summary = "Dislike a post")
    public Post dislikePost(Long postId, String username) {
        return postRepository.findById(postId).map(post -> {
            post.getLikes().remove(username);

            if (!post.getDislikes().add(username)) {
                post.getDislikes().remove(username);
            }

            return postRepository.save(post);
        }).orElseThrow(() -> new RuntimeException(ExceptionMessages.POST_NOT_FOUND));
    }

}
