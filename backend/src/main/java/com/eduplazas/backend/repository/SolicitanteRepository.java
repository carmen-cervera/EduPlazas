package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitanteRepository extends JpaRepository<Solicitante, Long> {
}