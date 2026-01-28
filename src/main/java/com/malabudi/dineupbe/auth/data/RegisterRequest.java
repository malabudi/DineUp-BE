package com.malabudi.dineupbe.auth.data;

import com.malabudi.dineupbe.common.util.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String password;
    private Role role;
}
