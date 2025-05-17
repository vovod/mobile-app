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
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.User;

public class UserHomePageActivity extends AppCompatActivity {

    ImageButton btnVideoTiengAnh;
    ImageButton btnChoiGame;
    ImageButton btnTiengAnh;
    String username;
    ImageButton Setting;
    TextView UserTextView, ScoreTextView;
    int score;

    private UserDao userDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);


        userDao = new UserDao(this);


        btnVideoTiengAnh = findViewById(R.id.img_amnhac);
        btnChoiGame = findViewById(R.id.img_kiemtra);
        btnTiengAnh = findViewById(R.id.img_tudien);
        Setting = findViewById(R.id.btnsetting);
        UserTextView = findViewById(R.id.tvUser);
        ScoreTextView = findViewById(R.id.tvScore);


        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");

            UserTextView.setText(username);
        } else {

            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }



        btnVideoTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to videoTiengAnh");

                Intent intent = new Intent(UserHomePageActivity.this, VideoListActivity.class);
                intent.putExtra("username", username);

                startActivity(intent);
            }
        });

        btnChoiGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to choiGame");

                Intent intent = new Intent(UserHomePageActivity.this, GameActivity.class);
                intent.putExtra("username", username);

                startActivity(intent);
            }
        });

        btnTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to tiengAnh");

                Intent intent = new Intent(UserHomePageActivity.this, DictionaryActivity.class);
                intent.putExtra("username", username);

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

        if (username != null && !username.isEmpty()) {
            User user = userDao.getUserByUsername(username);
            if (user != null) {
                score = user.getScore();
                ScoreTextView.setText(String.valueOf(score));
                Log.d("UserHomePage", "Score refreshed in onResume: " + score);
            } else {

                Toast.makeText(this, "Tài khoản không tồn tại. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void ShowSettingDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_setting, null);
        builder.setView(dialogView);


        Button logoutButton = dialogView.findViewById(R.id.igbLogOut);


        AlertDialog dialog = builder.create();
        dialog.show();


        logoutButton.setOnClickListener(v -> {
            dialog.dismiss();

            Intent intent = new Intent(UserHomePageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity();
            Toast.makeText(UserHomePageActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

}
