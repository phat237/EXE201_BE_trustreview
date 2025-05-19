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

import com.trustreview.trustreview.Config.SecurityConfig;
import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Enums.AccountRoles;
import com.trustreview.trustreview.Enums.AccountStatus;
import com.trustreview.trustreview.Model.AccountReponse;
import com.trustreview.trustreview.Model.ChangePasswordRequest;
import com.trustreview.trustreview.Model.LoginRequest;
import com.trustreview.trustreview.Model.RegisterRequest;
import com.trustreview.trustreview.Repository.AuthenticationRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
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

    @Autowired
    SecurityConfig securityConfig;

    @Autowired
    AccountUtils accountUtils;

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
            if(loginRequest == null || loginRequest.getPassword().isEmpty() || loginRequest.getUsername().isEmpty()){
                throw new BadCredentialsException("Vui lòng điền đầy đủ thông tin đăng nhập!");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));
            Account account = authenticationRepository.findByUsername(loginRequest.getUsername());
            if (account == null || !securityConfig.passwordEncoder().matches(loginRequest.getPassword(), account.getPassword())) {
                throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng!");
            }
            if(!account.getStatus().equals(AccountStatus.ACTIVE)){
                throw new AuthenticationServiceException("Tài khoản bạn đã bị khóa!");
            }

            AccountReponse accountReponse = new AccountReponse();
            String token = tokenService.generateToken(account);
            accountReponse.setUsername(account.getUsername());
            accountReponse.setId(account.getId());
            accountReponse.setDisplayName(account.getDisplayName());
            accountReponse.setEmail(account.getEmail());
            accountReponse.setRole(account.getRole());
            accountReponse.setStatus(account.getStatus());
            accountReponse.setCreatedAt(LocalDateTime.now());
            accountReponse.setToken(token);
            return accountReponse;
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng!");
        }
    }

    public Account register(RegisterRequest registerRequest) {
        if (authenticationRepository.findByUsername(registerRequest.getUsername()) != null){
            throw new BadCredentialsException("Tên đăng nhập bị trùng, vui lòng chọn một tên khác!");
        }
        Account account = new Account();
        account.setUsername(registerRequest.getUsername());
        account.setDisplayName(registerRequest.getDisplayName());
        account.setEmail(registerRequest.getEmail());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setRole(AccountRoles.USER);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        return authenticationRepository.save(account);
    }

    public String updateDisplayName(Long id, String displaynameRequest) {
        Account account = authenticationRepository.findAccountById(id);
        if (account != null){
            account.setDisplayName(displaynameRequest);
            authenticationRepository.save(account);
            return "Đã cập nhật tên hiển thị thành " + displaynameRequest + "!";
        } else {
            return "Cập nhật tên hiển thị không thành công!";
        }
    }

    public String changePassword(ChangePasswordRequest changePasswordRequest) {
        Account account = authenticationRepository.findAccountById(accountUtils.getAccountCurrent().getId());
        if (securityConfig.passwordEncoder().matches(changePasswordRequest.getOldPassword(), account.getPassword())) {
            account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            authenticationRepository.save(account);
            return "Thay đổi mật khẩu thành công!";
        } else {
            return "Có lỗi xảy ra, thay đổi mật khẩu thất bại!";
        }
    }

    public String deleteAccountById(long id) {
        if (authenticationRepository.findAccountById(id) != null){
            authenticationRepository.deleteById(id);
            return "Đã xóa tài khoản thành công!";
        } else {
            return "Có lỗi xảy ra, xóa tài khoản không thành công!";
        }
    }
}
