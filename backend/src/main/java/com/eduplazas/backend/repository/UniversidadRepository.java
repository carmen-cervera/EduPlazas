package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Universidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversidadRepository extends JpaRepository<Universidad, Long> {
}