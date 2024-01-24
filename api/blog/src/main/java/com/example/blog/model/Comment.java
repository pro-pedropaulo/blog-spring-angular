package com.example.blog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Transient
    private String username;

    @ManyToOne
    private User app_user;

    @ManyToOne
    @JsonIgnore
    private Post post;

    @Column(name = "post_id", insertable = false, updatable = false)
    private Long postId;

    private LocalDateTime createdAt = LocalDateTime.now();

    public String getUsername() {
        return app_user != null ? app_user.getUsername() : "Visitante";
    }
}
