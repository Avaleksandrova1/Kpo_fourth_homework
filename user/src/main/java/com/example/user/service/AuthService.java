package com.example.user.service;

import com.example.user.domain.entity.Role;
import com.example.user.domain.entity.Session;
import com.example.user.domain.entity.User;
import com.example.user.domain.repository.SessionRepository;
import com.example.user.domain.repository.UserRepository;
import com.example.user.datatransfer.jwt.JwtResponseDto;
import com.example.user.datatransfer.userResponces.LoginUserDto;
import com.example.user.datatransfer.userResponces.RegisterUserDto;
import com.example.user.exceptions.InvalidException;
import com.example.user.service.constants.ErrorMessagesConstants;
import com.example.user.service.constants.UserAuthConstants;
import com.example.user.utils.Keys;
import com.example.user.utils.MD5HashFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public JwtResponseDto getAccessToken(String token) throws InvalidException {
        if (!sessionRepository.existsBySessionToken(token)) {
            throw new InvalidException(HttpStatus.NOT_FOUND, "Invalid token");
        }

        Session session = sessionRepository.findBySessionToken(token);
        if (session.getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            sessionRepository.deleteById(session.getId());
            throw new InvalidException(HttpStatus.UNAUTHORIZED, "Token expired");
        }

        User user = session.getUser();
        return createNewToken(user);
    }

    private JwtResponseDto createNewToken(User user) throws InvalidException {
        String jsonEncoded = JwtSignatureCreator.getEncodedJson(user);
        String signature = JwtSignatureCreator.getSignature(jsonEncoded, Keys.secret_key);
        if ("".equals(signature)) {
            throw new InvalidException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessagesConstants.INTERNAL_SERVER_ERROR);
        }
        String token = jsonEncoded + "." + signature;
        String refreshToken = KeyGenerator.generateToken();

        if (sessionRepository.existsByUserId(user.getId())) {
            Session session = sessionRepository.findByUserId(user.getId())
                    .setSessionToken(token)
                    .setExpiresAt(Timestamp.valueOf(LocalDateTime.now().
                            plusMinutes(JwtSignatureCreator.REFRESH_TOKEN_EXPIRATION_TIME)));
            sessionRepository.save(session);
        } else {
            Session session = new Session()
                    .setSessionToken(token)
                    .setExpiresAt(Timestamp.valueOf(LocalDateTime.now().
                            plusMinutes(JwtSignatureCreator.REFRESH_TOKEN_EXPIRATION_TIME)))
                    .setUser(user);
            user.setSession(session);
            session.setUser(user);
            userRepository.save(user);
        }
        return new JwtResponseDto().setAccessToken(token).setRefreshToken(refreshToken);
    }

    public String register(RegisterUserDto registerUserDto) throws InvalidException {
        String userName = registerUserDto.getUsername();
        String email = registerUserDto.getEmail();
        checkUsernameAndEmail(userName, email);
        String passwordHash = MD5HashFunction.getMD5Hash(registerUserDto.getPassword());
        userRepository.save(new User()
                .setRole(Role.CUSTOMER)
                .setEmail(email)
                .setUsername(userName)
                .setPasswordHash(passwordHash)
                .setCreatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()))
        );
        return "Successfully registered";
    }

    private void checkUsernameAndEmail(String userName, String email) throws InvalidException {
        if (!userName.matches("^[A-Za-z0-9_]{3,15}$")) {
            throw new InvalidException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    UserAuthConstants.INVALID_USERNAME
            );
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    UserAuthConstants.INVALID_EMAIL
            );
        }

        if (userRepository.existsByUsername(userName)) {
            throw new InvalidException(
                    HttpStatus.CONFLICT,
                    UserAuthConstants.USERNAME_IS_TAKEN
            );
        }
        if (userRepository.existsByEmail(email)) {
            throw new InvalidException(
                    HttpStatus.CONFLICT,
                    UserAuthConstants.EMAIL_IS_TAKEN
            );
        }
    }

    public JwtResponseDto login(LoginUserDto authRequest) throws InvalidException {
        boolean emailExists = userRepository.existsByEmail(authRequest.getEmail());
        if (!emailExists) {
            throw new InvalidException(HttpStatus.NOT_FOUND,
                    UserAuthConstants.EMAIL_NOT_FOUND);
        }
        User user = userRepository.findByEmail(authRequest.getEmail());
        String storedHashedPassword = user.getPasswordHash();
        if (!storedHashedPassword.equals(MD5HashFunction.getMD5Hash(authRequest.getPassword()))) {
            throw new InvalidException(HttpStatus.CONFLICT,
                    UserAuthConstants.WRONG_PASSWORD);
        }
        return createNewToken(user);
    }


}
