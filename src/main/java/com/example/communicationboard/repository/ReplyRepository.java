package com.example.communicationboard.repository;

import com.example.communicationboard.model.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends MongoRepository<Reply, String> {

}
