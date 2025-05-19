//package com.trustreview.trustreview.Service;
//
//import com.trustreview.trustreview.Config.SecurityConfig;
//import com.trustreview.trustreview.Entity.Account;
//import com.trustreview.trustreview.Enums.AccountStatus;
//import com.trustreview.trustreview.Model.AccountReponse;
//import com.trustreview.trustreview.Model.LoginRequest;
//import com.trustreview.trustreview.Repository.AuthenticationRepository;
//import jakarta.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import java.time.LocalDateTime;
//
//@Service
//@Transactional
//
//public class AuthenticationService implements UserDetailsService {
//
//    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
//
//    @Autowired
//    AuthenticationManager authenticationManager;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Autowired
//    TokenService tokenService;
//
//    @Autowired
//    AuthenticationRepository authenticationRepository;
//
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return authenticationRepository.findByUsername(username);
//    }
//
//    public AccountReponse login(LoginRequest loginRequest) {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                    loginRequest.getUsername(),
//                    loginRequest.getPassword()
//            ));
//            Account account = authenticationRepository.findByUsername(loginRequest.getUsername());
//            if (account == null || !securityConfig.passwordEncoder().matches(loginRequest.getPassword(), account.getPassword())) {
//                throw new BadCredentialsException("Incorrect username or password");
//            }
//            if(!account.getStatus().equals(AccountStatus.ACTIVE)){
//                throw new AuthenticationServiceException("Your account locked!!!");
//            }
//            AccountReponse accountReponse = new AccountReponse();
//            String token = tokenService.generateToken(account);
//            accountReponse.setUsername(account.getUsername());
//            accountReponse.setId(account.getId());
//            accountReponse.setDisplayName(account.getDisplayName());
//            accountReponse.setEmail(account.getEmail());
//            accountReponse.setRole(account.getRole());
//            accountReponse.setStatus(account.getStatus());
//            accountReponse.setCreatedAt(LocalDateTime.now());
//            accountReponse.setToken(token);
//            return accountReponse;
//        } catch (AuthenticationException e) {
//            throw new BadCredentialsException("Incorrect username or password!");
//        }
//    }
//}

package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Enums.AccountStatus;
import com.trustreview.trustreview.Model.AccountReponse;
import com.trustreview.trustreview.Model.LoginRequest;
import com.trustreview.trustreview.Repository.AuthenticationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Transactional
public class AuthenticationService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    @Lazy
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        Account account = authenticationRepository.findByUsername(username);
        if (account == null) {
            log.warn("Username not found: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return account;
    }

    public AccountReponse login(LoginRequest loginRequest) {
        try {
            log.info("Attempting login for user: {}", loginRequest.getUsername());
            // The AuthenticationManager will be fully resolved here when authenticate() is called
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));

            Account account = authenticationRepository.findByUsername(loginRequest.getUsername());
            if (account == null) {
                log.error("Critical error: Account {} not found after successful authenticationManager.authenticate() call.", loginRequest.getUsername());
                throw new AuthenticationServiceException("Authentication process error."); // Should not happen
            }

            if(!account.getStatus().equals(AccountStatus.ACTIVE)){
                log.warn("Login failed: Account for user {} is not active. Status: {}", account.getUsername(), account.getStatus());
                throw new AuthenticationServiceException("Your account is locked or not active.");
            }

            AccountReponse accountReponse = new AccountReponse();
            String token = tokenService.generateToken(account); // Ensure tokenService.generateToken(account) is working
            accountReponse.setUsername(account.getUsername());
            accountReponse.setId(account.getId());
            accountReponse.setDisplayName(account.getDisplayName());
            accountReponse.setEmail(account.getEmail());
            accountReponse.setRole(account.getRole());
            accountReponse.setStatus(account.getStatus());
            accountReponse.setCreatedAt(LocalDateTime.now()); // Or account.getCreatedAt() depending on desired logic
            accountReponse.setToken(token);
            log.info("Login successful for user: {}", loginRequest.getUsername());
            return accountReponse;
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user {}: Bad credentials", loginRequest.getUsername(), e);
            throw new BadCredentialsException("Incorrect username or password!");
        } catch (AuthenticationServiceException e) {
            log.warn("Login failed for user {}: Account status issue - {}", loginRequest.getUsername(), e.getMessage(), e);
            throw e; // Re-throw specific service exception
        } catch (AuthenticationException e) {
            log.warn("Login failed for user {}: AuthenticationException - {}", loginRequest.getUsername(), e.getMessage(), e);
            throw new BadCredentialsException("Incorrect username or password due to authentication failure!");
        }
    }
}
