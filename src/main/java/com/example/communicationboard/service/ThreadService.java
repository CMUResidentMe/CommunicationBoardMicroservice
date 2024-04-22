package com.example.communicationboard.service;

import com.example.communicationboard.model.PostComponent;
import com.example.communicationboard.model.Thread;
import com.example.communicationboard.model.Post;
import com.example.communicationboard.repository.ThreadRepository;
import com.example.communicationboard.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.communicationboard.kafka.MsgProducer;
import com.example.communicationboard.dto.RmNotification;
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
    @Autowired
    private MsgProducer msgProducer;

    public Thread createThread(Thread thread) {
        return threadRepository.save(thread);
    }

    public Optional<Thread> getThread(String id) {
        return threadRepository.findById(id);
    }

    public List<Thread> getAllThreads(int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<Thread> page = threadRepository.findAll(pageRequest);
        return page.getContent();
    }

    @Transactional
    public void deleteThread(String id, String userId, String privilege) {
        Thread thread = threadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found with id: " + id));

        if (!privilege.equals("manager") && !thread.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this thread.");
        }

        System.out.println("Deleting thread: " + thread.getTitle());
        System.out.println("thread children size: " + thread.getChildren().size());
        // Delegate deletion of children posts to PostService
        thread.getChildren()
                .forEach(post -> postService.deletePost(post.getId(), userId, "manager", true));
        threadRepository.delete(thread);

        // If the thread owner is not the one deleting the thread, send a notification
        // to the owner
        if (!thread.getUserId().equals(userId)) {
            RmNotification notification = new RmNotification("threadDeleted");
            notification.setOwner(thread.getUserId());
            String message = String.format("Your thread '%s' has been deleted by an admin.", thread.getTitle());
            notification.setMessage(message);
            notification.setSourceID(thread.getId());
            msgProducer.sendThreadDeletedNotification(notification);
            System.out.println("Thread deleted notification sent to " + thread.getUserId());
            System.out.println("Message: " + message);
        }
    }

    public Post addPostToThread(String threadId, Post post) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found with id: " + threadId));
        post = postRepository.save(post);
        thread.addChild(post);
        threadRepository.save(thread);

        // Notify the thread owner if the post creator is not the thread owner
        if (!post.getUserId().equals(thread.getUserId())) {
            RmNotification notification = new RmNotification("postCreated");
            notification.setOwner(thread.getUserId());
            String message = String.format("Someone posted '%s' under your thread '%s'.", post.getContent(),
                    thread.getTitle());
            notification.setMessage(message);
            notification.setSourceID(post.getId());
            msgProducer.sendPostCreatedNotification(notification);
            System.out.println("Post created notification sent to " + thread.getUserId());
            System.out.println("Message: " + message);
        }

        return post;
    }

    public List<PostComponent> findPostsByThreadId(String threadId) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return thread.getChildren();
    }

}
