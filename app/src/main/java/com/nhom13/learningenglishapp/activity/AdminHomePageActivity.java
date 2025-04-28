package com.nhom13.learningenglishapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;

public class AdminHomePageActivity extends AppCompatActivity {

    private LinearLayout itemQlTu;
    private LinearLayout itemQlUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_quanly);

        // Ánh xạ các view
        itemQlTu = findViewById(R.id.itemQlTu);
        itemQlUser = findViewById(R.id.itemQlUser);

        // Thiết lập sự kiện click cho các mục quản lý
        itemQlTu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageWordActivity.class);
            startActivity(intent);
        });

        itemQlUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageUserActivity.class);
            startActivity(intent);
        });
    }
}
