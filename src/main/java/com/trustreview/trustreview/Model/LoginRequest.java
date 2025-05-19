package com.trustreview.trustreview.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class LoginRequest {
    String username;
    String password;
}
