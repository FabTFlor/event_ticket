package com.eventtickets.eventtickets.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String name;
    private String email;
    private String dni;
    private String phoneNumber;
    private String currentPassword;
}