package com.example.slgrocery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slgrocery.Models.User;
import com.example.slgrocery.Utils.AccountValidation;
import com.example.slgrocery.Utils.Dialog;
import com.example.slgrocery.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    /** Tạo một đối tượng ActivitySignUpBinding từ file layout XML và kết nối nó với Activity hiện tại */
    ActivitySignUpBinding activitySignUpBinding;
    LoginActivity loginActivity;
    DbHelper dbHelper;
    private String createUserResponse = "hi";

    private BroadcastReceiver SignUpBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            createUserResponse = intent.getStringExtra("create_user");
            // Xử lý kết quả mua hàng nhận được (ví dụ: cập nhật giao diện, hiển thị thông báo)
            Toast.makeText(loginActivity.getAppContext(), createUserResponse, Toast.LENGTH_SHORT).show();
            Log.i("createUserResponse1", createUserResponse);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(activitySignUpBinding.getRoot());
        IntentFilter filter = new IntentFilter("com.example.createuser_result");
        registerReceiver(SignUpBroadcastReceiver, filter);
        init();
    }

    @Override
    public void onBackPressed() {
    }

    private void init() {
        dbHelper = new DbHelper(getApplicationContext());
        activitySignUpBinding.signUpLoginBtn.setOnClickListener(this);
        activitySignUpBinding.signUpSignUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // when the login button is clicked
        if (v.getId() == activitySignUpBinding.signUpSignUpBtn.getId()) {
            EditText usernameInputController = activitySignUpBinding.signUpInputUsername;
            String username = usernameInputController.getText().toString();
            EditText emailInputController = activitySignUpBinding.signUpInputEmail;
            String email = emailInputController.getText().toString();
            EditText passwordInputController = activitySignUpBinding.signUpInputPassword;
            String password = passwordInputController.getText().toString();
            EditText confirmPasswordController = activitySignUpBinding.signUpInputConfirmPassword;
            String confirmPassword = confirmPasswordController.getText().toString();
            // reset errors
            /** Khi người dùng nhấn nút "Đăng ký", đoạn code bắt đầu quá trình xác thực dữ liệu đầu vào.
             Các dòng lệnh setError(null) được sử dụng để xóa bất kỳ thông báo lỗi nào từ lần xác thực trước đó, đảm bảo giao diện và trải nghiệm người dùng được sạch sẽ và trực quan.
             Nếu không xóa lỗi cũ, các thông báo lỗi từ lần xác thực trước có thể vẫn hiển thị, gây nhầm lẫn hoặc khó chịu cho người dùng.*/
            usernameInputController.setError(null);
            emailInputController.setError(null);
            passwordInputController.setError(null);
            confirmPasswordController.setError(null);
            // Validate input
            AccountValidation accountValidation = new AccountValidation(username, email, password);
            boolean isUsernameValid = accountValidation.usernameValidation();
            if (!isUsernameValid) {
                usernameInputController.setError(accountValidation.errorMessage);
            }
            boolean isEmailValid = accountValidation.emailValidation();
            if (!isEmailValid) {
                emailInputController.setError(accountValidation.errorMessage);
            }
            boolean isPasswordValid = accountValidation.passwordValidation();
            if (!isPasswordValid) {
                passwordInputController.setError(accountValidation.errorMessage);
            }
            boolean isPasswordSame = confirmPassword.equals(password);
            if (!isPasswordSame) {
                confirmPasswordController.setError("Confirm password is different than the password");
            }
            if (isUsernameValid && isPasswordValid && isEmailValid && isPasswordSame) {

                User user = new User();
                user.email = email;
                user.username = username;
                user.password = password;
                String createUserResponse = dbHelper.createUser(user);

//                Intent intentService = new Intent(this, MyDatabaseService.class);
//                intentService.putExtra("operation", "create_User");
//                intentService.putExtra("email", email);
//                intentService.putExtra("username", username);
//                intentService.putExtra("password", password);
//                // Sử dụng ngữ cảnh của Activity để khởi động Service
//                startService(intentService);

                Log.i("createUserResponse2", createUserResponse);
                if (Objects.equals(createUserResponse, "DONE")) {
                    Toast.makeText(getApplicationContext(), "Create User Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
                    Dialog dialog = new Dialog("Create User Failed", createUserResponse, "Try Again");
                    dialog.show(getSupportFragmentManager(), "signUpFailed");
                }

            }
        }
        // when the login button is clicked
        else if (v.getId() == activitySignUpBinding.signUpLoginBtn.getId()) {
            Intent intent = new Intent(this, LoginActivity.class);
            /**Thêm cờ FLAG_ACTIVITY_NEW_TASK: Dòng này thêm một cờ đặc biệt cho Intent.
             Cờ FLAG_ACTIVITY_NEW_TASK chỉ định rằng Activity mới sẽ được khởi chạy trong một tác vụ (task) mới.
             Điều này có nghĩa là nếu người dùng nhấn nút "Quay lại" trong màn hình đăng nhập, họ sẽ thoát khỏi ứng dụng hoàn toàn, thay vì quay lại màn hình đăng ký.*/
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(SignUpBroadcastReceiver);
    }
}