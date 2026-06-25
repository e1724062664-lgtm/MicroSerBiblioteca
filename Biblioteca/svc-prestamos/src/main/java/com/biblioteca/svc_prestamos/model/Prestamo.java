package com.biblioteca.svc_prestamos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    private String id;

    private String isbn;
    private String miembroId;
    private String fechaPrestamo;
    private String fechaDevolucionEstimada;
    private String fechaDevolucionReal;
    private String estado;
}
