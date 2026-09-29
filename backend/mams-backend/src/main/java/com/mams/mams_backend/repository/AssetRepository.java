package com.mams.mams_backend.repository;

import com.mams.mams_backend.entity.Asset;
import com.mams.mams_backend.enums.AssetStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Asset> findFirstByBaseIdAndEquipmentTypeIdAndStatus(Long baseId, Long equipmentTypeId, AssetStatus status);

    @Query("""
        SELECT a FROM Asset a
        WHERE (:baseId IS NULL OR a.base.id = :baseId)
          AND (:typeId IS NULL OR a.equipmentType.id = :typeId)
        ORDER BY a.base.name, a.equipmentType.name
        """)
    List<Asset> findFiltered(@Param("baseId") Long baseId, @Param("typeId") Long typeId);
}
