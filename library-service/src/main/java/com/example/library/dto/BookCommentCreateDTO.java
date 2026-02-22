package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookCommentCreateDTO {
    @NotNull(message = "libroId requerido")
    private Long libroId;

    @NotBlank(message = "user requerido")
    private String user;

    @NotBlank(message = "texto requerido")
    private String texto;

    public BookCommentCreateDTO() {
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
