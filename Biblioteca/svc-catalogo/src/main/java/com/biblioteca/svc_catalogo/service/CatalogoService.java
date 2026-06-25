package com.biblioteca.svc_catalogo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.biblioteca.svc_catalogo.dto.LibroRequest;
import com.biblioteca.svc_catalogo.model.Libro;
import com.biblioteca.svc_catalogo.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service

public class CatalogoService {

    private static final long TTL_SEGUNDOS = 60;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> crearLibro(LibroRequest request) {
        Libro libro = new Libro();
        libro.setIsbn(request.getIsbn());
        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setGenero(request.getGenero());
        libro.setAnioPublicacion(request.getAnioPublicacion());
        libro.setDisponible(request.getDisponible());
        libro.setCopiasTotales(request.getCopiasTotales());
        libro.setCopiasDisponibles(request.getCopiasDisponibles());

        libroRepository.save(libro);
        // Invalidar caché si existía
        stringRedisTemplate.delete("libro:" + libro.getIsbn());

        return Map.of(
                "mensaje", "Libro registrado exitosamente",
                "isbn", libro.getIsbn()
        );
    }

    public Map<String, Object> getLibro(String isbn) {
        String cacheKey = "libro:" + isbn;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            try {
                Map<String, Object> libroCache = objectMapper.readValue(
                        cached, new TypeReference<Map<String, Object>>() {}
                );
                return Map.of(
                        "fuente", "CACHE Redis (~2ms)",
                        "datos", libroCache
                );
            } catch (Exception e) {
                System.out.println("Error leyendo caché: " + e.getMessage());
                stringRedisTemplate.delete(cacheKey);
            }
        }

        Optional<Libro> libroOpt = libroRepository.findById(isbn);

        if (libroOpt.isEmpty()) {
            return Map.of("error", "Libro no encontrado con ISBN: " + isbn);
        }

        Libro libro = libroOpt.get();

        try {
            String jsonLibro = objectMapper.writeValueAsString(libro);
            stringRedisTemplate.opsForValue().set(cacheKey, jsonLibro, TTL_SEGUNDOS, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Error guardando en caché: " + e.getMessage());
        }

        return Map.of(
                "fuente", "BASE DE DATOS (~80ms)",
                "datos", libro
        );
    }
}
