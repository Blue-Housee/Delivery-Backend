package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.user.SignUpRequestDto;
import com.spring.delivery.domain.controller.dto.user.UserUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import com.spring.delivery.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    public User signup(SignUpRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인 ( 관리자 권한은 Token으로 확인 )
        Role role = requestDto.getRole();
        if (role.equals(Role.MANAGER) || role.equals(Role.MASTER)) { //관리자 권한
            System.out.print(ADMIN_TOKEN);
            System.out.print(requestDto.getAdminToken());
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
        }

        // 사용자 등록
        User user = User.createUser(username, email, password, role);
        userRepository.save(user);

        return user;
    }

    public User getUser(Long id, UserDetailsImpl userDetails) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        String currentUsername = userDetails.getUsername();
        Role currentUserRole = userDetails.getUser().getRole();

        // 자기 자신 or MANAGER, MASTER 만 접근 가능
        if (user.getUsername().equals(currentUsername) ||
                currentUserRole == Role.MANAGER ||
                currentUserRole == Role.MASTER
        ) {
            return user;
        }

        throw new AccessDeniedException("접근 권한이 없는 사용자입니다.");
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequestDto requestDto, UserDetailsImpl userDetails) {

        log.info(requestDto.toString());
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        String currentUsername = userDetails.getUsername();
        Role currentUserRole = userDetails.getUser().getRole();

        // 자기 자신 or MANAGER, MASTER 만 접근 가능
        if (!(user.getUsername().equals(currentUsername) ||
                currentUserRole == Role.MANAGER ||
                currentUserRole == Role.MASTER)
        ) {
            throw new AccessDeniedException("접근 권한이 없는 사용자입니다.");
        }

        //TODO: username 변경 시, Jwt 를 새로 발급해 주던지, 로그아웃 시키고, 로그인하도록 해야함. => 지금은 client가 없으므로, Jwt를 새로 발급해줘야 할 듯.

        // username, email 중복 확인
        if (StringUtils.hasText(requestDto.getUsername()) && !user.getUsername().equals(requestDto.getUsername())) {
            Optional<User> userFindByUsername = userRepository.findByUsername(requestDto.getUsername());
            if (userFindByUsername.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 username입니다.");
            }
        }

        if (StringUtils.hasText(requestDto.getEmail()) && !user.getEmail().equals(requestDto.getEmail())) {
            Optional<User> userFindByEmail = userRepository.findByEmail(requestDto.getEmail());
            if (userFindByEmail.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 email입니다.");
            }
        }

        String newPassword = null;
        // newPassword 가 있으면 originPassword 확인
        if (StringUtils.hasText(requestDto.getNewPassword())) {
            if (!passwordEncoder.matches(requestDto.getOriginPassword(), user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            newPassword = passwordEncoder.encode(requestDto.getNewPassword());
        }

        // 유저 정보 업데이트
        user.updateUser(
                requestDto.getUsername(),
                requestDto.getEmail(),
                newPassword
        );

        log.info(user.toString());
        return user;
    }
}
