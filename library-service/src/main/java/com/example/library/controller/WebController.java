package com.example.library.controller;

import com.example.library.domain.Libro;
import com.example.library.domain.Miembro;
import com.example.library.domain.Prestamo;
import com.example.library.domain.EstadoPrestamo;
import com.example.library.dto.PrestamoCreateDTO;
import com.example.library.dto.PrestamoUpdateDTO;
import com.example.library.dto.BookCommentCreateDTO;
import com.example.library.mongo.BookComment;
import com.example.library.repository.LibroRepository;
import com.example.library.repository.MiembroRepository;
import com.example.library.repository.PrestamoRepository;
import com.example.library.service.BookCommentService;
import com.example.library.service.PrestamoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class WebController {
    private final LibroRepository libroRepository;
    private final MiembroRepository miembroRepository;
    private final PrestamoRepository prestamoRepository;
    private final PrestamoService prestamoService;
    private final BookCommentService bookCommentService;

    public WebController(LibroRepository libroRepository,
            MiembroRepository miembroRepository,
            PrestamoRepository prestamoRepository,
            PrestamoService prestamoService,
            BookCommentService bookCommentService) {
        this.libroRepository = libroRepository;
        this.miembroRepository = miembroRepository;
        this.prestamoRepository = prestamoRepository;
        this.prestamoService = prestamoService;
        this.bookCommentService = bookCommentService;
    }

    @GetMapping({"/", "/ui"})
    public String home() {
        return "redirect:/ui/libros";
    }

    @GetMapping("/ui/libros")
    public String libros(@RequestParam(required = false) Long libroId,
            @RequestParam(required = false, defaultValue = "false") boolean conComentarios,
            Model model) {
        List<Libro> libros = libroRepository.findAll();
        if (conComentarios) {
            Set<Long> idsConComentarios = bookCommentService.libroIdsConComentarios().stream()
                    .collect(Collectors.toSet());
            libros = libros.stream()
                    .filter(l -> idsConComentarios.contains(l.getId()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("libros", libros);
        model.addAttribute("selectedLibroId", libroId);
        model.addAttribute("conComentarios", conComentarios);
        if (libroId != null) {
            List<BookComment> comentarios = bookCommentService.listar(libroId, null, null, null);
            model.addAttribute("comentarios", comentarios);
        }
        return "libros";
    }

    @PostMapping("/ui/libros/comentarios")
    public String crearComentario(@RequestParam Long libroId,
            @RequestParam String user,
            @RequestParam String texto,
            @RequestParam(required = false, defaultValue = "false") boolean conComentarios) {
        BookCommentCreateDTO dto = new BookCommentCreateDTO();
        dto.setLibroId(libroId);
        dto.setUser(user);
        dto.setTexto(texto);
        bookCommentService.crear(dto);
        String suffix = conComentarios ? "&conComentarios=true" : "";
        return "redirect:/ui/libros?libroId=" + libroId + suffix;
    }

    @GetMapping("/ui/libros/nuevo")
    public String nuevoLibro(Model model) {
        model.addAttribute("libro", new Libro());
        return "libro_form";
    }

    @PostMapping("/ui/libros")
    public String crearLibro(@ModelAttribute Libro libro) {
        libroRepository.save(libro);
        return "redirect:/ui/libros";
    }

    @GetMapping("/ui/libros/{id}/editar")
    public String editarLibro(@PathVariable Long id, Model model) {
        Libro libro = libroRepository.findById(id).orElseThrow();
        model.addAttribute("libro", libro);
        return "libro_form";
    }

    @PostMapping("/ui/libros/{id}")
    public String actualizarLibro(@PathVariable Long id, @ModelAttribute Libro libro) {
        Libro existing = libroRepository.findById(id).orElseThrow();
        existing.setTitulo(libro.getTitulo());
        existing.setIsbn(libro.getIsbn());
        existing.setCategoria(libro.getCategoria());
        existing.setPublicadoEn(libro.getPublicadoEn());
        existing.setStock(libro.getStock());
        libroRepository.save(existing);
        return "redirect:/ui/libros";
    }

    @PostMapping("/ui/libros/{id}/eliminar")
    public String eliminarLibro(@PathVariable Long id) {
        libroRepository.deleteById(id);
        return "redirect:/ui/libros";
    }

    @GetMapping("/ui/miembros")
    public String miembros(Model model) {
        model.addAttribute("miembros", miembroRepository.findAll());
        return "miembros";
    }

    @GetMapping("/ui/miembros/nuevo")
    public String nuevoMiembro(Model model) {
        model.addAttribute("miembro", new Miembro());
        return "miembro_form";
    }

    @PostMapping("/ui/miembros")
    public String crearMiembro(@ModelAttribute Miembro miembro) {
        miembroRepository.save(miembro);
        return "redirect:/ui/miembros";
    }

    @GetMapping("/ui/miembros/{id}/editar")
    public String editarMiembro(@PathVariable Long id, Model model) {
        Miembro miembro = miembroRepository.findById(id).orElseThrow();
        model.addAttribute("miembro", miembro);
        return "miembro_form";
    }

    @PostMapping("/ui/miembros/{id}")
    public String actualizarMiembro(@PathVariable Long id, @ModelAttribute Miembro miembro) {
        Miembro existing = miembroRepository.findById(id).orElseThrow();
        existing.setNombre(miembro.getNombre());
        existing.setEmail(miembro.getEmail());
        existing.setTelefono(miembro.getTelefono());
        existing.setFechaAlta(miembro.getFechaAlta());
        miembroRepository.save(existing);
        return "redirect:/ui/miembros";
    }

    @PostMapping("/ui/miembros/{id}/eliminar")
    public String eliminarMiembro(@PathVariable Long id) {
        miembroRepository.deleteById(id);
        return "redirect:/ui/miembros";
    }

    @GetMapping("/ui/prestamos")
    public String prestamos(Model model) {
        model.addAttribute("prestamos", prestamoRepository.findAll());
        return "prestamos";
    }

    @GetMapping("/ui/prestamos/nuevo")
    public String nuevoPrestamo(Model model) {
        model.addAttribute("libros", libroRepository.findAll());
        model.addAttribute("miembros", miembroRepository.findAll());
        return "prestamo_form";
    }

    @PostMapping("/ui/prestamos")
    public String crearPrestamo(@RequestParam Long libroId,
            @RequestParam Long miembroId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaVencimiento) {
        PrestamoCreateDTO dto = new PrestamoCreateDTO();
        dto.setLibroId(libroId);
        dto.setMiembroId(miembroId);
        dto.setFechaVencimiento(fechaVencimiento);
        prestamoService.crearPrestamo(dto, "web");
        return "redirect:/ui/prestamos";
    }

    @GetMapping("/ui/prestamos/{id}/editar")
    public String editarPrestamo(@PathVariable Long id, Model model) {
        Prestamo prestamo = prestamoService.obtenerPrestamo(id);
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("estados", EstadoPrestamo.values());
        return "prestamo_edit";
    }

    @PostMapping("/ui/prestamos/{id}")
    public String actualizarPrestamo(@PathVariable Long id,
            @RequestParam(required = false) EstadoPrestamo estado,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaVencimiento,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaDevolucion) {
        PrestamoUpdateDTO dto = new PrestamoUpdateDTO();
        dto.setEstado(estado);
        dto.setFechaVencimiento(fechaVencimiento);
        dto.setFechaDevolucion(fechaDevolucion);
        prestamoService.actualizarPrestamo(id, dto, "web");
        return "redirect:/ui/prestamos";
    }

    @PostMapping("/ui/prestamos/{id}/eliminar")
    public String eliminarPrestamo(@PathVariable Long id) {
        prestamoService.eliminarPrestamo(id, "web");
        return "redirect:/ui/prestamos";
    }
}
