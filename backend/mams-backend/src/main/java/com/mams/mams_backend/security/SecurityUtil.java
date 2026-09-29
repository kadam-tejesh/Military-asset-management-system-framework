package com.mams.mams_backend.security;

import com.mams.mams_backend.entity.User;
import com.mams.mams_backend.enums.RoleName;
import com.mams.mams_backend.exception.BadRequestException;
import com.mams.mams_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("Not authenticated");
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    public boolean isAdmin() {
        return currentUser().getRole().getName() == RoleName.ADMIN;
    }

    /** For reads: ADMIN may pass any base (or null = all). Others are locked to their own base. */
    public Long resolveBaseId(Long requested) {
        User u = currentUser();
        if (u.getRole().getName() == RoleName.ADMIN) {
            return requested;
        }
        if (u.getBase() == null) {
            throw new AccessDeniedException("No base assigned to this user");
        }
        Long own = u.getBase().getId();
        if (requested != null && !requested.equals(own)) {
            throw new AccessDeniedException("You can only access your own base");
        }
        return own;
    }

    /** For writes: a concrete base is mandatory. */
    public Long resolveBaseIdForWrite(Long requested) {
        Long id = resolveBaseId(requested);
        if (id == null) {
            throw new BadRequestException("baseId is required");
        }
        return id;
    }
}