package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Long> {
    Optional<Convocatoria> findByEstado(String estado);
}