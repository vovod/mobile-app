package com.nhom13.learningenglishapp.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.LoginActivity;

public class AdminHomePageActivity extends AppCompatActivity {

    private ImageButton igbQlTu;
    private ImageButton igbQlUser;
    private ImageButton igbQlQuiz;
    private ImageButton igbQlVideo;
    private ImageButton btnAddLoai;
    private ImageButton igbStatistics;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        // Ánh xạ các view
        igbQlTu = findViewById(R.id.igbQLTuVung);
        igbQlUser = findViewById(R.id.igbQuanLyUser);
        igbQlQuiz = findViewById(R.id.igbQuanLyQuiz);
        igbQlVideo = findViewById(R.id.igbQuanLyVideo);
        btnAddLoai = findViewById(R.id.btnAddLoai);
        igbStatistics = findViewById(R.id.igbStatistics);
        btnBack = findViewById(R.id.btnBack);


        igbQlTu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageWordActivity.class);
            startActivity(intent);
        });


        igbQlUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageUserActivity.class);
            startActivity(intent);
        });


        igbQlQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageQuizActivity.class);
            startActivity(intent);
        });


        igbQlVideo.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageVideoActivity.class);
            startActivity(intent);
        });


        btnAddLoai.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageChapterActivity.class);
            startActivity(intent);
        });


        igbStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, AdminStatisticsActivity.class);
            startActivity(intent);
        });


        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}