package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.NotaAsignatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaAsignaturaRepository extends JpaRepository<NotaAsignatura, Long> {
    List<NotaAsignatura> findBySolicitanteId(Long solicitanteId);
    void deleteBySolicitanteId(Long solicitanteId);
}
