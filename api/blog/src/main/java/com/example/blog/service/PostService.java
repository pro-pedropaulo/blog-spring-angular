package com.example.blog.service;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReactionRepository;
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
                new ResourceNotFoundException("Post not found with id: " + id));
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

    @Transactional
    public boolean delete(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    commentRepository.deleteByPostId(id);
                    reactionRepository.deleteByPostId(id);
                    postRepository.delete(post);
                    return true;
                }).orElse(false);
    }


    public Post likePost(Long postId, String username) {
        return postRepository.findById(postId).map(post -> {
            post.getDislikes().remove(username);

            if (!post.getLikes().add(username)) {
                post.getLikes().remove(username);
            }

            return postRepository.save(post);
        }).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public Post dislikePost(Long postId, String username) {
        return postRepository.findById(postId).map(post -> {
            post.getLikes().remove(username);

            if (!post.getDislikes().add(username)) {
                post.getDislikes().remove(username);
            }

            return postRepository.save(post);
        }).orElseThrow(() -> new RuntimeException("Post not found"));
    }

}
