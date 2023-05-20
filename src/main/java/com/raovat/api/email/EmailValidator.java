package com.raovat.api.email;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

    private static final String EMAIL_NOT_VALID = "Email not valid";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Override
    public boolean test(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalStateException(EMAIL_NOT_VALID);
        }
        return true;
    }
}
