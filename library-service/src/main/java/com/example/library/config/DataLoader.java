package com.example.library.config;

import com.example.library.domain.Libro;
import com.example.library.domain.Miembro;
import com.example.library.repository.LibroRepository;
import com.example.library.repository.MiembroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {
    private final LibroRepository libroRepository;
    private final MiembroRepository miembroRepository;

    public DataLoader(LibroRepository libroRepository, MiembroRepository miembroRepository) {
        this.libroRepository = libroRepository;
        this.miembroRepository = miembroRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (libroRepository.count() == 0) {
            Libro l1 = new Libro();
            l1.setTitulo("El Quijote");
            l1.setIsbn("978-1");
            l1.setPublicadoEn(LocalDate.of(1605, 1, 1));
            l1.setCategoria("Clásico");
            l1.setStock(5);
            Libro l2 = new Libro();
            l2.setTitulo("Java moderno");
            l2.setIsbn("978-2");
            l2.setPublicadoEn(LocalDate.of(2020, 5, 1));
            l2.setCategoria("Tecnología");
            l2.setStock(3);
            libroRepository.save(l1);
            libroRepository.save(l2);
        }
        if (miembroRepository.count() == 0) {
            Miembro m1 = new Miembro();
            m1.setNombre("Ana");
            m1.setEmail("ana@example.com");
            m1.setTelefono("600111222");
            m1.setFechaAlta(LocalDate.now());
            Miembro m2 = new Miembro();
            m2.setNombre("Luis");
            m2.setEmail("luis@example.com");
            m2.setTelefono("600333444");
            m2.setFechaAlta(LocalDate.now());
            miembroRepository.save(m1);
            miembroRepository.save(m2);
        }
    }
}
