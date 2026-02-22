package com.example.library.dto;

public class LibroPrestamosCountDTO {
    private Long libroId;
    private Long total;

    public LibroPrestamosCountDTO() {
    }

    public LibroPrestamosCountDTO(Long libroId, Long total) {
        this.libroId = libroId;
        this.total = total;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
