package com.example.communicationboard.repository;

import com.example.communicationboard.model.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends MongoRepository<Reply, String> {
    Page<Reply> findByPostId(@Param("postId") String postId, Pageable pageable);
}
