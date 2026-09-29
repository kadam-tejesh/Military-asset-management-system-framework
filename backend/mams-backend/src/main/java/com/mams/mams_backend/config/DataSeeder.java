package com.mams.mams_backend.config;

import com.mams.mams_backend.entity.Base;
import com.mams.mams_backend.entity.EquipmentType;
import com.mams.mams_backend.entity.Role;
import com.mams.mams_backend.entity.User;
import com.mams.mams_backend.enums.RoleName;
import com.mams.mams_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        for (RoleName rn : RoleName.values()) {
            if (roleRepository.findByName(rn).isEmpty()) {
                Role r = new Role();
                r.setName(rn);
                roleRepository.save(r);
            }
        }

        if (baseRepository.count() == 0) {
            baseRepository.save(new Base(null, "Alpha Base", "Northern Command"));
            baseRepository.save(new Base(null, "Bravo Base", "Western Command"));
        }

        for (String[] t : new String[][]{{"WEAPON", "Arms"}, {"VEHICLE", "Transport"}, {"AMMUNITION", "Munitions"}}) {
            if (!equipmentTypeRepository.existsByName(t[0])) {
                equipmentTypeRepository.save(new EquipmentType(null, t[0], t[1]));
            }
        }

        Base alpha = baseRepository.findAll().get(0);
        createUser("admin", "Admin@123", RoleName.ADMIN, null);
        createUser("commander", "Command@123", RoleName.BASE_COMMANDER, alpha);
        createUser("logistics", "Logistics@123", RoleName.LOGISTICS_OFFICER, alpha);
    }

    private void createUser(String username, String rawPassword, RoleName role, Base base) {
        if (userRepository.existsByUsername(username)) return;
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setEmail(username + "@mams.local");
        u.setRole(roleRepository.findByName(role).orElseThrow());
        u.setBase(base);
        u.setEnabled(true);
        userRepository.save(u);
    }
}
