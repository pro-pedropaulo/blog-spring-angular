package com.example.blog.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Data
@Setter
@Getter
@Table(name = "post")

public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
