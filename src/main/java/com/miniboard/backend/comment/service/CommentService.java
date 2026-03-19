package com.miniboard.backend.comment.service;

import com.miniboard.backend.comment.domain.Comment;
import com.miniboard.backend.comment.dto.CommentResponseDto;
import com.miniboard.backend.comment.repository.CommentRepository;
import com.miniboard.backend.member.domain.UserEntity;
import com.miniboard.backend.member.repository.UserRepository;
import com.miniboard.backend.post.domain.Post;
import com.miniboard.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createComment(Long postId, Long userId, Long parentId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        UserEntity author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        Comment parent = null;

        if (parentId != null) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));

            if (parent.getParent() != null) {
                throw new IllegalArgumentException("대댓글에는 댓글을 작성할 수 없습니다.");
            }
        }

        Comment comment = new Comment(post, author, parent, content);
        commentRepository.save(comment);

        return comment.getId();
    }

    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        Map<Long, List<Comment>> childrenMap = new HashMap<>();
        List<Comment> parents = new ArrayList<>();

        divideComments(comments, parents, childrenMap);

        return parents.stream()
                .map(parent -> CommentResponseDto.from(
                        parent,
                        childrenMap.getOrDefault(parent.getId(), List.of())
                                .stream()
                                .map(child -> CommentResponseDto.from(
                                        child, List.of()
                                ))
                                .toList()
                ))
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if(!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        comment.setDeleted(true);
    }


    private void divideComments(
            List<Comment> comments,
            List<Comment> parents,
            Map<Long, List<Comment>> childrenMap
    ) {
        for (Comment comment : comments) {
            if (comment.getParent() == null) {
                parents.add(comment);
                continue;
            }

            childrenMap.computeIfAbsent(comment.getParent().getId(), k -> new ArrayList<>())
                    .add(comment);
        }
    }


}
