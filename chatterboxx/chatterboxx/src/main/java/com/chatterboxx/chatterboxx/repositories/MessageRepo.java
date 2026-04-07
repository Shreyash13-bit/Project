package com.chatterboxx.chatterboxx.repositories;

import com.chatterboxx.chatterboxx.entities.message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepo extends MongoRepository<message, String> {
    List<message> findByRoomIdOrderByTimestampAsc(String roomId);
}