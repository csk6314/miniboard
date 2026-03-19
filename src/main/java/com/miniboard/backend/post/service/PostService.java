package com.miniboard.backend.post.service;

import com.miniboard.backend.member.domain.UserEntity;
import com.miniboard.backend.member.repository.UserRepository;
import com.miniboard.backend.post.domain.Category;
import com.miniboard.backend.post.domain.Post;
import com.miniboard.backend.post.domain.PostCategory;
import com.miniboard.backend.post.dto.PostDetailResponseDto;
import com.miniboard.backend.post.repository.CategoryRepository;
import com.miniboard.backend.post.repository.PostCategoryRepository;
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

    private final CategoryRepository categoryRepository;
    private final PostCategoryRepository postCategoryRepository;

    @Transactional
    public Long createPost(Long userId, String title, String content, List<Long> categoryIds) {
        UserEntity user =  userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Post post = new Post(user, title, content);

        postRepository.save(post);

        addCategories(userId, categoryIds, post);

        return post.getId();
    }

    private void addCategories(Long userId, List<Long> categoryIds, Post post) {
        for(Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(()->new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

            validateCategoryOwner(userId, category);

            PostCategory postCategory = new PostCategory(post, category);
            postCategoryRepository.save(postCategory);
        }
    }

    private void validateCategoryOwner(Long userId, Category category) {
        if(!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 만든 카테고리만 수정할 수 있습니다");
        }
    }

    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        List<PostCategory> categories = postCategoryRepository.findByPostId(postId);

        return PostDetailResponseDto.from(post, categories);
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
