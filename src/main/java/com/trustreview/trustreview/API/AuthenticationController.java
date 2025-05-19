package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Model.LoginRequest;
import com.trustreview.trustreview.Service.AuthenticationService;
import com.trustreview.trustreview.Utils.AccountUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/testcurrent")
    public Account current() {
        return accountUtils.getAccountCurrent();
    }

}

