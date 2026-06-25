package com.biblioteca.svc_miembros.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.biblioteca.svc_miembros.dto.MiembroRequest;
import com.biblioteca.svc_miembros.model.Miembro;
import com.biblioteca.svc_miembros.repository.MiembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MiembroService {

    private static final long TTL_SEGUNDOS = 120; // TTL mayor para miembros
    private static final AtomicInteger contador = new AtomicInteger(1);

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> crearMiembro(MiembroRequest request) {
        String id = "M" + String.format("%03d", contador.getAndIncrement());

        Miembro miembro = new Miembro();
        miembro.setId(id);
        miembro.setNombre(request.getNombre());
        miembro.setEmail(request.getEmail());
        miembro.setTipoMiembro(request.getTipoMiembro());
        miembro.setFechaRegistro(new Date().toString());
        miembro.setPrestamosActivos(0);

        miembroRepository.save(miembro);

        return Map.of(
                "mensaje", "Miembro registrado exitosamente",
                "id", id
        );
    }

    public Map<String, Object> getMiembro(String id) {
        String cacheKey = "miembro:" + id;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            try {
                Map<String, Object> miembroCache = objectMapper.readValue(
                        cached, new TypeReference<Map<String, Object>>() {}
                );
                return Map.of(
                        "fuente", "CACHE Redis (~2ms)",
                        "datos", miembroCache
                );
            } catch (Exception e) {
                System.out.println("Error leyendo caché: " + e.getMessage());
                stringRedisTemplate.delete(cacheKey);
            }
        }

        Optional<Miembro> miembroOpt = miembroRepository.findById(id);

        if (miembroOpt.isEmpty()) {
            return Map.of("error", "Miembro no encontrado con ID: " + id);
        }

        Miembro miembro = miembroOpt.get();

        try {
            String jsonMiembro = objectMapper.writeValueAsString(miembro);
            stringRedisTemplate.opsForValue().set(cacheKey, jsonMiembro, TTL_SEGUNDOS, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Error guardando en caché: " + e.getMessage());
        }

        return Map.of(
                "fuente", "BASE DE DATOS (~80ms)",
                "datos", miembro
        );
    }
}
