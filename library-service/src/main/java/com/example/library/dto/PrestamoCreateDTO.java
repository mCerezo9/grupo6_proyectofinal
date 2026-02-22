package com.example.library.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PrestamoCreateDTO {
    @NotNull(message = "libroId requerido")
    private Long libroId;
    @NotNull(message = "miembroId requerido")
    private Long miembroId;
    @NotNull(message = "fechaVencimiento requerida")
    @Future(message = "fechaVencimiento debe ser futura")
    private LocalDateTime fechaVencimiento;

    public PrestamoCreateDTO() {
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public Long getMiembroId() {
        return miembroId;
    }

    public void setMiembroId(Long miembroId) {
        this.miembroId = miembroId;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
