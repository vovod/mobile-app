package com.nhom13.learningenglishapp.activity.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Thêm import
import androidx.recyclerview.widget.RecyclerView;       // Thêm import

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.LoginActivity;
import com.nhom13.learningenglishapp.adapters.UserAdapter; // Thêm import
import com.nhom13.learningenglishapp.database.dao.UserDao;   // Thêm import
import com.nhom13.learningenglishapp.database.models.User;  // Thêm import

import java.util.ArrayList; // Thêm import
import java.util.List;      // Thêm import

public class GameActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private ImageButton btnHomeGame, btnSettingGame;
    private LinearLayout itemLeaderboardButtonContainer; // Đổi tên từ itemYesNo
    private LinearLayout itemDoanHinh;
    // private ImageButton igbYesNo; // Đã đổi thành igbShowLeaderboard, không cần biến riêng nếu bắt sự kiện trên LinearLayout
    private ImageButton igbDoanHinh;
    private String username;
    private int score;

    private UserDao userDao; // Thêm UserDao
    private UserAdapter leaderboardDialogAdapter; // Adapter cho dialog

    private static final String TAG = "GameActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        if (getIntent().hasExtra("score")) {
            score = getIntent().getIntExtra("score", 0);
        }

        userDao = new UserDao(this); // Khởi tạo UserDao

        btnHomeGame = findViewById(R.id.btnHomeGame);
        btnSettingGame = findViewById(R.id.btnSettingGame);
        itemLeaderboardButtonContainer = findViewById(R.id.item_leaderboard_button_container); // Ánh xạ ID mới
        itemDoanHinh = findViewById(R.id.item2);
        igbDoanHinh = findViewById(R.id.igbDoanHinh);

        btnHomeGame.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, UserHomePageActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        });

        btnSettingGame.setOnClickListener(v -> {
            showSettingDialog();
        });

        itemLeaderboardButtonContainer.setOnClickListener(v -> { // Bắt sự kiện click trên LinearLayout
            showLeaderboardDialog();
        });

        igbDoanHinh.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, GuessImageActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("score", score);
            startActivity(intent);
        });
    }

    private void showLeaderboardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog); // Sử dụng style tùy chỉnh nếu có
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_leaderboard, null);
        builder.setView(dialogView);

        RecyclerView rcvDialogLeaderboard = dialogView.findViewById(R.id.rcvDialogLeaderboard);
        Button btnCloseDialog = dialogView.findViewById(R.id.btnCloseLeaderboardDialog);

        rcvDialogLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        List<User> topUsers = userDao.getTopUsersByScore(10); // Lấy top 10 người dùng, bạn có thể thay đổi số lượng

        // Khởi tạo adapter cho dialog, truyền 'this' nếu muốn xử lý click item trong dialog
        // hoặc null nếu không cần.
        leaderboardDialogAdapter = new UserAdapter(this, topUsers, null, this);
        rcvDialogLeaderboard.setAdapter(leaderboardDialogAdapter);

        if (topUsers.isEmpty()) {
            Log.d(TAG, "Leaderboard data for dialog is empty.");
            Toast.makeText(this, "Chưa có dữ liệu bảng xếp hạng.", Toast.LENGTH_SHORT).show();
        }

        AlertDialog dialog = builder.create();

        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Không cần load lại BXH ở đây vì nó chỉ hiện khi click
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_setting, null);
        builder.setView(dialogView);

        Button logoutButton = dialogView.findViewById(R.id.igbLogOut);
        AlertDialog dialog = builder.create();
        dialog.show();

        logoutButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(GameActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity();
            Toast.makeText(GameActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onUserClick(User user) {
        // Xử lý khi một item trên BXH trong dialog được click (nếu UserAdapter được truyền listener là 'this')
        // Ví dụ: Toast.makeText(this, "User: " + user.getUsername() + ", Score: " + user.getScore(), Toast.LENGTH_SHORT).show();
        // Bạn có thể không cần làm gì ở đây nếu không muốn có hành động khi click vào item trong dialog.
    }
}