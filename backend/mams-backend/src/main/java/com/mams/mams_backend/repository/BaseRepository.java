package com.mams.mams_backend.repository;

import com.mams.mams_backend.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository extends JpaRepository<Base, Long> {
    boolean existsByName(String name);
}
