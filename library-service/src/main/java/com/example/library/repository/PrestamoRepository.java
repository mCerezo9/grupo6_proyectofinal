package com.example.library.repository;

import com.example.library.domain.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    @Query("SELECT p FROM Prestamo p WHERE p.fechaDevolucion IS NULL AND p.fechaVencimiento < :now")
    List<Prestamo> findVencidos(@Param("now") LocalDateTime now);

    @Query("SELECT p.libro.id, COUNT(p) FROM Prestamo p GROUP BY p.libro.id ORDER BY COUNT(p) DESC")
    List<Object[]> topLibrosPrestados();
}
