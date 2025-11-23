package com.pokeshop.cambioDeMoneda.repository;

import com.pokeshop.cambioDeMoneda.model.TasaCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TasaCambioRepository extends JpaRepository<TasaCambio, Long> {
    // Buscar tasa por fecha y código
    Optional<TasaCambio> findByFechaAndCodigoMoneda(LocalDate fecha, String codigoMoneda);
}
