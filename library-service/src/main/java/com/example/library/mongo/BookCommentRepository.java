package com.example.library.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface BookCommentRepository extends MongoRepository<BookComment, String> {
    List<BookComment> findByLibroId(Long libroId);

    List<BookComment> findByUser(String user);

    List<BookComment> findByCreatedAtBetween(Instant from, Instant to);
}
