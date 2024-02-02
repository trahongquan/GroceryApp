package com.example.slgrocery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slgrocery.Utils.AccountValidation;
import com.example.slgrocery.Utils.Dialog;
import com.example.slgrocery.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityLoginBinding activityLoginBinding;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (routeGuard()) {
            activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(activityLoginBinding.getRoot());
            init();
        } else {
            goHomeActivity();
        }
    }

    private void init() {
        dbHelper = new DbHelper(getApplicationContext());
        activityLoginBinding.loginLoginBtn.setOnClickListener(this);
        activityLoginBinding.loginSignUpBtn.setOnClickListener(this);
    }

    // if user login successfully, save the related info into sharePreferences
    private void saveSession(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.session_key, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String username = dbHelper.getUsernameByEmail(email);
        editor.putString(Settings.session_username_key, username);
        editor.apply();
    }

    // if the session can be get from sharePreferences, go to the home page
    private boolean routeGuard() {
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.session_key, MODE_PRIVATE);
        return sharedPreferences.getString(Settings.session_username_key, null) == null;
    }

    private void goHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        // when login button is clicked
        if (v.getId() == activityLoginBinding.loginLoginBtn.getId()) {
            EditText emailInputController = activityLoginBinding.loginInputEmail;
            String email = emailInputController.getText().toString();
            EditText passwordInputController = activityLoginBinding.loginInputPassword;
            String password = passwordInputController.getText().toString();
            // reset errors
            emailInputController.setError(null);
            passwordInputController.setError(null);
            // Validate input
            AccountValidation accountValidation = new AccountValidation(email, password);
            boolean isEmailValid = accountValidation.emailValidation();
            if (!isEmailValid) {
                emailInputController.setError(accountValidation.errorMessage);
            }
            boolean isPasswordValid = accountValidation.passwordValidation();
            if (!isPasswordValid) {
                passwordInputController.setError(accountValidation.errorMessage);
            }
            if (isPasswordValid && isEmailValid) {
                // search account is existing or not
                if (dbHelper.loginChecking(email, password)) {
                    saveSession(email); // save email to sharePreferences
                    Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_LONG).show();
                    goHomeActivity();
                } else {
                    Dialog dialog = new Dialog("Login Failed", "Email and Password Are Not Matched");
                    dialog.show(getSupportFragmentManager(), "loginFailed");
                }
            }
        }
        // when sign up button is clicked
        else if (v.getId() == activityLoginBinding.loginSignUpBtn.getId()) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        }
    }
}