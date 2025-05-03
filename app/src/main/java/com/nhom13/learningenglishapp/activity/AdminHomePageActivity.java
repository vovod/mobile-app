package com.nhom13.learningenglishapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;

public class AdminHomePageActivity extends AppCompatActivity {

    private ImageButton igbQlTu;
    private ImageButton igbQlUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        // Ánh xạ các view
        igbQlTu = findViewById(R.id.igbQLTuVung);
        igbQlUser = findViewById(R.id.igbQuanLyUser);

        // Thiết lập sự kiện click cho các mục quản lý
        igbQlTu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageWordActivity.class);
            startActivity(intent);
        });

        igbQlUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageUserActivity.class);
            startActivity(intent);
        });
    }
}
