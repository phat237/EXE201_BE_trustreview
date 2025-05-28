package com.trustreview.trustreview.Model;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterPartnerRequest {
    private String username;
    private String displayName;
    private String email;
    private String password;
    private String companyName;
    private String businessRegistrationNumber;
    private String website;
    private String contactPhone;
}
