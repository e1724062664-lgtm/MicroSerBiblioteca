package com.biblioteca.svc_catalogo.controller;

import com.biblioteca.svc_catalogo.dto.LibroRequest;
import com.biblioteca.svc_catalogo.service.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Catalogocontroller {
    @Autowired
    private CatalogoService catalogoService;

    @PostMapping("/libros")
    public Map<String, Object> crearLibro(@Valid @RequestBody LibroRequest request) {
        return catalogoService.crearLibro(request);
    }

    @GetMapping("/libros/{isbn}")
    public Map<String, Object> getLibro(@PathVariable String isbn) {
        return catalogoService.getLibro(isbn);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("servicio", "svc-catalogo", "estado", "ok");
    }
}
