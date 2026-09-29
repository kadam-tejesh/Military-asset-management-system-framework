package com.mams.mams_backend.service;

import com.mams.mams_backend.aspect.Audited;
import com.mams.mams_backend.dto.CreateUserRequest;
import com.mams.mams_backend.dto.UserDTO;
import com.mams.mams_backend.entity.Base;
import com.mams.mams_backend.entity.Role;
import com.mams.mams_backend.entity.User;
import com.mams.mams_backend.enums.RoleName;
import com.mams.mams_backend.exception.BadRequestException;
import com.mams.mams_backend.exception.NotFoundException;
import com.mams.mams_backend.repository.BaseRepository;
import com.mams.mams_backend.repository.RoleRepository;
import com.mams.mams_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BaseRepository baseRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Audited(action = "CREATE_USER", entityType = "User")
    public UserDTO create(CreateUserRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BadRequestException("Username already exists");
        }
        Role role = roleRepository.findByName(req.role())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        Base base = null;
        if (req.role() != RoleName.ADMIN) {
            if (req.baseId() == null) {
                throw new BadRequestException("baseId is required for " + req.role());
            }
            base = baseRepository.findById(req.baseId())
                    .orElseThrow(() -> new NotFoundException("Base not found"));
        }

        User u = new User();
        u.setUsername(req.username());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setEmail(req.email());
        u.setRole(role);
        u.setBase(base);
        u.setEnabled(true);
        return UserDTO.from(userRepository.save(u));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> list() {
        return userRepository.findAll().stream().map(UserDTO::from).toList();
    }
}
