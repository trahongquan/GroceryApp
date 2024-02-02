package com.example.slgrocery.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountValidation {
    private final String password;
    private final String email;
    private final int minimalPasswordLength = 4;
    public String errorMessage;
    private String username;

    public AccountValidation(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public AccountValidation(String email, String password) {
        this.password = password;
        this.email = email;
    }

    public boolean usernameValidation() {
        errorMessage = null;
        if (username.trim().isEmpty()) {
            errorMessage = "Username should not be empty";
            return false;
        }
        return true;
    }

    public boolean passwordValidation() {
        errorMessage = null;
        if (password.trim().isEmpty()) {
            errorMessage = "Password should not be empty";
            return false;
        } else if (password.trim().length() < minimalPasswordLength) {
            errorMessage = "The length of password should greater than" + minimalPasswordLength;
            return false;
        }
        return true;
    }

    public boolean emailValidation() {
        errorMessage = null;
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            errorMessage = "Email Format is incorrect";
            return false;
        }
        return true;
    }
}
