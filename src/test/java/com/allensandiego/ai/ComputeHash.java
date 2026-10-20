package com.allensandiego.ai;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ComputeHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String password = "password123";
        String hash = encoder.encode(password);
        System.out.println("Hash for " + password + ": " + hash);
    }
}
