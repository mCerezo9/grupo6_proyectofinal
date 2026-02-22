package com.example.library.service;

import com.example.library.domain.Prestamo;
import com.example.library.domain.Libro;
import com.example.library.domain.Miembro;
import com.example.library.domain.EstadoPrestamo;
import com.example.library.dto.PrestamoCreateDTO;
import com.example.library.dto.PrestamoUpdateDTO;
import com.example.library.repository.LibroRepository;
import com.example.library.repository.MiembroRepository;
import com.example.library.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final MiembroRepository miembroRepository;
    private final AuditLogService auditLogService;

    public PrestamoService(PrestamoRepository prestamoRepository,
            LibroRepository libroRepository,
            MiembroRepository miembroRepository,
            AuditLogService auditLogService) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.miembroRepository = miembroRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Prestamo crearPrestamo(PrestamoCreateDTO dto, String usuario) {
        Libro libro = libroRepository.findById(dto.getLibroId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Libro no encontrado"));
        Miembro miembro = miembroRepository.findById(dto.getMiembroId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Miembro no encontrado"));

        if (libro.getStock() <= 0) {
            throw new ResponseStatusException(CONFLICT, "Sin stock disponible");
        }

        Prestamo p = new Prestamo();
        p.setLibro(libro);
        p.setMiembro(miembro);
        p.setFechaInicio(LocalDateTime.now());
        p.setFechaVencimiento(dto.getFechaVencimiento());
        p.setEstado(EstadoPrestamo.PRESTADO);

        libro.setStock(libro.getStock() - 1);
        libroRepository.save(libro);

        Prestamo saved = prestamoRepository.save(p);

        Map<String, Object> payload = new HashMap<>();
        payload.put("libroId", libro.getId());
        payload.put("miembroId", miembro.getId());
        payload.put("fechaVencimiento", dto.getFechaVencimiento().toString());
        auditLogService.log("CREATE_PRESTAMO", usuario, "Prestamo", saved.getId(), payload);

        return saved;
    }

    @Transactional(readOnly = true)
    public Prestamo obtenerPrestamo(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prestamo no encontrado"));
    }

    @Transactional
    public Prestamo actualizarPrestamo(Long id, PrestamoUpdateDTO dto, String usuario) {
        Prestamo prestamo = obtenerPrestamo(id);
        Libro libro = prestamo.getLibro();

        if (dto.getFechaVencimiento() != null) {
            prestamo.setFechaVencimiento(dto.getFechaVencimiento());
        }

        if (dto.getFechaDevolucion() != null) {
            prestamo.setFechaDevolucion(dto.getFechaDevolucion());
        }

        if (dto.getEstado() != null) {
            EstadoPrestamo previo = prestamo.getEstado();
            EstadoPrestamo nuevo = dto.getEstado();
            if (previo != nuevo) {
                if (nuevo == EstadoPrestamo.DEVUELTO && previo == EstadoPrestamo.PRESTADO) {
                    if (prestamo.getFechaDevolucion() == null) {
                        prestamo.setFechaDevolucion(LocalDateTime.now());
                    }
                    libro.setStock(libro.getStock() + 1);
                    libroRepository.save(libro);
                } else if (nuevo == EstadoPrestamo.PRESTADO && previo == EstadoPrestamo.DEVUELTO) {
                    if (libro.getStock() <= 0) {
                        throw new ResponseStatusException(CONFLICT, "Sin stock disponible");
                    }
                    prestamo.setFechaDevolucion(null);
                    libro.setStock(libro.getStock() - 1);
                    libroRepository.save(libro);
                }
                prestamo.setEstado(nuevo);
            }
        }

        Prestamo saved = prestamoRepository.save(prestamo);
        Map<String, Object> payload = new HashMap<>();
        payload.put("estado", saved.getEstado().name());
        payload.put("fechaVencimiento", saved.getFechaVencimiento());
        payload.put("fechaDevolucion", saved.getFechaDevolucion());
        auditLogService.log("UPDATE_PRESTAMO", usuario, "Prestamo", saved.getId(), payload);

        return saved;
    }

    @Transactional
    public void eliminarPrestamo(Long id, String usuario) {
        Prestamo prestamo = obtenerPrestamo(id);
        Libro libro = prestamo.getLibro();
        if (prestamo.getEstado() == EstadoPrestamo.PRESTADO && prestamo.getFechaDevolucion() == null) {
            libro.setStock(libro.getStock() + 1);
            libroRepository.save(libro);
        }
        prestamoRepository.delete(prestamo);

        Map<String, Object> payload = new HashMap<>();
        payload.put("libroId", libro.getId());
        payload.put("miembroId", prestamo.getMiembro().getId());
        auditLogService.log("DELETE_PRESTAMO", usuario, "Prestamo", id, payload);
    }
}
