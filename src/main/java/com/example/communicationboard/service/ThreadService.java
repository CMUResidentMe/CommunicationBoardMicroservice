package com.example.communicationboard.service;

import com.example.communicationboard.model.PostComponent;
import com.example.communicationboard.model.Post;
import com.example.communicationboard.model.Thread;
import com.example.communicationboard.repository.PostRepository;
import com.example.communicationboard.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class ThreadService {

    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    public Thread createThread(Thread thread) {
        return threadRepository.save(thread);
    }

    public Optional<Thread> getThread(String id) {
        return threadRepository.findById(id);
    }

    public List<Thread> getAllThreads() {
        return threadRepository.findAll();
    }

    @Transactional
    public void deleteThread(String id, String userId, String privilege) {
        Thread thread = threadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found with id: " + id));

        if (!privilege.equals("manager") && !thread.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this thread.");
        }
        // Delegate deletion of children posts to PostService
        thread.getChildren().forEach(post -> postService.deletePost(post.getId(), userId, "manager"));
        threadRepository.delete(thread);
    }

    public Post addPostToThread(String threadId, Post post) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found with id: " + threadId));
        post = postRepository.save(post);
        thread.addChild(post);
        threadRepository.save(thread);
        return post;
    }

    public List<PostComponent> findPostsByThreadId(String threadId) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return thread.getChildren();
    }

}
