package com.example.communicationboard.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Post implements PostComponent {

    @Id
    private String id;
    private String content; // The content of the post
    private String userId; // ID of the user who created the post
    private String userName; // Name of the user who created the post
    private String threadId; // ID of the thread this post belongs to

    @DBRef
    private List<PostComponent> replies; // List to hold the child replies

    public Post(String content, String userId, String userName, String threadId) {
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.threadId = threadId;
        this.replies = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @Override
    public void addChild(PostComponent postComponent) {
        if (postComponent instanceof Reply) {
            replies.add(postComponent);
        } else {
            throw new UnsupportedOperationException("Cannot add non-reply PostComponent to replies");
        }
    }

    @Override
    public void removeChildById(String childId) {
        replies.removeIf(reply -> reply.getId().equals(childId));
    }

    @Override
    public PostComponent getChild(int i) {
        return replies.get(i);
    }

    @Override
    public List<PostComponent> getChildren() {
        return replies;
    }
}
