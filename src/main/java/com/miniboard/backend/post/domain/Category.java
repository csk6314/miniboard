package com.miniboard.backend.post.domain;

import com.miniboard.backend.domain.BaseEntity;
import com.miniboard.backend.member.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Category extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id")
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    public Category(UserEntity user, String name) {
        this.user = user;
        this.name = name;
    }
}
