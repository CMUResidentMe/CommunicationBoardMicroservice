package com.example.communicationboard.controller;

import com.example.communicationboard.model.Reply;
import com.example.communicationboard.service.PostService;
import com.example.communicationboard.service.ReplyService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class ReplyController {

    @Autowired
    private PostService postService;
    @Autowired
    private ReplyService replyService;

    // Query to get all replies in a post
    @QueryMapping
    public List<Reply> repliesByPost(@Argument String postId, @Argument int pageNum, @Argument int pageSize) {
        return replyService.getRepliesByPost(postId, pageNum, pageSize);
    }

    // Mutation to create a reply in a post
    @MutationMapping
    public Reply createReply(@Argument String postId, @Argument String content,
            @Argument String userId, @Argument String userName) {
        return postService.addReplyToPost(postId, new Reply(content, userId, userName, postId));
    }

    // Mutation to delete a reply
    @MutationMapping
    public Boolean deleteReply(@Argument String id, @Argument String userId, @Argument String privilege) {
        replyService.deleteReply(id, userId, privilege, false);
        return true;
    }
}
