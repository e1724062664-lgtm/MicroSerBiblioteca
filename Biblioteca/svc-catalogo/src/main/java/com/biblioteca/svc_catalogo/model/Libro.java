package com.biblioteca.svc_catalogo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "libros")

public class Libro {
    @Id
    private String isbn;

    private String titulo;
    private String autor;
    private String genero;
    private Integer anioPublicacion;
    private Boolean disponible;
    private Integer copiasTotales;
    private Integer copiasDisponibles;
}
