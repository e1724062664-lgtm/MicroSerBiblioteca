package com.biblioteca.svc_prestamos.repository;

import com.biblioteca.svc_prestamos.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, String>{
}
