package com.eventtickets.eventtickets.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    private String currentPassword; // Contraseña actual (para validación)
    private String newPassword; // Nueva contraseña
}