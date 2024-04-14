package com.example.communicationboard.model;

import java.util.List;

public interface PostComponent {
    String getId();

    void addChild(PostComponent postComponent);

    void removeChildById(String childId);

    PostComponent getChild(int i);

    List<PostComponent> getChildren();
}
