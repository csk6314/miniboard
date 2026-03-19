package com.miniboard.backend.comment.service;

import com.miniboard.backend.comment.domain.Comment;
import com.miniboard.backend.comment.dto.CommentResponseDto;
import com.miniboard.backend.comment.repository.CommentRepository;
import com.miniboard.backend.member.domain.UserEntity;
import com.miniboard.backend.member.domain.UserRole;
import com.miniboard.backend.post.domain.Post;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("부모 댓글에 자식 댓글이 정상적으로 매핑된다")
    void getComments_structure() {
        // given
        UserEntity user = new UserEntity("test@test.com","1234", "테스트", UserRole.USER);
        Post post = new Post(user, "테스트","테서트");

        Comment parent = new Comment(post, user, null, "댓글댓글");
        Comment child1 = new Comment(post, user,    parent, "대댓글1");
        Comment child2 = new Comment(post, user, parent, "대댓글2");

        ReflectionTestUtils.setField(parent, "id", 1L);
        ReflectionTestUtils.setField(child1, "id", 2L);
        ReflectionTestUtils.setField(child2, "id", 3L);

        List<Comment> comments = List.of(parent, child1, child2);

        given(commentRepository.findByPostId(post.getId()))
                .willReturn(comments);

        child1.setDeleted(true);

        // when
        List<CommentResponseDto> result = commentService.getCommentsByPostId(post.getId());

        System.out.println(result.toString());

        // then
        assertThat(result).hasSize(1); // 부모 댓글 1개

        CommentResponseDto parentDto = result.get(0);

        assertThat(parentDto.id()).isEqualTo(1L);
        assertThat(parentDto.children()).hasSize(2);

        assertThat(parentDto.children())
                .extracting(CommentResponseDto::id)
                .containsExactlyInAnyOrder(2L, 3L);
    }


}