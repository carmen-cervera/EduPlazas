package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {
}