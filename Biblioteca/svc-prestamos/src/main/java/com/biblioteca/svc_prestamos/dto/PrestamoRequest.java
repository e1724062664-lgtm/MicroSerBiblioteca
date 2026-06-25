package com.biblioteca.svc_prestamos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class PrestamoRequest {

    @NotBlank(message = "El ISBN del libro es obligatorio")
    private String isbn;

    @NotBlank(message = "El ID del miembro es obligatorio")
    private String miembroId;
}
