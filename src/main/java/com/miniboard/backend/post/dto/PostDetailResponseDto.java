package com.miniboard.backend.post.dto;

import com.miniboard.backend.post.domain.Post;
import com.miniboard.backend.post.domain.PostCategory;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponseDto(
        Long id,
        String title,
        String content,
        String authorName,
        int viewCount,
        List<String> categories,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostDetailResponseDto from(Post post, List<PostCategory> categories) {
        return new PostDetailResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                post.getViewCount(),
                categories.stream()
                        .map((pc)->pc.getCategory().getName())
                        .toList(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
