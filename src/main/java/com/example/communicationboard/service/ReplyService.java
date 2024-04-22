package com.example.communicationboard.service;

import com.example.communicationboard.model.Post;
import com.example.communicationboard.model.Reply;
import com.example.communicationboard.repository.PostRepository;
import com.example.communicationboard.repository.ReplyRepository;
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
public class ReplyService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private MsgProducer msgProducer;

    // Unused for now
    public Reply createReply(Reply reply) {
        return replyRepository.save(reply);
    }

    public Optional<Reply> getReply(String id) {
        return replyRepository.findById(id);
    }

    public List<Reply> getRepliesByPost(String postId, int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").ascending());
        return replyRepository.findByPostId(postId, pageRequest).getContent();
    }

    @Transactional
    public void deleteReply(String id, String userId, String privilege, boolean isCascading) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found with id: " + id));
        if (!privilege.equals("manager") && !userId.equals(reply.getUserId())) {
            throw new SecurityException("Unauthorized to delete this reply.");
        }

        Post post = postRepository.findById(reply.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + reply.getPostId()));
        post.removeChildById(id);

        postRepository.save(post);

        replyRepository.delete(reply);

        if (!reply.getUserId().equals(userId)) {
            String message = isCascading
                    ? String.format("Your reply '%s' to the post '%s' has been deleted because the post was removed.",
                            reply.getContent(), post.getContent())
                    : String.format("Your reply '%s' to the post '%s' has been deleted by an admin.",
                            reply.getContent(), post.getContent());
            RmNotification notification = new RmNotification("replyDeleted");
            notification.setOwner(reply.getUserId());
            notification.setMessage(message);
            notification.setSourceID(reply.getId());
            msgProducer.sendReplyDeletedNotification(notification);
            System.out.println("Reply deleted notification sent to " + reply.getUserId());
            System.out.println("Message: " + message);
        }
    }
}
