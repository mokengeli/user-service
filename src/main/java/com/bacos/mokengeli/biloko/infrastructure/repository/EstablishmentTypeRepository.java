package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.EstablishmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstablishmentTypeRepository
        extends JpaRepository<EstablishmentType, Long> {
    Optional<EstablishmentType> findByCode(String code);
}
