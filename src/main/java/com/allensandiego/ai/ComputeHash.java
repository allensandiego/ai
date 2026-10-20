package com.allensandiego.ai;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ComputeHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("password123");
        System.out.println("Hash: " + hash);
    }
}
