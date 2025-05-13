package com.nhom13.learningenglishapp.activity.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.LoginActivity;

public class UserHomePageActivity extends AppCompatActivity {

    ImageButton btnVideoTiengAnh; // Nút Video Tiếng Anh
    ImageButton btnChoiGame;      // Nút Chơi Game
    ImageButton btnTiengAnh;      // Nút Học Tiếng Anh
    String username;
    ImageButton Setting;
    TextView User, Score;
    int score;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);

        // Ánh xạ các nút và view
        btnVideoTiengAnh = findViewById(R.id.img_amnhac); // Nút Video Tiếng Anh
        btnChoiGame = findViewById(R.id.img_kiemtra);    // Nút Chơi Game
        btnTiengAnh = findViewById(R.id.img_tudien);     // Nút Học Tiếng Anh
        Setting = findViewById(R.id.btnsetting);
        User = findViewById(R.id.tvUser);
        Score = findViewById(R.id.tvScore);

        // Nhận dữ liệu từ intent
        if (getIntent().hasExtra("username")) {
            System.out.println("Username: " + getIntent().getStringExtra("username"));
            username = getIntent().getStringExtra("username");
            // Cập nhật TextView User
            User.setText(username);
        }
        if (getIntent().hasExtra("score")) {
            System.out.println("Score: " + getIntent().getIntExtra("score", 0));
            score = getIntent().getIntExtra("score", 0);
            // Cập nhật TextView Score
            Score.setText(String.valueOf(score));
        }

        btnVideoTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to videoTiengAnh");
                // Chuyển đến VideoListActivity
                Intent intent = new Intent(UserHomePageActivity.this, VideoListActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("score", score);
                startActivity(intent);
            }
        });

        btnChoiGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to choiGame");
                // Chuyển đến GameActivity
                Intent intent = new Intent(UserHomePageActivity.this, GameActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("score", score);
                startActivity(intent);
            }
        });

        btnTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to tiengAnh");
                // Chuyển đến DictionaryActivity
                Intent intent = new Intent(UserHomePageActivity.this, DictionaryActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("score", score);
                startActivity(intent);
            }
        });
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to Setting");
                ShowSettingDialog(view);
            }
        });
    }

    public void ShowSettingDialog(View view) {
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
            Intent intent = new Intent(UserHomePageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(UserHomePageActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

}
