package com.example.TPO.Tpo.TPODTO;

import com.example.TPO.DBMS.Tpo.TPOUser;
import org.springframework.stereotype.Component;

@Component

public
class  TPOUserMapper {

    // Convert Entity to DTO
    public static TPOUserDTO toDTO(TPOUser tpoUser) {
        return new TPOUserDTO(
                tpoUser.getId(),
                tpoUser.getUser().getUsername(),
                tpoUser.getUser().getId(),
                tpoUser.getUser().getEmail(),
                tpoUser.getRole(),
                tpoUser.getUser().isVerified()
        );
    }

    // Convert DTO to Entity
    public static TPOUser toEntity(TPOUserDTO dto) {
        TPOUser tpoUser = new TPOUser();
        tpoUser.setId(dto.getId());
        tpoUser.setRole(dto.getRole());
        return tpoUser;
    }
}
