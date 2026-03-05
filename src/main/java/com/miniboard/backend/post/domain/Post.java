package com.miniboard.backend.post.domain;

import com.miniboard.backend.domain.BaseEntity;
import com.miniboard.backend.member.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name="posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private boolean isDelete;

    public Post(UserEntity author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }
}
