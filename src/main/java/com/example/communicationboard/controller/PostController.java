package com.example.communicationboard.controller;

import com.example.communicationboard.model.Post;
import com.example.communicationboard.service.ThreadService;
import com.example.communicationboard.service.PostService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class PostController {

    @Autowired
    private ThreadService threadService;
    @Autowired
    private PostService postService;

    @MutationMapping
    public Post createPost(@Argument String threadId, @Argument String content,
            @Argument String userId, @Argument String userName) {
        return threadService.addPostToThread(threadId, new Post(content, userId, userName, threadId));
    }

    @MutationMapping
    public Boolean deletePost(@Argument String id, @Argument String userId, @Argument String privilege) {
        postService.deletePost(id, userId, privilege);
        return true;
    }
}
