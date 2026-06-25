package com.biblioteca.svc_miembros.repository;

import com.biblioteca.svc_miembros.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MiembroRepository extends JpaRepository<Miembro, String>{
}
