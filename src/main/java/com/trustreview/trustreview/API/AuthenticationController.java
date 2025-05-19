package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Model.ChangePasswordRequest;
import com.trustreview.trustreview.Model.LoginRequest;
import com.trustreview.trustreview.Model.RegisterRequest;
import com.trustreview.trustreview.Service.AuthenticationService;
import com.trustreview.trustreview.Utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        Account account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody RegisterRequest registerRequest) {
        Account account = authenticationService.register(registerRequest);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/accountCurrent")
    public Account current() {
        return accountUtils.getAccountCurrent();
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
}

