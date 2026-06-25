package com.biblioteca.svc_miembros.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "miembros")
public class Miembro {

    @Id
    private String id;

    private String nombre;
    private String email;
    private String tipoMiembro;
    private String fechaRegistro;
    private Integer prestamosActivos;
}
