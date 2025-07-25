package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.Tenant;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Tenant findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByName(String name);
    boolean existsByEmail(String email);

    Page<Tenant> findByNameContainingIgnoreCase(String search, Pageable pageable);
}
