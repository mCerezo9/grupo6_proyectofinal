package com.example.library.repository;

import com.example.library.domain.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Page<Libro> findByCategoria(String categoria, Pageable pageable);
}
