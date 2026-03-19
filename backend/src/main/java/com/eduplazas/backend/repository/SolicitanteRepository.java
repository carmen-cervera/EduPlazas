package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitanteRepository extends JpaRepository<Solicitante, Long> {
    Optional<Solicitante> findByUsuarioId(Long usuarioId);
}