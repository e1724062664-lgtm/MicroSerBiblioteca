package com.biblioteca.svc_prestamos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.biblioteca.svc_prestamos.dto.PrestamoRequest;
import com.biblioteca.svc_prestamos.model.Prestamo;
import com.biblioteca.svc_prestamos.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PrestamoService {

    private static final long TTL_SEGUNDOS = 60;
    private static final String ESTADO_ACTIVO   = "ACTIVO";
    private static final String ESTADO_DEVUELTO = "DEVUELTO";

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> crearPrestamo(PrestamoRequest request) {
        String id = "PRE-" + System.currentTimeMillis();

        Date fechaPrestamo = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaPrestamo);
        cal.add(Calendar.DAY_OF_MONTH, 14); // 14 días de plazo
        Date fechaDevolucionEstimada = cal.getTime();

        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        prestamo.setIsbn(request.getIsbn());
        prestamo.setMiembroId(request.getMiembroId());
        prestamo.setFechaPrestamo(fechaPrestamo.toString());
        prestamo.setFechaDevolucionEstimada(fechaDevolucionEstimada.toString());
        prestamo.setFechaDevolucionReal(null);
        prestamo.setEstado(ESTADO_ACTIVO);

        prestamoRepository.save(prestamo);

        return Map.of(
                "mensaje", "Préstamo registrado exitosamente",
                "id", id,
                "fechaDevolucionEstimada", fechaDevolucionEstimada.toString()
        );
    }

    public Map<String, Object> getPrestamo(String id) {
        String cacheKey = "prestamo:" + id;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            try {
                Map<String, Object> prestamoCache = objectMapper.readValue(
                        cached, new TypeReference<Map<String, Object>>() {}
                );
                return Map.of(
                        "fuente", "CACHE Redis (~2ms)",
                        "datos", prestamoCache
                );
            } catch (Exception e) {
                System.out.println("Error leyendo caché: " + e.getMessage());
                stringRedisTemplate.delete(cacheKey);
            }
        }

        Optional<Prestamo> prestamoOpt = prestamoRepository.findById(id);

        if (prestamoOpt.isEmpty()) {
            return Map.of("error", "Préstamo no encontrado con ID: " + id);
        }

        Prestamo prestamo = prestamoOpt.get();

        try {
            String jsonPrestamo = objectMapper.writeValueAsString(prestamo);
            stringRedisTemplate.opsForValue().set(cacheKey, jsonPrestamo, TTL_SEGUNDOS, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Error guardando en caché: " + e.getMessage());
        }

        return Map.of(
                "fuente", "BASE DE DATOS (~80ms)",
                "datos", prestamo
        );
    }

    public Map<String, Object> devolverPrestamo(String id) {
        Optional<Prestamo> prestamoOpt = prestamoRepository.findById(id);

        if (prestamoOpt.isEmpty()) {
            return Map.of("error", "Préstamo no encontrado con ID: " + id);
        }

        Prestamo prestamo = prestamoOpt.get();

        if (ESTADO_DEVUELTO.equals(prestamo.getEstado())) {
            return Map.of("error", "El préstamo ya fue devuelto anteriormente");
        }

        prestamo.setFechaDevolucionReal(new Date().toString());
        prestamo.setEstado(ESTADO_DEVUELTO);
        prestamoRepository.save(prestamo);

        // Invalidar caché para que refleje el nuevo estado
        stringRedisTemplate.delete("prestamo:" + id);

        return Map.of(
                "mensaje", "Devolución registrada exitosamente",
                "id", id,
                "estado", ESTADO_DEVUELTO,
                "fechaDevolucionReal", prestamo.getFechaDevolucionReal()
        );
    }
}
