package com.rkovaliov.bu.repositories;

import com.rkovaliov.bu.entities.User;
import com.rkovaliov.bu.resources.Sex;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDAO extends MongoRepository<User, Long> {

    List<User> findTop5BySexOrderByLikeCountDesc(Sex sexOrdinal);
//    @Query("{user: ")
//    List<User> findTopBySexOrderByLikeCountAsc(int top, int sexOrdinal);
}
