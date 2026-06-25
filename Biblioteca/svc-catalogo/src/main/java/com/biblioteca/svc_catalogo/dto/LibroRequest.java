package com.biblioteca.svc_catalogo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data

public class LibroRequest {
    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @NotBlank(message = "El género es obligatorio")
    private String genero;

    @NotNull(message = "El año de publicación es obligatorio")
    private Integer anioPublicacion;

    @NotNull(message = "La disponibilidad es obligatoria")
    private Boolean disponible;

    @NotNull(message = "Las copias totales son obligatorias")
    @Min(value = 1, message = "Debe haber al menos 1 copia")
    private Integer copiasTotales;

    @NotNull(message = "Las copias disponibles son obligatorias")
    @Min(value = 0, message = "Las copias disponibles no pueden ser negativas")
    private Integer copiasDisponibles;
}
