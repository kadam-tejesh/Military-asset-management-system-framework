package com.mams.mams_backend.repository;

import com.mams.mams_backend.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("""
        SELECT p FROM Purchase p
        WHERE (:baseId IS NULL OR p.base.id = :baseId)
          AND (:typeId IS NULL OR p.equipmentType.id = :typeId)
          AND p.purchaseDate BETWEEN :from AND :to
        ORDER BY p.purchaseDate DESC, p.id DESC
        """)
    Page<Purchase> findFiltered(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                                @Param("from") LocalDate from, @Param("to") LocalDate to,
                                Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(p.quantity), 0) FROM Purchase p
        WHERE (:baseId IS NULL OR p.base.id = :baseId)
          AND (:typeId IS NULL OR p.equipmentType.id = :typeId)
          AND p.purchaseDate BETWEEN :from AND :to
        """)
    long sumBetween(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                    @Param("from") LocalDate from, @Param("to") LocalDate to);
}