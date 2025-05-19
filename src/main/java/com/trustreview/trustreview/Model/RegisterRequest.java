package com.trustreview.trustreview.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class RegisterRequest {
    private String username;
    private String displayName;
    private String email;
    private String password;
}
