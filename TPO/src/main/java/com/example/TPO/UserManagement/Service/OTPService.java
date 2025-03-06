package com.example.TPO.UserManagement.Service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, Long> otpExpiry = new HashMap<>();

    public String generateOTP(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStorage.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + (5 * 60 * 1000)); // Valid for 5 minutes
        return otp;
    }

    public boolean validateOTP(String email, String otp) {
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            if (System.currentTimeMillis() <= otpExpiry.get(email)) {
                otpStorage.remove(email);
                otpExpiry.remove(email);
                return true;
            }
        }
        return false;
    }
}
