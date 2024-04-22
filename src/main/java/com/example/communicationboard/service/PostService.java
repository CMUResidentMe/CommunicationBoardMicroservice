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
import com.example.communicationboard.kafka.MsgProducer;
import com.example.communicationboard.dto.RmNotification;
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
    @Autowired
    private MsgProducer msgProducer;

    // Unused for now
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
    public void deletePost(String id, String userId, String privilege, boolean isCascading) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
        if (!privilege.equals("manager") && !post.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this post.");
        }

        // Delegate deletion of children replies to ReplyService
        post.getChildren()
                .forEach(reply -> replyService.deleteReply(reply.getId(), userId, "manager", true));

        // If the post is directly deleted, remove it from the thread's children
        Thread thread = threadRepository.findById(post.getThreadId())
                .orElseThrow(() -> new RuntimeException("Thread not found with id: " + post.getThreadId()));
        thread.removeChildById(id);

        threadRepository.save(thread);

        postRepository.delete(post);

        // If the post owner is not the one deleting the post, send a notification to
        // the owner
        if (!post.getUserId().equals(userId)) {
            String message = isCascading
                    ? String.format(
                            "Your post '%s' under the thread '%s' has been deleted because the thread was removed.",
                            post.getContent(), thread.getTitle())
                    : String.format("Your post '%s' under the thread '%s' has been deleted by an admin.",
                            post.getContent(), thread.getTitle());
            RmNotification notification = new RmNotification("postDeleted");
            notification.setOwner(post.getUserId());
            notification.setMessage(message);
            notification.setSourceID(post.getId());
            msgProducer.sendPostDeletedNotification(notification);
            System.out.println("Post deleted notification sent to " + post.getUserId());
            System.out.println("Message: " + message);
        }
    }

    public Reply addReplyToPost(String postId, Reply reply) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
        reply = replyService.createReply(reply);
        post.addChild(reply);
        postRepository.save(post);

        // Notify the post owner if the reply creator is not the post owner
        if (!reply.getUserId().equals(post.getUserId())) {
            RmNotification notification = new RmNotification("replyCreated");
            notification.setOwner(post.getUserId());
            String message = String.format("Someone replied '%s' to your post '%s'.", reply.getContent(),
                    post.getContent());
            notification.setMessage(message);
            notification.setSourceID(reply.getId());
            msgProducer.sendReplyCreatedNotification(notification);
            System.out.println("Reply created notification sent to " + post.getUserId());
            System.out.println("Message: " + message);
        }

        return reply;
    }

    public List<PostComponent> findRepliesByPostId(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return post.getChildren();
    }
}
