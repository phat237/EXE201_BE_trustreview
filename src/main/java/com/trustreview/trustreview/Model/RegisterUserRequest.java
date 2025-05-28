package com.trustreview.trustreview.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class RegisterUserRequest {
    private String username;
    private String displayName;
    private String email;
    private String password;
}
