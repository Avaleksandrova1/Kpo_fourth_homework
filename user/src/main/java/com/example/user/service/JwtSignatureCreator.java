package com.example.user.service;

import com.example.user.domain.entity.User;
import com.example.user.datatransfer.jwt.JwtHeaderDto;
import com.example.user.datatransfer.jwt.JwtPayloadDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.Encoders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class JwtSignatureCreator {
    public static final int REFRESH_TOKEN_EXPIRATION_TIME = 60;
    public static final int ACCESS_TOKEN_LIVE_TIME = 2;

    public static String ALGORITHM = "HmacSHA512";

    public static String getSignature(String data, String key) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Mac mac;
        try {
            mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return Encoders.BASE64.encode(mac.doFinal(data.getBytes()));
    }

    /**
     * Get expired time in milliseconds
     * @param mins minutes
     * @return expired time in milliseconds
     */
    public static Long getExpiredIn(int mins) {
        return System.currentTimeMillis() + (long) mins * 60 * 1000;
    }

    /**
     * Get encoded json string
     * @param user user
     * @return encoded json string
     */
    public static String getEncodedJson(User user) {
        JwtHeaderDto headerDto = new JwtHeaderDto()
                .setAlg("HS512")
                .setTyp("JWT");

        Long expireIn = getExpiredIn(ACCESS_TOKEN_LIVE_TIME);
        JwtPayloadDto payloadDto = new JwtPayloadDto()
                .setIss("Auth")
                .setExp(expireIn)
                .setNbf(System.currentTimeMillis())
                .setRole(user.getRole())
                .setUsername(user.getUsername());


        return getEncodedJsonHelper(headerDto, payloadDto);
    }

    /**
     * Get encoded json string
     * @param headerDto header dto
     * @param payloadDto payload dto
     * @return encoded json string
     */
    private static String getEncodedJsonHelper(JwtHeaderDto headerDto, JwtPayloadDto payloadDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonEncoded;
        try {
            jsonEncoded = Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(headerDto).getBytes()) +
                    "." +
                    Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(payloadDto).getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonEncoded;
    }

}
