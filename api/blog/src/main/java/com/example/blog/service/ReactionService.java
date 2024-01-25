package com.example.blog.service;

import com.example.blog.model.Post;
import com.example.blog.model.Reaction;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ReactionRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public Post saveOrUpdateReaction(Reaction reaction, Long postId, String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            return null;
        }
        User user = userOptional.get();

        Optional<Post> postOptional = postRepository.findById(postId);
        if (!postOptional.isPresent()) {
            return null;
        }
        Post post = postOptional.get();

        Optional<Reaction> existingReaction = reactionRepository.findByUserAndPost(user, post);
        if (existingReaction.isPresent()) {
            Reaction updatedReaction = existingReaction.get();
            updatedReaction.setReactionLike(reaction.isReactionLike());
            reactionRepository.save(updatedReaction);
        } else {
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setReactionLike(reaction.isReactionLike());
            reactionRepository.save(reaction);
        }

        updateReactionCounts(post);
        return postRepository.save(post);
    }

    private void updateReactionCounts(Post post) {
        long likeCount = reactionRepository.countByPostAndReactionLike(post, true);
        long dislikeCount = reactionRepository.countByPostAndReactionLike(post, false);

        post.setLikeCount(likeCount);
        post.setDislikeCount(dislikeCount);
    }
}
