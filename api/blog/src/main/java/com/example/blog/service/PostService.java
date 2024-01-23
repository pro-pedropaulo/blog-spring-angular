package com.example.blog.service;

import com.example.blog.model.Post;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
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

    public boolean delete(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    postRepository.delete(post);
                    return true;
                }).orElse(false);
    }
}
