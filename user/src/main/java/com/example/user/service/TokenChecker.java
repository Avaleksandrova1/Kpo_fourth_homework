package com.example.user.service;

import com.example.user.datatransfer.jwt.JwtHeaderDto;
import com.example.user.datatransfer.jwt.JwtPayloadDto;
import com.example.user.utils.Keys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;

public class TokenChecker {
    public static boolean isTokenValid(String token) {
        String header;
        String payload;
        String signature;

        String[] tokenParts = token.split("\\.");
        if (tokenParts.length != 3) {
            return false;
        }

        try {
            header = new String(Base64.decodeBase64(tokenParts[0]));
            payload = new String(Base64.decodeBase64(tokenParts[1]));
            signature = JwtSignatureCreator.getSignature(tokenParts[0] + "." + tokenParts[1], Keys.secret_key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (!signature.equals(tokenParts[2])) {
            return false;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readValue(header, JwtHeaderDto.class);
            objectMapper.readValue(payload, JwtPayloadDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }

        return getJson(header, new JwtHeaderDto()) != null && getJson(payload, new JwtPayloadDto()) != null;
    }
    public static boolean isValidJson(String value) {
        try {
            new ObjectMapper().readTree(value);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
    public static <T> Object getJson(String value, T object) {
        try {
            return new ObjectMapper().readValue(value, object.getClass());
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static JwtPayloadDto getPayload(String token) {
        String[] tokenParts = token.split("\\.");
        String payload = new String(Base64.decodeBase64(tokenParts[1]));
        return (JwtPayloadDto) getJson(payload, new JwtPayloadDto());
    }

    public static String checkPayload(JwtPayloadDto payloadDto) {
        Long notBefore = payloadDto.getNbf();
        Long expiredIn = payloadDto.getExp();
        Long current = System.currentTimeMillis();
        if (current < notBefore) {
            return "error while creating token";
        } else if (expiredIn < current) {
            return "token expired";
        }
        return "ok";
    }
}
