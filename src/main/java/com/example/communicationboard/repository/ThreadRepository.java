package com.example.communicationboard.repository;

import com.example.communicationboard.model.Thread;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends MongoRepository<Thread, String> {

}
