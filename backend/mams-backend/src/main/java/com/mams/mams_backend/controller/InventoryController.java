package com.mams.mams_backend.controller;

import com.mams.mams_backend.dto.InventoryDTO;
import com.mams.mams_backend.repository.AssetRepository;
import com.mams.mams_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
@RequiredArgsConstructor
public class InventoryController {

    private final AssetRepository assetRepository;
    private final SecurityUtil securityUtil;

    @GetMapping
    @Transactional(readOnly = true)
    public List<InventoryDTO> stock(@RequestParam(required = false) Long baseId,
                                    @RequestParam(required = false) Long equipmentTypeId) {
        Long scoped = securityUtil.resolveBaseId(baseId);
        return assetRepository.findFiltered(scoped, equipmentTypeId).stream().map(InventoryDTO::from).toList();
    }
}
