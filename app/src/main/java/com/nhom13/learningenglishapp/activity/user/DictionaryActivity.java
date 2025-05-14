package com.nhom13.learningenglishapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap; // Thêm dòng này
import android.graphics.BitmapFactory; // Thêm dòng này
import android.os.Bundle;
import android.util.Log; // Thêm dòng này
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.adapters.AlphabetAdapter;
import com.nhom13.learningenglishapp.database.dao.VocabularyDao;
import com.nhom13.learningenglishapp.database.models.Vocabulary;

import java.io.IOException; // Thêm dòng này
import java.io.InputStream; // Thêm dòng này
import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity implements AlphabetAdapter.OnVocabularyClickListener {

    private RecyclerView recyclerView;
    private List<Vocabulary> vocabularyList;
    private VocabularyDao vocabularyDao;
    private AlphabetAdapter alphabetAdapter;
    private ImageButton btnHome, btnSetting;
    private ImageView imgWordView;
    private TextView tvNameColor;
    private LottieAnimationView animBalloon;
    private String username;
    private int score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dictionary);

        // Nhận dữ liệu từ intent
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        if (getIntent().hasExtra("score")) {
            score = getIntent().getIntExtra("score", 0);
        }

        // Khởi tạo DAO
        vocabularyDao = new VocabularyDao(this);

        // Khởi tạo danh sách từ vựng
        vocabularyList = new ArrayList<>();

        // Ánh xạ các view
        recyclerView = findViewById(R.id.rcv_alphabet);
        btnHome = findViewById(R.id.btnhomedict);
        btnSetting = findViewById(R.id.btnsetting);
        imgWordView = findViewById(R.id.imgWordView);
        tvNameColor = findViewById(R.id.tv_namecolor);
        animBalloon = findViewById(R.id.anim_ballon);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        alphabetAdapter = new AlphabetAdapter(this, vocabularyList, this);
        recyclerView.setAdapter(alphabetAdapter);

        // Tải dữ liệu từ vựng
        loadVocabularyData();

        // Thiết lập sự kiện click cho các nút
        btnHome.setOnClickListener(v -> {
            // Quay về màn hình chính
            Intent intent = new Intent(DictionaryActivity.this, UserHomePageActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        });

        btnSetting.setOnClickListener(v -> {
            showSettingDialog();
        });
    }

    private void loadVocabularyData() {
        vocabularyList.clear();
        vocabularyList.addAll(vocabularyDao.getAllVocabulary());
        alphabetAdapter.notifyDataSetChanged();

        // Hiển thị từ vựng đầu tiên nếu có
        if (!vocabularyList.isEmpty()) {
            displayVocabulary(vocabularyList.get(0));
        }
    }

    @Override
    public void onVocabularyClick(Vocabulary vocabulary) {
        // Hiển thị từ vựng được chọn
        displayVocabulary(vocabulary);
    }

    // Trong DictionaryActivity.java
    private void displayVocabulary(Vocabulary vocabulary) {
        if (vocabulary.getImagePath() != null && !vocabulary.getImagePath().isEmpty()) {
            InputStream inputStream = null; // Khởi tạo để có thể đóng trong finally
            try {
                // Giả sử vocabulary.getImagePath() là "images/alphabet/a.png"
                inputStream = getAssets().open(vocabulary.getImagePath());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgWordView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("DictionaryActivity", "Error loading image from assets: " + vocabulary.getImagePath(), e);
                imgWordView.setImageResource(R.drawable.abc); // Ảnh mặc định nếu lỗi
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close(); // Luôn đóng InputStream
                    } catch (IOException e) {
                        Log.e("DictionaryActivity", "Error closing InputStream", e);
                    }
                }
            }
        } else {
            imgWordView.setImageResource(R.drawable.abc);
        }
        tvNameColor.setText(vocabulary.getWord());
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
            Intent intent = new Intent(DictionaryActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(DictionaryActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }
}
