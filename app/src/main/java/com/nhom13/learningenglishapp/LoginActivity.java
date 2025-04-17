package com.nhom13.learningenglishapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.User;

public class LoginActivity extends AppCompatActivity {
    boolean isLogin = false;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    UserDao ud ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        ud = new UserDao(this);
        usernameEditText = findViewById(R.id.editTextEmail); // Thay đổi ID nếu bạn đặt khác
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                //Kiểm tra tài khoản mật khẩu
                isLogin = checkLogin(username, password);
                if(isLogin == true){
                    GoToNextActivity();
                }
            }
        });

        registerTextView = findViewById(R.id.textViewRegister);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển tới RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public boolean checkLogin(String usernameInput, String passwordInput) {
        UserDao userDao = new UserDao(this);
        User user = userDao.getUserByUsername(usernameInput);

        if (user == null) {
            // Username không tồn tại
            System.out.println("Username không tồn tại");
            return false;
        } else {
            if (user.getPassword().equals(passwordInput)) {
                // Đăng nhập thành công
                System.out.println("Đăng nhập thành công");
                return true;
            } else {
                // Sai mật khẩu
                System.out.println("Sai mật khẩu");
                return false;
            }
        }
    }
    private void GoToNextActivity() {
        // Chuyển tới Activity khác
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // đóng LoginActivity nếu muốn
    }
}
