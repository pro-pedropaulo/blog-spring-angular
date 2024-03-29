package com.example.blog.service;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.Comment;
import com.example.blog.repository.CommentRepository;
import com.example.blog.util.ExceptionMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + id));
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment update(Long id, Comment commentDetails) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + id));

        existingComment.setContent(commentDetails.getContent());

        return commentRepository.save(existingComment);
    }


    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND_WITH_ID + id));
        commentRepository.delete(comment);
    }

    public List<Comment> findCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }
}
