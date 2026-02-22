package com.example.library.controller;

import com.example.library.domain.Miembro;
import com.example.library.repository.MiembroRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/miembros")
public class MiembroController {
    private final MiembroRepository miembroRepository;

    public MiembroController(MiembroRepository miembroRepository) {
        this.miembroRepository = miembroRepository;
    }

    @GetMapping
    public Iterable<Miembro> listar() {
        return miembroRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Miembro> getOne(@PathVariable Long id) {
        Optional<Miembro> o = miembroRepository.findById(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Miembro> create(@Valid @RequestBody Miembro miembro) {
        Miembro saved = miembroRepository.save(miembro);
        return ResponseEntity.created(URI.create("/api/miembros/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Miembro> update(@PathVariable Long id, @Valid @RequestBody Miembro miembro) {
        return miembroRepository.findById(id).map(existing -> {
            existing.setNombre(miembro.getNombre());
            existing.setEmail(miembro.getEmail());
            existing.setTelefono(miembro.getTelefono());
            existing.setFechaAlta(miembro.getFechaAlta());
            miembroRepository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!miembroRepository.existsById(id))
            return ResponseEntity.notFound().build();
        miembroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
