package com.biblioteca.svc_prestamos.controller;

import com.biblioteca.svc_prestamos.dto.PrestamoRequest;
import com.biblioteca.svc_prestamos.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping("/prestamos")
    public Map<String, Object> crearPrestamo(@Valid @RequestBody PrestamoRequest request) {
        return prestamoService.crearPrestamo(request);
    }

    @GetMapping("/prestamos/{id}")
    public Map<String, Object> getPrestamo(@PathVariable String id) {
        return prestamoService.getPrestamo(id);
    }

    @PutMapping("/prestamos/{id}/devolver")
    public Map<String, Object> devolverPrestamo(@PathVariable String id) {
        return prestamoService.devolverPrestamo(id);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("servicio", "svc-prestamos", "estado", "ok");
    }
}
