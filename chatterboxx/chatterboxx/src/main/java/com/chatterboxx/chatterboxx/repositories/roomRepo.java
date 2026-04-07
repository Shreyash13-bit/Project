package com.chatterboxx.chatterboxx.repositories;

import com.chatterboxx.chatterboxx.entities.room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface roomRepo extends MongoRepository<room, String> {

    room findByRoomId(String roomId);
}
