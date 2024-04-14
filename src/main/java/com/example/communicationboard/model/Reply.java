package com.example.communicationboard.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Reply implements PostComponent {

    @Id
    private String id;
    private String content;
    private String userId; // ID of the user who created the reply
    private String userName; // Name of the user who created the reply
    private String postId; // ID of the post this reply belongs to

    public Reply(String content, String userId, String userName, String postId) {
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // These methods are no-ops for the Reply because it is a leaf in the composite
    // structure
    @Override
    public void addChild(PostComponent postComponent) {
        // No operation, Replies don't have children
    }

    @Override
    public void removeChildById(String childId) {
        // No operation, Replies don't have children
    }

    @Override
    public PostComponent getChild(int i) {
        return null; // Replies don't have children
    }

    @Override
    public List<PostComponent> getChildren() {
        return null; // Replies don't have children
    }
}
