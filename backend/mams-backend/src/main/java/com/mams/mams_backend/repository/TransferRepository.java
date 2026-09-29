package com.mams.mams_backend.repository;

import com.mams.mams_backend.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("""
        SELECT t FROM Transfer t
        WHERE (:baseId IS NULL OR t.sourceBase.id = :baseId OR t.destinationBase.id = :baseId)
          AND (:typeId IS NULL OR t.equipmentType.id = :typeId)
          AND t.transferDate >= :start AND t.transferDate < :end
        ORDER BY t.transferDate DESC
        """)
    Page<Transfer> findHistory(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                               @Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                               Pageable pageable);

    @Query("""
        SELECT t FROM Transfer t
        WHERE (:baseId IS NULL OR t.destinationBase.id = :baseId)
          AND (:typeId IS NULL OR t.equipmentType.id = :typeId)
          AND t.transferDate >= :start AND t.transferDate < :end
          AND t.status = com.mams.mams_backend.enums.TransferStatus.COMPLETED
        ORDER BY t.transferDate DESC
        """)
    List<Transfer> findIncoming(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                                @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
        SELECT t FROM Transfer t
        WHERE (:baseId IS NULL OR t.sourceBase.id = :baseId)
          AND (:typeId IS NULL OR t.equipmentType.id = :typeId)
          AND t.transferDate >= :start AND t.transferDate < :end
          AND t.status = com.mams.mams_backend.enums.TransferStatus.COMPLETED
        ORDER BY t.transferDate DESC
        """)
    List<Transfer> findOutgoing(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                                @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
        SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t
        WHERE (:baseId IS NULL OR t.destinationBase.id = :baseId)
          AND (:typeId IS NULL OR t.equipmentType.id = :typeId)
          AND t.transferDate >= :start AND t.transferDate < :end
          AND t.status = com.mams.mams_backend.enums.TransferStatus.COMPLETED
        """)
    long sumIn(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
               @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
        SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t
        WHERE (:baseId IS NULL OR t.sourceBase.id = :baseId)
          AND (:typeId IS NULL OR t.equipmentType.id = :typeId)
          AND t.transferDate >= :start AND t.transferDate < :end
          AND t.status = com.mams.mams_backend.enums.TransferStatus.COMPLETED
        """)
    long sumOut(@Param("baseId") Long baseId, @Param("typeId") Long typeId,
                @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}