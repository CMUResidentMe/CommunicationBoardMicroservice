package com.example.communicationboard.service;

import com.example.communicationboard.model.Post;
import com.example.communicationboard.model.Reply;
import com.example.communicationboard.repository.PostRepository;
import com.example.communicationboard.repository.ReplyRepository;

// import org.hibernate.validator.internal.util.logging.Log_.logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReplyService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReplyRepository replyRepository;

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
    public void deleteReply(String id, String userId, String privilege) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found with id: " + id));
        if (!"manager".equals(privilege) && !userId.equals(reply.getUserId())) {
            throw new SecurityException("Unauthorized to delete this reply.");
        }

        Post post = postRepository.findById(reply.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + reply.getPostId()));
        post.removeChildById(id);

        postRepository.save(post);

        replyRepository.delete(reply);
    }
}
