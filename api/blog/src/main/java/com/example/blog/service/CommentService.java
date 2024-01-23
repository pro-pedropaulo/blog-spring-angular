package com.example.blog.service;

import com.example.blog.model.Comment;
import com.example.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Comment> update(Long id, Comment commentDetails) {
        return commentRepository.findById(id)
                .map(existingComment -> {
                    existingComment.setContent(commentDetails.getContent());
                    return commentRepository.save(existingComment);
                });
    }

    public boolean delete(Long id) {
        return commentRepository.findById(id)
                .map(comment -> {
                    commentRepository.delete(comment);
                    return true;
                }).orElse(false);
    }
}
