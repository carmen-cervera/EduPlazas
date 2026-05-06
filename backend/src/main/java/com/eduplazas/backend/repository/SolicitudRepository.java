package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Solicitud;
import com.eduplazas.backend.dto.SolicitudRecibidaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    Optional<Solicitud> findBySolicitanteIdAndConvocatoriaId(Long solicitanteId, Long convocatoriaId);

    Optional<Solicitud> findBySolicitanteUsuarioId(Long usuarioId);

    @Query("SELECT new com.eduplazas.backend.dto.SolicitudRecibidaDTO(" +
            "s.id, " +
            "s.solicitante.nombre, " + 
            "o.grado, " + 
            "INDEX(o) + 1, " +
            "s.estado) " +
            "FROM Solicitud s JOIN s.preferencias o " +
            "WHERE o.universidad.id = :univId")
    List<SolicitudRecibidaDTO> findSolicitudesByUniversidadId(@Param("univId") Long univId);
}