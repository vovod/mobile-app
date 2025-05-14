package com.nhom13.learningenglishapp.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.LoginActivity; // Import LoginActivity

public class AdminHomePageActivity extends AppCompatActivity {

    private ImageButton igbQlTu;
    private ImageButton igbQlUser;
    private ImageButton igbQlQuiz;
    private ImageButton igbQlVideo;
    private ImageButton btnAddLoai; // Quản lý Chapter
    private ImageButton igbStatistics; // Khai báo nút Thống kê
    private ImageButton btnBack; // Khai báo nút Back

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        // Ánh xạ các view
        igbQlTu = findViewById(R.id.igbQLTuVung);
        igbQlUser = findViewById(R.id.igbQuanLyUser);
        igbQlQuiz = findViewById(R.id.igbQuanLyQuiz);
        igbQlVideo = findViewById(R.id.igbQuanLyVideo);
        btnAddLoai = findViewById(R.id.btnAddLoai); // Ánh xạ nút Quản lý Chapter
        igbStatistics = findViewById(R.id.igbStatistics); // Ánh xạ nút Thống kê từ layout
        btnBack = findViewById(R.id.btnBack); // Ánh xạ nút Back từ layout


        // Thiết lập sự kiện click cho các mục quản lý

        // Quản lý Từ vựng
        igbQlTu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageWordActivity.class);
            startActivity(intent);
        });

        // Quản lý Người dùng
        igbQlUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageUserActivity.class);
            startActivity(intent);
        });

        // Quản lý Quiz
        igbQlQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageQuizActivity.class);
            startActivity(intent);
        });

        // Quản lý Video
        igbQlVideo.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageVideoActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện click cho nút Quản lý Chapter
        btnAddLoai.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, ManageChapterActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện click cho nút Thống kê (igbStatistics)
        igbStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, AdminStatisticsActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện click cho nút Back (quay về Login)
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomePageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Kết thúc AdminHomePageActivity để không quay lại được từ Login
        });
    }
}