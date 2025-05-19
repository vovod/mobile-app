package com.nhom13.learningenglishapp.activity.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;

public class DictionaryActivity extends AppCompatActivity implements AlphabetAdapter.OnVocabularyClickListener, TextToSpeech.OnInitListener {

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

    private ImageButton btnSpeakWord;
    private TextToSpeech textToSpeech;
    private Vocabulary currentVocabulary;

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
        btnSpeakWord = findViewById(R.id.btnSpeakWord);


        textToSpeech = new TextToSpeech(this, this);

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

        btnSpeakWord.setOnClickListener(v -> {
            speakCurrentWord();
        });
    }

    private void loadVocabularyData() {
        vocabularyList.clear();
        vocabularyList.addAll(vocabularyDao.getAllVocabulary());
        alphabetAdapter.notifyDataSetChanged();

        if (!vocabularyList.isEmpty()) {
            Vocabulary firstVocab = vocabularyList.get(0);
            displayVocabulary(firstVocab);

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
        currentVocabulary = vocabulary;
        if (vocabulary == null) {
            imgWordView.setImageResource(R.drawable.abc);
            tvNameColor.setText("");
            btnSpeakWord.setEnabled(false);
            return;
        }
        btnSpeakWord.setEnabled(true);

        if (vocabulary.getImagePath() != null && !vocabulary.getImagePath().isEmpty()) {
            InputStream inputStream = null;
            try {
                if (!vocabulary.getImagePath().startsWith("/")) {
                    inputStream = getAssets().open(vocabulary.getImagePath());
                } else {
                    inputStream = new java.io.FileInputStream(new java.io.File(vocabulary.getImagePath()));
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgWordView.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.e("DictionaryActivity", "Error loading image: " + vocabulary.getImagePath(), e);

                try {
                    inputStream = getAssets().open(vocabulary.getImagePath().replaceFirst("^images/", ""));
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgWordView.setImageBitmap(bitmap);
                } catch (IOException e2) {
                    Log.e("DictionaryActivity", "Error loading image as fallback: " + vocabulary.getImagePath(), e2);
                    imgWordView.setImageResource(R.drawable.abc);
                }
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


    private void speakCurrentWord() {
        if (currentVocabulary != null && currentVocabulary.getWord() != null && !currentVocabulary.getWord().isEmpty()) {
            if (textToSpeech != null && textToSpeech.getEngines().size() > 0) {
                if (textToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.setLanguage(Locale.US);
                } else if (textToSpeech.isLanguageAvailable(Locale.UK) == TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.setLanguage(Locale.UK);
                } else if (textToSpeech.isLanguageAvailable(Locale.ENGLISH) == TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                } else {
                    Toast.makeText(this, "Ngôn ngữ Tiếng Anh không được hỗ trợ.", Toast.LENGTH_SHORT).show();
                    return;
                }
                textToSpeech.speak(currentVocabulary.getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(this, "TextToSpeech không sẵn sàng.", Toast.LENGTH_SHORT).show();

                Intent checkIntent = new Intent();
                checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                startActivityForResult(checkIntent, 1234);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

                textToSpeech = new TextToSpeech(this, this);
                Log.i("DictionaryActivity", "TTS data checked, re-initializing.");
            } else {

                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                try {
                    startActivity(installIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Không thể mở cài đặt TTS.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Ngôn ngữ này không được hỗ trợ");

                result = textToSpeech.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Ngôn ngữ English cũng không được hỗ trợ");
                    btnSpeakWord.setEnabled(false);
                    Toast.makeText(this, "TextToSpeech: Ngôn ngữ không được hỗ trợ.", Toast.LENGTH_LONG).show();
                } else {
                    btnSpeakWord.setEnabled(true);
                }
            } else {
                btnSpeakWord.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Khởi tạo TextToSpeech thất bại!");
            Toast.makeText(this, "Khởi tạo TextToSpeech thất bại.", Toast.LENGTH_LONG).show();
            btnSpeakWord.setEnabled(false);
        }
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

    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            Log.d("DictionaryActivity", "TextToSpeech đã được giải phóng.");
        }
        super.onDestroy();
    }
}