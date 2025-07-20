package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Config.SecurityConfig;
import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.Users;
import com.trustreview.trustreview.Enums.AccountRoles;
import com.trustreview.trustreview.Enums.AccountStatus;
import com.trustreview.trustreview.Model.*;
import com.trustreview.trustreview.Repository.AuthenticationRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
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
//@Transactional
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

    public Object login(LoginRequest loginRequest) {
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

            if(account.getStatus().equals(AccountStatus.PENDING)){
                throw new AuthenticationServiceException("Tài khoản đang chờ xác thực!");
            }

            String token = tokenService.generateToken(account);

            if (account.getRole().equals(AccountRoles.USER) || account.getRole().equals(AccountRoles.ADMIN)){
                UserReponse userReponse = new UserReponse();
                userReponse.setUsername(account.getUsername());
                userReponse.setId(account.getId());
                userReponse.setEmail(account.getEmail());
                userReponse.setRole(account.getRole());
                userReponse.setStatus(account.getStatus());
                userReponse.setCreatedAt(account.getCreatedAt());
                userReponse.setDisplayName(((Users) account).getDisplayName());
                userReponse.setBannedUntil(((Users) account).getBannedUntil());
                userReponse.setPoint(((Users) account).getPoint());
                userReponse.setToken(token);
                return userReponse;
            } else if (account.getRole().equals(AccountRoles.PARTNER)){
                PartnerReponse partnerReponse = new PartnerReponse();
                partnerReponse.setUsername(account.getUsername());
                partnerReponse.setId(account.getId());
                partnerReponse.setEmail(account.getEmail());
                partnerReponse.setRole(account.getRole());
                partnerReponse.setStatus(account.getStatus());
                partnerReponse.setCreatedAt(account.getCreatedAt());
                partnerReponse.setCompanyName(((Partner) account).getCompanyName());
                partnerReponse.setBusinessRegistrationNumber(((Partner) account).getBusinessRegistrationNumber());
                partnerReponse.setWebsite(((Partner) account).getWebsite());
                partnerReponse.setContactPhone(((Partner) account).getContactPhone());
                partnerReponse.setMoney(((Partner) account).getMoney());
                partnerReponse.setToken(token);
                return partnerReponse;
            } else {
                return null;
            }

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng!");
        }
    }

    public Users registerUser(RegisterUserRequest registerRequest) {
        if (authenticationRepository.findByUsername(registerRequest.getUsername()) != null){
            throw new BadCredentialsException("Tên đăng nhập bị trùng, vui lòng chọn một tên khác!");
        }
        Users user = new Users();
        user.setUsername(registerRequest.getUsername());
        user.setDisplayName(registerRequest.getDisplayName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPoint(0);
        user.setRole(AccountRoles.USER);
        user.setStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        return authenticationRepository.save(user);
    }

    public String updateDisplayName(Long id, String displayNameRequest) {
        Account account = authenticationRepository.findAccountById(id);
        if (account == null) {
            return "Tài khoản không tồn tại!";
        }
        if (account instanceof Users) {
            Users user = (Users) account;
            user.setDisplayName(displayNameRequest);
            authenticationRepository.save(user);
            return "Đã cập nhật tên hiển thị thành " + displayNameRequest + "!";
        } else {
            return "Chỉ tài khoản người dùng mới được phép cập nhật tên hiển thị!";
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

    public Partner registerPartner(RegisterPartnerRequest registerPartnerRequest) {
        if (authenticationRepository.findByUsername(registerPartnerRequest.getUsername()) != null){
            throw new BadCredentialsException("Tên đăng nhập bị trùng, vui lòng chọn một tên khác!");
        }
        Partner partner = new Partner();
        partner.setUsername(registerPartnerRequest.getUsername());
        partner.setEmail(registerPartnerRequest.getEmail());
        partner.setPassword(passwordEncoder.encode(registerPartnerRequest.getPassword()));
        partner.setCompanyName(registerPartnerRequest.getCompanyName());
        partner.setBusinessRegistrationNumber(registerPartnerRequest.getBusinessRegistrationNumber());
        partner.setContactPhone(registerPartnerRequest.getContactPhone());
        partner.setWebsite(registerPartnerRequest.getWebsite());
        partner.setMoney(0.0);
        partner.setRole(AccountRoles.PARTNER);
        partner.setStatus(AccountStatus.ACTIVE);
        partner.setCreatedAt(LocalDateTime.now());
        return authenticationRepository.save(partner);
    }
}
