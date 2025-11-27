package com.example.secure.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Base64;

public class Base64PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        // Перетворюємо пароль у байти і кодуємо в рядок Base64
        return Base64.getEncoder().encodeToString(rawPassword.toString().getBytes());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Щоб перевірити пароль, ми кодуємо те, що ввів користувач (rawPassword),
        // і порівнюємо з тим, що лежить у базі (encodedPassword).
        String encodedRaw = encode(rawPassword);
        return encodedRaw.equals(encodedPassword);
    }
}