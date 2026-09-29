package com.mams.mams_backend.repository;

import com.mams.mams_backend.entity.Assignment;
import com.mams.mams_backend.enums.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("""
        SELECT a FROM Assignment a
        WHERE (:baseId IS NULL OR a.base.id = :baseId)
          AND (:typeId IS NULL OR a.asset.equipmentType.id = :typeId)
          AND (:status IS NULL OR a.status = :status)
          AND a.assignedDate BETWEEN :from AND :to
        ORDER BY a.assignedDate DESC, a.id DESC
        """)
    Page<Assignment> findFiltered(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                                  @Param("status") AssignmentStatus status,
                                  @Param("from") LocalDate from, @Param("to") LocalDate to,
                                  Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a
        WHERE (:baseId IS NULL OR a.base.id = :baseId)
          AND (:typeId IS NULL OR a.asset.equipmentType.id = :typeId)
          AND a.status = com.mams.mams_backend.enums.AssignmentStatus.ASSIGNED
          AND a.assignedDate BETWEEN :from AND :to
        """)
    long sumAssigned(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                     @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
        SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a
        WHERE (:baseId IS NULL OR a.base.id = :baseId)
          AND (:typeId IS NULL OR a.asset.equipmentType.id = :typeId)
          AND a.status = com.mams.mams_backend.enums.AssignmentStatus.EXPENDED
          AND a.expendedDate BETWEEN :from AND :to
        """)
    long sumExpended(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                     @Param("from") LocalDate from, @Param("to") LocalDate to);
}
