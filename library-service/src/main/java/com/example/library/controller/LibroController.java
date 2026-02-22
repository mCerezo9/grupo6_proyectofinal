package com.example.library.controller;

import com.example.library.domain.Libro;
import com.example.library.repository.LibroRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private final LibroRepository libroRepository;

    public LibroController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @GetMapping
    public Page<Libro> listar(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String categoria) {
        String[] s = sort.split(",");
        Sort.Order order = new Sort.Order(Sort.Direction.fromString(s.length > 1 ? s[1] : "asc"), s[0]);
        Pageable p = PageRequest.of(page, size, Sort.by(order));
        if (categoria != null && !categoria.isBlank()) {
            return libroRepository.findByCategoria(categoria, p);
        }
        return libroRepository.findAll(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> getOne(@PathVariable Long id) {
        Optional<Libro> o = libroRepository.findById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Libro> create(@Valid @RequestBody Libro libro) {
        Libro saved = libroRepository.save(libro);
        return ResponseEntity.created(URI.create("/api/libros/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> update(@PathVariable Long id, @Valid @RequestBody Libro libro) {
        return libroRepository.findById(id).map(existing -> {
            existing.setTitulo(libro.getTitulo());
            existing.setIsbn(libro.getIsbn());
            existing.setCategoria(libro.getCategoria());
            existing.setPublicadoEn(libro.getPublicadoEn());
            existing.setStock(libro.getStock());
            libroRepository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!libroRepository.existsById(id))
            return ResponseEntity.notFound().build();
        libroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
