package com.example.communicationboard.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Document
public class Thread implements PostComponent {

    @Id
    private String id;
    private String title; // The title of the thread
    private String content; // The initial content of the thread
    private String userId; // ID of the user who created the thread
    private String userName; // Name of the user who created the thread
    private Date createdAt; // The date and time the thread was created

    @DBRef
    private List<PostComponent> posts; // List to hold the child posts

    public Thread(String title, String content, String userId, String userName) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.posts = new ArrayList<>();
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void addChild(PostComponent postComponent) {
        if (postComponent instanceof Post) {
            posts.add(postComponent);
        } else {
            throw new UnsupportedOperationException("Cannot add non-post PostComponent to replies");
        }
    }

    @Override
    public void removeChildById(String childId) {
        posts.removeIf(post -> post.getId().equals(childId));
    }

    @Override
    public PostComponent getChild(int i) {
        return posts.get(i);
    }

    public List<PostComponent> getChildren() {
        return posts;
    }
}
