package com.example.slgrocery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slgrocery.Utils.AccountValidation;
import com.example.slgrocery.Utils.Dialog;
import com.example.slgrocery.databinding.ActivityLoginBinding;
import com.example.slgrocery.setting.SettingBiometric;
import com.example.slgrocery.setting.Settings;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;
    ActivityLoginBinding activityLoginBinding;
    DbHelper dbHelper;
    public Context getAppContext() {
        return getApplicationContext(); // Trả về ngữ cảnh ứng dụng của Activity
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (routeGuard()) { /** routeGuard() Kiểm tra đăng nhập*/
        /** Nếu chưa đăng nhập */
            activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(activityLoginBinding.getRoot());
            init();
            if(isBiometricAuthenticationAvailable()){activityLoginBinding.loginTouchid.setVisibility(View.VISIBLE);
            }else {activityLoginBinding.loginTouchid.setVisibility(View.INVISIBLE);}
        } else {
            if (isBiometricAuthenticationAvailable() && isBiometricLoginSaved()) {
                setupBiometricAuthentication();
                /** nếu máy có vân tay và đã đăng nhập thì bật giao diện quét vân tay */
            } else {
                goHomeActivity(); /** Tạm thời: đối với máy ko có vân tay sẽ vào luôn màn hình chính */
            }
        }
    }

    private void init() {
        dbHelper = new DbHelper(getApplicationContext());
        activityLoginBinding.loginLoginBtn.setOnClickListener(this);
        activityLoginBinding.loginSignUpBtn.setOnClickListener(this);
        activityLoginBinding.loginTouchid.setOnClickListener(this);
    }

    /** Lưu thông tin của người dùng đăngg nập thành công vào sharePreferences*/
    private void saveSession(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.session_key, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String username = dbHelper.getUsernameByEmail(email);
        editor.putString(Settings.session_username_key, username);
        editor.apply();
    }

    /** Nếu sharePreferences có thông tin đăng nhập sẵn thì return False chuyển đến Home*/
    private boolean routeGuard() {
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.session_key, MODE_PRIVATE);
//        return sharedPreferences.getString(Settings.session_username_key, null) == null;
        String s = sharedPreferences.getString(Settings.session_username_key, null);
        return s == null;
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
        if (v.getId() == activityLoginBinding.loginSignUpBtn.getId()) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == activityLoginBinding.loginLoginBtn.getId()) {
            // Kiểm tra xem có hỗ trợ xác thực sinh trắc học hay không
            if (isBiometricAuthenticationAvailable() && isBiometricLoginSaved()) {
                // Hiển thị giao diện xác thực sinh trắc học
//                showBiometricPrompt();
                setupBiometricAuthentication();
            } else {
                // Tiếp tục xử lý đăng nhập bằng email và mật khẩu
                performEmailPasswordLogin();
            }
        }
        else if (v.getId() == activityLoginBinding.loginTouchid.getId()) {
            if(isBiometricLoginSaved()){
                setupBiometricAuthentication();
            } else {
                Dialog dialog = new Dialog("Login Failed", "You have not logged in for the first time");
                dialog.show(getSupportFragmentManager(), "loginFailed");
            }
        }
    }
    private void goBackToLogin() {
//        new Thread(()->{
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            Log.i("RunnableLoginActivity", "run: LoginActivity.class");
//            startActivity(intent);
//        }).start();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                Log.i("RunnableLoginActivity", "run: LoginActivity.class");
                startActivity(intent);
            }
        });
    }
    /** Đăng nhập bằng vân tay*/
    private boolean isBiometricLoginSaved() {
        SharedPreferences sharedPreferences = getSharedPreferences(SettingBiometric.session_key, MODE_PRIVATE);
        return sharedPreferences.getBoolean(SettingBiometric.biometric_login_key, false);
    }
    private void saveBiometricLogin(boolean isBiometricLogin) {
        SharedPreferences sharedPreferences = getSharedPreferences(SettingBiometric.session_key, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SettingBiometric.biometric_login_key, isBiometricLogin);
        editor.apply();
    }
    private boolean isBiometricAuthenticationAvailable() {
        BiometricManager biometricManager = BiometricManager.from(this);
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
    }
    private void performEmailPasswordLogin() {
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
                saveBiometricLogin(true);
                Toast.makeText(getApplicationContext(),"Login Successfully", Toast.LENGTH_LONG).show();
                goHomeActivity();
            } else {
                Dialog dialog = new Dialog("Login Failed", "Email and Password Are Not Matched");
                dialog.show(getSupportFragmentManager(), "loginFailed");
            }
        }
    }
    private void setupBiometricAuthentication() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Xử lý khi xảy ra lỗi xác thực sinh trắc học
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                goBackToLogin();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Xử lý khi xác thực sinh trắc học thành công
                Toast.makeText(getApplicationContext(), "Authentication Login succeeded!", Toast.LENGTH_SHORT).show();
                saveBiometricLogin(true);
                goHomeActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Xử lý khi xác thực sinh trắc học thất bại
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                goBackToLogin();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Log in using your fingerprint or face")
                .setNegativeButtonText("Cancel")
                .build();

        if (isBiometricLoginSaved()) {
            biometricPrompt.authenticate(promptInfo);
        }
    }
//    private void showBiometricPrompt() {
//        biometricPrompt.authenticate(promptInfo);
//    }
}