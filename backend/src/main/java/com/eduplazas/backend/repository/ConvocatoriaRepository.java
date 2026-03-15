package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Long> {
}