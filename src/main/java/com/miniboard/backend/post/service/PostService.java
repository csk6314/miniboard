package com.miniboard.backend.post.service;

import com.miniboard.backend.member.domain.UserEntity;
import com.miniboard.backend.member.repository.UserRepository;
import com.miniboard.backend.post.domain.Post;
import com.miniboard.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(Long userId, String title, String content) {
        UserEntity user =  userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Post post = new Post(user, title, content);

        postRepository.save(post);

        return post.getId();
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public void updatePost(Long postId, Long userId, String title, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        vaildateWriter(post, userId);

        post.updateTitle(title);
        post.updateContent(content);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글입니다."));

        vaildateWriter(post, userId);

        postRepository.delete(post);
    }

    private void vaildateWriter(Post post, Long userId) {
        if(!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 게시글을 수정/삭제할 수 있습니다.");
        }
    }


}
