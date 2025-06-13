package com.nhom13.learningenglishapp.activity.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer; // Thêm import này
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.ChatbotActivity;
import com.nhom13.learningenglishapp.activity.LoginActivity;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.User;

public class UserHomePageActivity extends AppCompatActivity {

    ImageButton btnVideoTiengAnh;
    ImageButton btnChoiGame;
    ImageButton btnTiengAnh;
    ImageButton btnChatbot;
    String username;
    ImageButton Setting;
    TextView UserTextView, ScoreTextView;
    int score;

    private UserDao userDao;
    private MediaPlayer mediaPlayer; // Khai báo MediaPlayer

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);

        userDao = new UserDao(this);

        // Ánh xạ các View (giữ nguyên)
        btnVideoTiengAnh = findViewById(R.id.img_amnhac);
        btnChoiGame = findViewById(R.id.img_kiemtra);
        btnTiengAnh = findViewById(R.id.img_tudien);
        btnChatbot = findViewById(R.id.img_chatbot);
        Setting = findViewById(R.id.btnsetting);
        UserTextView = findViewById(R.id.tvUser);
        ScoreTextView = findViewById(R.id.tvScore);

        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
            UserTextView.setText(username);
        } else {
            // Xử lý nếu không có username (giữ nguyên)
            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // --- PHẦN CODE PHÁT NHẠC ĐƠN GIẢN ---
        try {
            // 1. Tạo MediaPlayer với file nhạc từ thư mục res/raw
            //    Hãy thay R.raw.background_music bằng tên file nhạc của bạn
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music);

            if (mediaPlayer != null) {
                // 2. Đặt cho nhạc lặp lại vô hạn
                mediaPlayer.setLooping(true);
                // 3. (Tùy chọn) Đặt âm lượng (từ 0.0f đến 1.0f)
                mediaPlayer.setVolume(0.5f, 0.5f);
                // 4. Bắt đầu phát nhạc
                //    Chúng ta sẽ bắt đầu trong onResume() để đảm bảo Activity hiển thị
            } else {
                Log.e("UserHomePageActivity", "Không thể tạo MediaPlayer, kiểm tra lại file nhạc.");
            }
        } catch (Exception e) {
            Log.e("UserHomePageActivity", "Lỗi khi khởi tạo MediaPlayer: " + e.getMessage());
        }
        // --- KẾT THÚC PHẦN PHÁT NHẠC ---

        // Các OnClickListener (giữ nguyên)
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
                intent.putExtra("score", score);
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

        if (btnChatbot != null) {
            btnChatbot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("UserHomePageActivity", "Chatbot button clicked");
                    Intent intent = new Intent(UserHomePageActivity.this, ChatbotActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("UserHomePageActivity", "btnChatbot is null. Check layout ID 'img_chatbot'.");
        }

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
        // Cập nhật điểm (giữ nguyên)
        if (username != null && !username.isEmpty()) {
            User user = userDao.getUserByUsername(username);
            if (user != null) {
                score = user.getScore();
                ScoreTextView.setText(String.valueOf(score));
            } else {
                Toast.makeText(this, "Tài khoản không tồn tại. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
                return;
            }
        }

        // 5. Bắt đầu phát nhạc khi Activity hiển thị (hoặc quay trở lại)
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();
            } catch (IllegalStateException e) {
                Log.e("UserHomePageActivity", "Lỗi khi bắt đầu nhạc trong onResume: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 6. Tạm dừng nhạc khi Activity không còn là Activity chính trên màn hình
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
            } catch (IllegalStateException e) {
                Log.e("UserHomePageActivity", "Lỗi khi tạm dừng nhạc: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 7. Giải phóng MediaPlayer khi Activity bị hủy hoàn toàn để tránh rò rỉ tài nguyên
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop(); // Dừng hẳn trước khi giải phóng
                }
                mediaPlayer.release(); // Giải phóng tài nguyên
            } catch (IllegalStateException e) {
                Log.e("UserHomePageActivity", "Lỗi khi giải phóng MediaPlayer: " + e.getMessage());
            }
            mediaPlayer = null; // Đặt về null
        }
    }

    // Phương thức ShowSettingDialog (giữ nguyên)
    public void ShowSettingDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_setting, null);
        builder.setView(dialogView);

        Button logoutButton = dialogView.findViewById(R.id.igbLogOut);
        SeekBar skbarVolume = dialogView.findViewById(R.id.skbarVolume);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);;

        AlertDialog dialog = builder.create();
        dialog.show();

        logoutButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Dừng và giải phóng nhạc trước khi đăng xuất (quan trọng)
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                } catch (IllegalStateException e) {
                    Log.e("UserHomePageActivity", "Lỗi khi giải phóng MediaPlayer lúc logout: " + e.getMessage());
                }
                mediaPlayer = null;
            }

            Intent intent = new Intent(UserHomePageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity();
            Toast.makeText(UserHomePageActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        skbarVolume.setMax(maxVolume);
        skbarVolume.setProgress(currentVolume);

        // Lắng nghe khi người dùng thay đổi SeekBar
        skbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}