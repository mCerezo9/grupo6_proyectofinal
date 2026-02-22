package com.example.library.service;

import com.example.library.dto.BookCommentCreateDTO;
import com.example.library.dto.LibroComentariosCountDTO;
import com.example.library.mongo.BookComment;
import com.example.library.mongo.BookCommentRepository;
import com.example.library.repository.LibroRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class BookCommentService {
    private final BookCommentRepository commentRepository;
    private final LibroRepository libroRepository;
    private final MongoTemplate mongoTemplate;

    public BookCommentService(BookCommentRepository commentRepository,
            LibroRepository libroRepository,
            MongoTemplate mongoTemplate) {
        this.commentRepository = commentRepository;
        this.libroRepository = libroRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public BookComment crear(BookCommentCreateDTO dto) {
        libroRepository.findById(dto.getLibroId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Libro no encontrado"));

        BookComment comment = new BookComment();
        comment.setLibroId(dto.getLibroId());
        comment.setUser(dto.getUser());
        comment.setTexto(dto.getTexto());
        comment.setCreatedAt(Instant.now());

        return commentRepository.save(comment);
    }

    public List<BookComment> listar(Long libroId, String user, Instant from, Instant to) {
        if (libroId != null) {
            return commentRepository.findByLibroId(libroId);
        }
        if (user != null && !user.isBlank()) {
            return commentRepository.findByUser(user);
        }
        if (from != null && to != null) {
            return commentRepository.findByCreatedAtBetween(from, to);
        }
        return commentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<LibroComentariosCountDTO> topLibros(int limit) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("libroId").count().as("total"),
                Aggregation.sort(Sort.Direction.DESC, "total"),
                Aggregation.limit(limit)
        );

        AggregationResults<BookCommentCount> results = mongoTemplate.aggregate(
                agg, "book_comments", BookCommentCount.class);

        return results.getMappedResults().stream()
                .map(r -> new LibroComentariosCountDTO(r.getLibroId(), r.getTotal()))
                .collect(Collectors.toList());
    }

            public List<Long> libroIdsConComentarios() {
            Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("libroId")
            );

            AggregationResults<BookCommentCount> results = mongoTemplate.aggregate(
                agg, "book_comments", BookCommentCount.class);

            return results.getMappedResults().stream()
                .map(BookCommentCount::getLibroId)
                .collect(Collectors.toList());
            }

    private static class BookCommentCount {
        private Long libroId;
        private Long total;

        public Long getLibroId() {
            return libroId;
        }

        public void setLibroId(Long libroId) {
            this.libroId = libroId;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }
    }
}
