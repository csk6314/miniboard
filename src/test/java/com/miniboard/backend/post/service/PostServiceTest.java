package com.miniboard.backend.post.service;

import com.miniboard.backend.member.domain.UserEntity;
import com.miniboard.backend.member.domain.UserRole;
import com.miniboard.backend.member.repository.UserRepository;
import com.miniboard.backend.post.domain.Category;
import com.miniboard.backend.post.domain.Post;
import com.miniboard.backend.post.domain.PostCategory;
import com.miniboard.backend.post.dto.PostDetailResponseDto;
import com.miniboard.backend.post.repository.CategoryRepository;
import com.miniboard.backend.post.repository.PostCategoryRepository;
import com.miniboard.backend.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostCategoryRepository postCategoryRepository;

    @InjectMocks
    private PostService postService;

    private UserEntity user;
    private Post post;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        user = new UserEntity("test@test.com", "1234", "tester", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        post = new Post(user, "제목", "내용");
        ReflectionTestUtils.setField(post, "id", 100L);
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.of(2026, 3, 19, 10, 0));
        ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.of(2026, 3, 19, 11, 0));
        ReflectionTestUtils.setField(post, "viewCount", 0);

        category1 = new Category(user, "백엔드");
        ReflectionTestUtils.setField(category1, "id", 10L);

        category2 = new Category(user, "JPA");
        ReflectionTestUtils.setField(category2, "id", 20L);
    }

    @Nested
    @DisplayName("createPost")
    class CreatePostTest {

        @Test
        @DisplayName("게시글 생성 성공 - 카테고리 연결까지 저장한다")
        void createPost_success() {
            // given
            List<Long> categoryIds = List.of(10L, 20L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(10L)).thenReturn(Optional.of(category1));
            when(categoryRepository.findById(20L)).thenReturn(Optional.of(category2));

            doAnswer(invocation -> {
                Post savedPost = invocation.getArgument(0);
                ReflectionTestUtils.setField(savedPost, "id", 100L);
                return null;
            }).when(postRepository).save(any(Post.class));

            // when
            Long postId = postService.createPost(1L, "제목", "내용", categoryIds);

            // then
            assertThat(postId).isEqualTo(100L);

            ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
            verify(postRepository).save(postCaptor.capture());

            Post savedPost = postCaptor.getValue();
            assertThat(savedPost.getTitle()).isEqualTo("제목");
            assertThat(savedPost.getContent()).isEqualTo("내용");
            assertThat(savedPost.getAuthor()).isEqualTo(user);

            ArgumentCaptor<PostCategory> postCategoryCaptor = ArgumentCaptor.forClass(PostCategory.class);
            verify(postCategoryRepository, times(2)).save(postCategoryCaptor.capture());

            List<PostCategory> savedPostCategories = postCategoryCaptor.getAllValues();
            assertThat(savedPostCategories).hasSize(2);
            assertThat(savedPostCategories)
                    .extracting(pc -> pc.getCategory().getId())
                    .containsExactlyInAnyOrder(10L, 20L);
        }

        @Test
        @DisplayName("게시글 생성 실패 - 유저가 없으면 예외")
        void createPost_fail_userNotFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.createPost(1L, "제목", "내용", List.of(10L)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유저를 찾을 수 없습니다.");

            verify(postRepository, never()).save(any());
            verify(postCategoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("게시글 생성 실패 - 존재하지 않는 카테고리면 예외")
        void createPost_fail_categoryNotFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.createPost(1L, "제목", "내용", List.of(10L)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("존재하지 않는 카테고리입니다.");

            verify(postRepository).save(any(Post.class));
            verify(postCategoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getPostDetail")
    class GetPostDetailTest {

        @Test
        @DisplayName("게시글 상세 조회 성공 - 카테고리 포함 DTO 반환")
        void getPostDetail_success() {
            // given
            ReflectionTestUtils.setField(post, "viewCount", 7);

            when(postRepository.findById(100L)).thenReturn(Optional.of(post));
            when(postCategoryRepository.findAllByPostId(100L))
                    .thenReturn(List.of(new PostCategory(post, category1), new PostCategory(post, category2)));

            // when
            PostDetailResponseDto result = postService.getPostDetail(100L);

            // then
            assertThat(result.id()).isEqualTo(100L);
            assertThat(result.title()).isEqualTo("제목");
            assertThat(result.content()).isEqualTo("내용");
            assertThat(result.authorName()).isEqualTo("tester");
            assertThat(result.categories()).containsExactly("백엔드", "JPA");
            //assertThat(result.viewCount()).isEqualTo(8); // 조회 시 증가한다고 가정
            assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2026, 3, 19, 10, 0));
            assertThat(result.updatedAt()).isEqualTo(LocalDateTime.of(2026, 3, 19, 11, 0));
        }

        @Test
        @DisplayName("게시글 상세 조회 실패 - 게시글이 없으면 예외")
        void getPostDetail_fail_postNotFound() {
            // given
            when(postRepository.findById(100L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.getPostDetail(100L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("존재하지 않는 게시글입니다.");

            verify(postCategoryRepository, never()).findAllByPostId(anyLong());
        }
    }

    @Nested
    @DisplayName("updatePost")
    class UpdatePostTest {

        @Test
        @DisplayName("게시글 수정 성공 - 기존 카테고리 삭제 후 새 카테고리로 재생성")
        void updatePost_success() {
            // given
            List<Long> newCategoryIds = List.of(20L);

            when(postRepository.findById(100L)).thenReturn(Optional.of(post));
            when(categoryRepository.findById(20L)).thenReturn(Optional.of(category2));

            // when
            postService.updatePost(100L, 1L, "수정 제목", "수정 내용", newCategoryIds);

            // then
            assertThat(post.getTitle()).isEqualTo("수정 제목");
            assertThat(post.getContent()).isEqualTo("수정 내용");

            verify(postCategoryRepository).deleteByPostId(100L);
            verify(postCategoryRepository, times(1)).save(any(PostCategory.class));
        }

        @Test
        @DisplayName("게시글 수정 실패 - 작성자가 아니면 예외")
        void updatePost_fail_notWriter() {
            // given
            when(postRepository.findById(100L)).thenReturn(Optional.of(post));

            // when & then
            assertThatThrownBy(() ->
                    postService.updatePost(100L, 999L, "수정 제목", "수정 내용", List.of(10L)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("작성자만 게시글을 수정/삭제할 수 있습니다.");

            verify(postCategoryRepository, never()).deleteByPostId(anyLong());
            verify(postCategoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deletePost")
    class DeletePostTest {

        @Test
        @DisplayName("게시글 삭제 성공 - 연결 카테고리 먼저 삭제 후 게시글 삭제")
        void deletePost_success() {
            // given
            when(postRepository.findById(100L)).thenReturn(Optional.of(post));

            // when
            postService.deletePost(100L, 1L);

            // then
            verify(postCategoryRepository).deleteByPostId(100L);
            verify(postRepository).delete(post);
        }

        @Test
        @DisplayName("게시글 삭제 실패 - 작성자가 아니면 예외")
        void deletePost_fail_notWriter() {
            // given
            when(postRepository.findById(100L)).thenReturn(Optional.of(post));

            // when & then
            assertThatThrownBy(() -> postService.deletePost(100L, 999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("작성자만 게시글을 수정/삭제할 수 있습니다.");

            verify(postCategoryRepository, never()).deleteByPostId(anyLong());
            verify(postRepository, never()).delete(any());
        }
    }
}