package com.nhom13.learningenglishapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText UserNameRegister;
    private EditText PasswordRegister;
    private Button RegisterButton;
    private EditText ConfirmPasswordRegister;
    private TextView LoginTextView;
    private UserDao ud;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ud = new UserDao(this);
        UserNameRegister = findViewById(R.id.editTextUsername);
        PasswordRegister = findViewById(R.id.editTextPassword);
        ConfirmPasswordRegister = findViewById(R.id.editTextConfirmPassword);

        RegisterButton = findViewById(R.id.buttonRegister);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = UserNameRegister.getText().toString();
                String password = PasswordRegister.getText().toString();
                String confirmPassword = ConfirmPasswordRegister.getText().toString();
                if (!password.equals(confirmPassword)) {
                    System.out.println("Mật khẩu không khớp");
                    PasswordRegister.setError("Mật khẩu không khớp");
                }
                else{
                    if(checkUsername(email,ud)==false){
                        System.out.println("Tài khoản đã tồn tại");
                        UserNameRegister.setError("Tài khoản đã tồn tại");
                    }
                    else{
                        ud.createUser(new User(email, password));
                        System.out.println("Đăng ký thành công");
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        ChangeToNextActivity();
                    }

                }
            }
        });

        LoginTextView = findViewById(R.id.textViewLogin);
        LoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean checkUsername(String username, UserDao userDao) {
        User user = userDao.getUserByUsername(username);
        if(user == null){
            return true;
        }
        return false;
    }

    private void ChangeToNextActivity(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
