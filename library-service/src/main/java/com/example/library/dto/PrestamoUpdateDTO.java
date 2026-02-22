package com.example.library.dto;

import com.example.library.domain.EstadoPrestamo;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public class PrestamoUpdateDTO {
    private EstadoPrestamo estado;

    private LocalDateTime fechaVencimiento;

    @PastOrPresent(message = "fechaDevolucion no puede ser futura")
    private LocalDateTime fechaDevolucion;

    public PrestamoUpdateDTO() {
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
}
