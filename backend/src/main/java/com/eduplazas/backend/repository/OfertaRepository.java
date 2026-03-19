package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    List<Oferta> findByConvocatoriaId(Long convocatoriaId);
}