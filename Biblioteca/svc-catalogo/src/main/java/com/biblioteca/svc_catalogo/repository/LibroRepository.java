package com.biblioteca.svc_catalogo.repository;

import com.biblioteca.svc_catalogo.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, String>{
}
