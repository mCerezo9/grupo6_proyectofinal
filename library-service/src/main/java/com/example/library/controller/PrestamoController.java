package com.example.library.controller;

import com.example.library.domain.Prestamo;
import com.example.library.dto.PrestamoCreateDTO;
import com.example.library.dto.PrestamoUpdateDTO;
import com.example.library.dto.LibroPrestamosCountDTO;
import com.example.library.repository.PrestamoRepository;
import com.example.library.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {
    private final PrestamoService prestamoService;
    private final PrestamoRepository prestamoRepository;

    public PrestamoController(PrestamoService prestamoService, PrestamoRepository prestamoRepository) {
        this.prestamoService = prestamoService;
        this.prestamoRepository = prestamoRepository;
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PrestamoCreateDTO dto,
            @RequestHeader(value = "X-USER", defaultValue = "system") String user) {
        Prestamo p = prestamoService.crearPrestamo(dto, user);
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping
    public List<Prestamo> listar() {
        return prestamoRepository.findAll();
    }

    @GetMapping("/vencidos")
    public List<Prestamo> vencidos() {
        return prestamoRepository.findVencidos(java.time.LocalDateTime.now());
    }

    @GetMapping("/top-libros")
    public List<LibroPrestamosCountDTO> topLibros(@RequestParam(defaultValue = "5") int limit) {
        return prestamoRepository.topLibrosPrestados().stream()
                .limit(limit)
                .map(row -> new LibroPrestamosCountDTO((Long) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Prestamo> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPrestamo(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Prestamo> update(@PathVariable Long id,
            @Valid @RequestBody PrestamoUpdateDTO dto,
            @RequestHeader(value = "X-USER", defaultValue = "system") String user) {
        Prestamo updated = prestamoService.actualizarPrestamo(id, dto, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
            @RequestHeader(value = "X-USER", defaultValue = "system") String user) {
        prestamoService.eliminarPrestamo(id, user);
        return ResponseEntity.noContent().build();
    }
}
