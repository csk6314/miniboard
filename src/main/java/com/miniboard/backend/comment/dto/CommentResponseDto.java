package com.miniboard.backend.comment.dto;

import com.miniboard.backend.comment.domain.Comment;

import java.util.List;

public record CommentResponseDto(
        Long id,
        String nickname,
        String content,
        List<CommentResponseDto> children

) {
    public static CommentResponseDto from(Comment comment, List<CommentResponseDto> children) {
        return new CommentResponseDto(
                comment.getId(),
                comment.isDelete() ? null : comment.getAuthor().getNickname(),
                comment.isDelete() ? "삭제된 댓글입니다." : comment.getContent(),
                children
        );
    }
}
