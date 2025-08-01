package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.Users;
import com.trustreview.trustreview.Model.*;
import com.trustreview.trustreview.Service.AuthenticationService;
import com.trustreview.trustreview.Utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/accounts")
@SecurityRequirement(name = "bearerAuth")

public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AccountUtils accountUtils;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        Object account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/register/user")
    public ResponseEntity<Users> register(@RequestBody RegisterUserRequest registerRequest) {
        Users user = authenticationService.registerUser(registerRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register/partner")
    public ResponseEntity<Partner> registerPartner(@RequestBody RegisterPartnerRequest registerPartnerRequest) {
        Partner partner = authenticationService.registerPartner(registerPartnerRequest);
        return ResponseEntity.ok(partner);
    }

    @GetMapping("/accountCurrent")
    public ResponseEntity<?> current() {
        Account account = accountUtils.getAccountCurrent();
        return ResponseEntity.ok(account);
    }

    @PutMapping("/displayName/{id}/{displayname}")
    public ResponseEntity<String> updateDln(@PathVariable Long id, @PathVariable String displayname) {
        return ResponseEntity.ok(authenticationService.updateDisplayName(id, displayname));
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(authenticationService.changePassword(changePasswordRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccountById(@PathVariable("id") long id){
        return ResponseEntity.ok(authenticationService.deleteAccountById(id));
    }

    @GetMapping("/account-counts")
    public Map<String, Long> getAccountCounts() {
        return authenticationService.getAccountCounts();
    }

    @GetMapping("/registration-growth")
    public Map<String, Object> getRegistrationGrowth() {
        return authenticationService.getRegistrationGrowth();
    }

    @GetMapping("/online-users")
    public Map<String, Long> getOnlineUsersCount() {
        return authenticationService.getOnlineUsersCount();
    }
}

