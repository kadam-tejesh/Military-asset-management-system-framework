package com.mams.mams_backend.controller;

import com.mams.mams_backend.dto.LookupDTO;
import com.mams.mams_backend.entity.User;
import com.mams.mams_backend.repository.BaseRepository;
import com.mams.mams_backend.repository.EquipmentTypeRepository;
import com.mams.mams_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookup")
@RequiredArgsConstructor
public class LookupController {

    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final SecurityUtil securityUtil;

    /** Admin: all bases. Others: only their own base (drives the Base filter dropdown). */
    @GetMapping("/bases")
    public List<LookupDTO> bases() {
        User u = securityUtil.currentUser();
        if (securityUtil.isAdmin()) {
            return baseRepository.findAll().stream().map(b -> new LookupDTO(b.getId(), b.getName())).toList();
        }
        return List.of(new LookupDTO(u.getBase().getId(), u.getBase().getName()));
    }

    /** Transfer destinations: every base, regardless of role. */
    @GetMapping("/all-bases")
    public List<LookupDTO> allBases() {
        return baseRepository.findAll().stream().map(b -> new LookupDTO(b.getId(), b.getName())).toList();
    }

    @GetMapping("/equipment-types")
    public List<LookupDTO> equipmentTypes() {
        return equipmentTypeRepository.findAll().stream()
                .map(e -> new LookupDTO(e.getId(), e.getName())).toList();
    }
}
