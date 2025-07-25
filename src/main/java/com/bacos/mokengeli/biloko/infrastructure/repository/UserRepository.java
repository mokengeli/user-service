package com.bacos.mokengeli.biloko.infrastructure.repository;

import com.bacos.mokengeli.biloko.infrastructure.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeNumber(String employeeNumber);

    Page<User> findByTenantCode(String tenantCode, Pageable pageable);

    @Query("""
            SELECT r.label AS role, COUNT(u) AS count
            FROM User u
            JOIN u.roles r
            WHERE u.tenant.code = :tenantCode
            GROUP BY r.label
            """)
    List<RoleCount> countByTenantCodeGroupByRole(@Param("tenantCode") String tenantCode);

    boolean existsByUserName(String userName);

    Page<User> findByTenantCodeAndLastNameContainingIgnoreCase(String tenantCode, String search, Pageable pageable);


    interface RoleCount {
        String getRole();

        Long getCount();
    }

}
