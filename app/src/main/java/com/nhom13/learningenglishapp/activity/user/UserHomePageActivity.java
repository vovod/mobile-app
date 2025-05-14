package com.nhom13.learningenglishapp.activity.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.nhom13.learningenglishapp.database.dao.UserDao; // Import UserDao
import com.nhom13.learningenglishapp.database.models.User; // Import User

public class UserHomePageActivity extends AppCompatActivity {

    ImageButton btnVideoTiengAnh; // Nút Video Tiếng Anh
    ImageButton btnChoiGame;      // Nút Chơi Game
    ImageButton btnTiengAnh;      // Nút Học Tiếng Anh
    String username;
    ImageButton Setting;
    TextView UserTextView, ScoreTextView; // Đổi tên biến để tránh trùng với class User/Score
    int score;

    private UserDao userDao; // Khai báo UserDao

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);

        // Khởi tạo UserDao
        userDao = new UserDao(this);

        // Ánh xạ các nút và view
        btnVideoTiengAnh = findViewById(R.id.img_amnhac); // Nút Video Tiếng Anh
        btnChoiGame = findViewById(R.id.img_kiemtra);    // Nút Chơi Game
        btnTiengAnh = findViewById(R.id.img_tudien);     // Nút Học Tiếng Anh
        Setting = findViewById(R.id.btnsetting);
        UserTextView = findViewById(R.id.tvUser); // Ánh xạ TextView User
        ScoreTextView = findViewById(R.id.tvScore); // Ánh xạ TextView Score

        // Nhận dữ liệu username từ intent (score sẽ được load lại trong onResume)
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
            // Cập nhật TextView User ngay khi có username
            UserTextView.setText(username);
        } else {
            // Xử lý trường hợp không có username (không mong muốn)
            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Thoát khỏi onCreate
        }

        // Các Intent khi chuyển màn hình sẽ chỉ cần truyền username,
        // màn hình đích (GameActivity, DictionaryActivity, VideoListActivity) sẽ tự load score
        // (Hoặc tiếp tục truyền score nếu luồng logic của bạn yêu cầu)
        // Ở đây, khi trở về UserHomePageActivity, chúng ta sẽ load lại score mới nhất.

        btnVideoTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to videoTiengAnh");
                // Chuyển đến VideoListActivity
                Intent intent = new Intent(UserHomePageActivity.this, VideoListActivity.class);
                intent.putExtra("username", username); // Chỉ truyền username
                // VideoListActivity sẽ tự lấy score nếu cần
                startActivity(intent);
            }
        });

        btnChoiGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to choiGame");
                // Chuyển đến GameActivity
                Intent intent = new Intent(UserHomePageActivity.this, GameActivity.class);
                intent.putExtra("username", username); // Chỉ truyền username
                // GameActivity sẽ tự lấy score nếu cần
                startActivity(intent);
            }
        });

        btnTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to tiengAnh");
                // Chuyển đến DictionaryActivity
                Intent intent = new Intent(UserHomePageActivity.this, DictionaryActivity.class);
                intent.putExtra("username", username); // Chỉ truyền username
                // DictionaryActivity sẽ tự lấy score nếu cần
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

    @Override
    protected void onResume() {
        super.onResume();
        // Đọc lại thông tin người dùng (bao gồm score) từ database mỗi khi Activity này hiển thị lại
        if (username != null && !username.isEmpty()) {
            User user = userDao.getUserByUsername(username);
            if (user != null) {
                score = user.getScore(); // Cập nhật biến score của Activity
                ScoreTextView.setText(String.valueOf(score)); // Cập nhật TextView hiển thị điểm
                Log.d("UserHomePage", "Score refreshed in onResume: " + score);
            } else {
                // Xử lý trường hợp user không còn tồn tại (ví dụ: bị admin xóa)
                Toast.makeText(this, "Tài khoản không tồn tại. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish(); // Kết thúc Activity hiện tại
            }
        }
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa tất cả activity trước đó
            startActivity(intent);
            finishAffinity(); // Kết thúc tất cả activity trong task hiện tại
            Toast.makeText(UserHomePageActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

    // Có thể bỏ phương thức onBackPressed() mặc định nếu không cần xử lý đặc biệt
    // @Override
    // public void onBackPressed() {
    //    // Xử lý nút Back ở đây nếu cần (ví dụ: hiển thị dialog xác nhận thoát)
    //    super.onBackPressed();
    // }
}
