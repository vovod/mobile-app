package com.nhom13.learningenglishapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;

public class GameActivity extends AppCompatActivity {

    private ImageButton btnHomeGame, btnSettingGame;
    private LinearLayout itemYesNo, itemDoanHinh;
    private ImageButton igbYesNo, igbDoanHinh;
    private String username;
    private int score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        // Nhận dữ liệu từ intent
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        if (getIntent().hasExtra("score")) {
            score = getIntent().getIntExtra("score", 0);
        }

        // Ánh xạ các view
        btnHomeGame = findViewById(R.id.btnHomeGame);
        btnSettingGame = findViewById(R.id.btnSettingGame);
        itemYesNo = findViewById(R.id.item1);
        itemDoanHinh = findViewById(R.id.item2);
        igbYesNo = findViewById(R.id.igbYesNo);
        igbDoanHinh = findViewById(R.id.igbDoanHinh);

        // Thiết lập sự kiện click cho nút Home
        btnHomeGame.setOnClickListener(v -> {
            // Quay về màn hình chính
            Intent intent = new Intent(GameActivity.this, UserHomePageActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        });

        // Thiết lập sự kiện click cho nút Setting
        btnSettingGame.setOnClickListener(v -> {
            showSettingDialog();
        });

        // Thiết lập sự kiện click cho game Đúng/Sai
        igbYesNo.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Đúng/Sai đang được phát triển", Toast.LENGTH_SHORT).show();
            // Ở đây bạn có thể thêm code để chuyển đến màn hình game Đúng/Sai
        });

        // Thiết lập sự kiện click cho game Đoán Hình
        igbDoanHinh.setOnClickListener(v -> {
            // Chuyển đến màn hình game Đoán Hình
            Intent intent = new Intent(GameActivity.this, GuessImageActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("score", score);
            startActivity(intent);
        });
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_setting, null);
        builder.setView(dialogView);

        // Khởi tạo nút đăng xuất
        Button logoutButton = dialogView.findViewById(R.id.igbLogOut);

        // Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý sự kiện click nút đăng xuất
        logoutButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(GameActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(GameActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }
}
