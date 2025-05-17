package com.nhom13.learningenglishapp.activity;

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

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.admin.AdminHomePageActivity;
import com.nhom13.learningenglishapp.activity.user.UserHomePageActivity;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.User;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    UserDao ud ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ud = new UserDao(this);
        usernameEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                boolean isLogin = checkLogin(username, password);
                if(isLogin == true){
                    if(checkAdmin(username,password)==true){
                        GoToNextAdminActivity();
                    }
                    else{
                        User user = ud.getUserByUsername(username);
                        GoToNextUserActivity(username, user.getScore());
                    }
                }
            }
        });

        registerTextView = findViewById(R.id.textViewRegister);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        User user =ud.getUserByUsername(usernameInput);

        if (user == null) {

            System.out.println("Username không tồn tại");
            Toast.makeText(this, "Username không tồn tại", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (user.getPassword().equals(passwordInput)) {

                System.out.println("Đăng nhập thành công");
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                return true;
            } else {

                System.out.println("Sai mật khẩu");
                Toast.makeText(this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private boolean checkAdmin(String username, String password){
        if(username.equals("admin") && password.equals("admin")){
            return true;
        }
        return false;
    }
    private void GoToNextUserActivity(String username, int score) {

        Intent intent = new Intent(LoginActivity.this, UserHomePageActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    private void GoToNextAdminActivity() {

        Intent intent = new Intent(LoginActivity.this, AdminHomePageActivity.class);
        startActivity(intent);
        finish();
        System.out.println("Admin");
    }
}
