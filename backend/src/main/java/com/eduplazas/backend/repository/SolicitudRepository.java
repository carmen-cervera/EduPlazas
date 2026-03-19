package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    Optional<Solicitud> findBySolicitanteIdAndConvocatoriaId(Long solicitanteId, Long convocatoriaId);
    Optional<Solicitud> findBySolicitanteUsuarioId(Long usuarioId);
}