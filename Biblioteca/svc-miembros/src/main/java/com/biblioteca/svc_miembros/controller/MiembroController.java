package com.biblioteca.svc_miembros.controller;

import com.biblioteca.svc_miembros.dto.MiembroRequest;
import com.biblioteca.svc_miembros.service.MiembroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MiembroController {

    @Autowired
    private MiembroService miembroService;

    @PostMapping("/miembros")
    public Map<String, Object> crearMiembro(@Valid @RequestBody MiembroRequest request) {
        return miembroService.crearMiembro(request);
    }

    @GetMapping("/miembros/{id}")
    public Map<String, Object> getMiembro(@PathVariable String id) {
        return miembroService.getMiembro(id);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("servicio", "svc-miembros", "estado", "ok");
    }
}
