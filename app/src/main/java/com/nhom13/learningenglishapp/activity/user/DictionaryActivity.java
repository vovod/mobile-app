package com.nhom13.learningenglishapp.activity.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
import com.nhom13.learningenglishapp.activity.LoginActivity;

import java.io.IOException;
import java.io.InputStream;
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


        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        if (getIntent().hasExtra("score")) {
            score = getIntent().getIntExtra("score", 0);
        }


        vocabularyDao = new VocabularyDao(this);


        vocabularyList = new ArrayList<>();


        recyclerView = findViewById(R.id.rcv_alphabet);
        btnHome = findViewById(R.id.btnhomedict);
        btnSetting = findViewById(R.id.btnsetting);
        imgWordView = findViewById(R.id.imgWordView);
        tvNameColor = findViewById(R.id.tv_namecolor);
        animBalloon = findViewById(R.id.anim_ballon);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        alphabetAdapter = new AlphabetAdapter(this, vocabularyList, this);
        recyclerView.setAdapter(alphabetAdapter);


        loadVocabularyData();


        btnHome.setOnClickListener(v -> {

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


        if (!vocabularyList.isEmpty()) {
            Vocabulary firstVocab = vocabularyList.get(0);
            displayVocabulary(firstVocab);
            if (firstVocab != null) {
                vocabularyDao.incrementViewCount(firstVocab.getId());
                Log.d("DictionaryActivity", "Incremented view count for first vocab: " + firstVocab.getWord());
            }
        }
    }

    @Override
    public void onVocabularyClick(Vocabulary vocabulary) {

        displayVocabulary(vocabulary);

        if (vocabulary != null) {
            vocabularyDao.incrementViewCount(vocabulary.getId());
            Log.d("DictionaryActivity", "Incremented view count for clicked vocab: " + vocabulary.getWord());
        }
    }

    private void displayVocabulary(Vocabulary vocabulary) {
        if (vocabulary == null) {
            imgWordView.setImageResource(R.drawable.abc);
            tvNameColor.setText("");
            return;
        }

        if (vocabulary.getImagePath() != null && !vocabulary.getImagePath().isEmpty()) {
            InputStream inputStream = null;
            try {

                if (!vocabulary.getImagePath().startsWith("/")) {
                    inputStream = getAssets().open(vocabulary.getImagePath());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgWordView.setImageBitmap(bitmap);
                } else {
                    try {
                        inputStream = getAssets().open(vocabulary.getImagePath());
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgWordView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.w("DictionaryActivity", "Image not found in assets, trying as file path: " + vocabulary.getImagePath());
                        Bitmap bitmap = BitmapFactory.decodeFile(vocabulary.getImagePath());
                        if (bitmap != null) {
                            imgWordView.setImageBitmap(bitmap);
                        } else {
                            Log.e("DictionaryActivity", "Error loading image as file: " + vocabulary.getImagePath());
                            imgWordView.setImageResource(R.drawable.abc);
                        }
                    }
                }

            } catch (IOException e) {
                Log.e("DictionaryActivity", "Error loading image: " + vocabulary.getImagePath(), e);
                imgWordView.setImageResource(R.drawable.abc); // Ảnh mặc định nếu lỗi
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
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

        Button logoutButton = dialogView.findViewById(R.id.igbLogOut);

        AlertDialog dialog = builder.create();
        dialog.show();

        logoutButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(DictionaryActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity();
            Toast.makeText(DictionaryActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }
}