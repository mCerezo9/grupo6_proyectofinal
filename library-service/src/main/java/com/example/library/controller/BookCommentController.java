package com.example.library.controller;

import com.example.library.dto.BookCommentCreateDTO;
import com.example.library.dto.LibroComentariosCountDTO;
import com.example.library.mongo.BookComment;
import com.example.library.service.BookCommentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class BookCommentController {
    private final BookCommentService commentService;

    public BookCommentController(BookCommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public BookComment crear(@Valid @RequestBody BookCommentCreateDTO dto) {
        return commentService.crear(dto);
    }

    @GetMapping
    public List<BookComment> listar(
            @RequestParam(required = false) Long libroId,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return commentService.listar(libroId, user, from, to);
    }

    @GetMapping("/top-libros")
    public List<LibroComentariosCountDTO> topLibros(@RequestParam(defaultValue = "5") int limit) {
        return commentService.topLibros(limit);
    }
}
