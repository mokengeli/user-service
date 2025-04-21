package com.bacos.mokengeli.biloko.infrastructure.repository;

import java.util.Optional;

import com.bacos.mokengeli.biloko.infrastructure.model.TenantUserSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

@Repository
public interface TenantUserSequenceRepository
        extends JpaRepository<TenantUserSequence, Long> {

    /**
     * Lit la ligne de séquence en mode PESSIMISTIC_WRITE
     * pour bloquer la mise à jour concurrente.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM TenantUserSequence s WHERE s.tenantId = :tenantId")
    Optional<TenantUserSequence> findByTenantIdForUpdate(
            @Param("tenantId") Long tenantId
    );
}
