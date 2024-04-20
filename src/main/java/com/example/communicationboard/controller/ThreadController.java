package com.example.communicationboard.controller;

import com.example.communicationboard.model.Thread;
import com.example.communicationboard.service.ThreadService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
public class ThreadController {

    @Autowired
    private ThreadService threadService;

    @QueryMapping
    public List<Thread> threads(@Argument int pageNum, @Argument int pageSize) {
        return threadService.getAllThreads(pageNum, pageSize);
    }

    @QueryMapping
    public Thread thread(@Argument String id) {
        return threadService.getThread(id)
                .orElseThrow(() -> new RuntimeException("Thread not found with id: " + id));
    }

    @MutationMapping
    public Thread createThread(@Argument String title, @Argument String content,
            @Argument String userId, @Argument String userName) {
        return threadService.createThread(new Thread(title, content, userId, userName));
    }

    @MutationMapping
    public Boolean deleteThread(@Argument String id, @Argument String userId, @Argument String privilege) {
        threadService.deleteThread(id, userId, privilege);
        return true;
    }
}
