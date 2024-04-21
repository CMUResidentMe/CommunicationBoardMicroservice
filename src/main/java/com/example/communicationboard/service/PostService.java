package com.example.communicationboard.service;

import com.example.communicationboard.model.PostComponent;
import com.example.communicationboard.model.Thread;
import com.example.communicationboard.model.Post;
import com.example.communicationboard.model.Reply;
import com.example.communicationboard.repository.ThreadRepository;
import com.example.communicationboard.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReplyService replyService;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> getPost(String id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByThread(String threadId, int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").ascending());
        return postRepository.findByThreadId(threadId, pageRequest).getContent();
    }

    @Transactional
    public void deletePost(String id, String userId, String privilege) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
        if (!privilege.equals("manager") && !post.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this post.");
        }
        // Delegate deletion of children replies to ReplyService
        post.getChildren().forEach(reply -> replyService.deleteReply(reply.getId(), userId, "manager"));

        Thread thread = threadRepository.findById(post.getThreadId())
                .orElseThrow(() -> new RuntimeException("Thread not found with id: " + post.getThreadId()));
        thread.removeChildById(id);

        threadRepository.save(thread);

        postRepository.delete(post);

    }

    public Reply addReplyToPost(String postId, Reply reply) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
        reply = replyService.createReply(reply);
        post.addChild(reply);
        postRepository.save(post);
        return reply;
    }

    public List<PostComponent> findRepliesByPostId(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return post.getChildren();
    }
}
