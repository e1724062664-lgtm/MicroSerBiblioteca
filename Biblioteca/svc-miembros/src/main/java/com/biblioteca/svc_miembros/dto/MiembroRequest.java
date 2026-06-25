package com.biblioteca.svc_miembros.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MiembroRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "El tipo de miembro es obligatorio")
    @Pattern(regexp = "ESTUDIANTE|DOCENTE|EXTERNO",
            message = "El tipo debe ser ESTUDIANTE, DOCENTE o EXTERNO")
    private String tipoMiembro;
}
