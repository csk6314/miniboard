package com.miniboard.backend.post.repository;

import com.miniboard.backend.member.domain.UserEntity;
import com.miniboard.backend.member.domain.UserRole;
import com.miniboard.backend.member.repository.UserRepository;
import com.miniboard.backend.post.domain.Post;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PostRepository.class, UserRepository.class})
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("게시글 저장")
    void saveTest() {
        UserEntity author = new UserEntity("test@gmail.com", "1234", "테스트계정1", UserRole.USER);

        userRepository.save(author);

        Post post = new Post(author, "테스트 게시글", "테스트 내용 12341234");

        postRepository.save(post);

        em.flush();
        em.clear();

        Optional<Post> result = postRepository.findById(post.getId());

        assertThat(result).isPresent();

        assertAll(
                () -> assertThat(result.get().getAuthor().getNickname()).isEqualTo("테스트계정1"),
                () -> assertThat(result.get().getTitle()).isEqualTo("테스트 게시글"),
                () -> assertThat(result.get().getContent()).isEqualTo("테스트 내용 12341234")
        );

    }


    @Test
    @DisplayName("유저 ID로 게시글 조회")
    void findAllByUserIdTest() {
        UserEntity user1 = new UserEntity("u1@test.com", "1234", "user1", UserRole.USER);
        UserEntity user2 = new UserEntity("u2@test.com", "1234", "user2", UserRole.USER);
        userRepository.save(user1);
        userRepository.save(user2);

        Post post1 = new Post(user1, "u1-게시글1", "내용1");
        Post post2 = new Post(user2 ,"u2-게시글1", "내용2");
        Post post3 = new Post(user1, "u1-게시글2", "내용3");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        em.flush();
        em.clear();

        List<Post> result = postRepository.findAllByUserId(user1.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("title")
                .containsExactly("u1-게시글2", "u1-게시글1");
    }


    @Test
    @DisplayName("Post Id로 게시글 삭제")
    void deleteById() {
        UserEntity user1 = new UserEntity("u1@test.com", "1234", "user1", UserRole.USER);
        userRepository.save(user1);

        Post post1 = new Post(user1, "u1-게시글1", "내용1");
        Post post3 = new Post(user1, "u1-게시글2", "내용2");

        postRepository.save(post1);
        postRepository.save(post3);

        em.flush();
        em.clear();

        postRepository.deleteById(post1.getId());

        em.flush();
        em.clear();

        Optional<Post> found = postRepository.findById(post1.getId());
        assertThat(found).isEmpty();
    }
}