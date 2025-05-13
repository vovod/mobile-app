package com.nhom13.learningenglishapp.activity.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.LoginActivity;
import com.nhom13.learningenglishapp.adapters.AnswerAdapter;
import com.nhom13.learningenglishapp.database.dao.QuizDao;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.Quiz;
import com.nhom13.learningenglishapp.database.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GuessImageActivity extends AppCompatActivity implements AnswerAdapter.OnAnswerClickListener {

    private static final int TOTAL_QUESTIONS = 10;
    private static final int TIME_PER_QUESTION = 10000; // 10 seconds per question

    private ImageButton btnBack, btnSetting, btnSpeak;
    private TextView tvScore, tvCurrentQuestion, tvQuestion;
    private ImageView imgAnswer;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private QuizDao quizDao;
    private UserDao userDao;
    private List<Quiz> quizList;
    private List<String> answerOptions;
    private AnswerAdapter answerAdapter;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private int userId = 0;
    private String username;
    private CountDownTimer timer;
    private TextToSpeech textToSpeech;
    private boolean isAnswered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_seeandchoose);

        // Nhận dữ liệu từ intent
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        if (getIntent().hasExtra("score")) {
            score = getIntent().getIntExtra("score", 0);
        }
        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getIntExtra("userId", 0);
        }

        // Khởi tạo DAO
        quizDao = new QuizDao(this);
        userDao = new UserDao(this);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btnBacKDH);
        btnSetting = findViewById(R.id.btnSettingGameDH);
        btnSpeak = findViewById(R.id.igbSpeakDH);
        tvScore = findViewById(R.id.tvDiem);
        tvCurrentQuestion = findViewById(R.id.tvCurrentQuestion);
        tvQuestion = findViewById(R.id.tvNoiDungCauHoiDH);
        imgAnswer = findViewById(R.id.imgDapAn);
        progressBar = findViewById(R.id.progressBarCount);
        recyclerView = findViewById(R.id.rcvTraLoiDH);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        answerOptions = new ArrayList<>();
        answerAdapter = new AnswerAdapter(this, answerOptions, this);
        recyclerView.setAdapter(answerAdapter);

        // Thiết lập TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Ngôn ngữ không được hỗ trợ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Khởi tạo TextToSpeech thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập sự kiện click cho các nút
        btnBack.setOnClickListener(v -> {
            showExitConfirmDialog();
        });

        btnSetting.setOnClickListener(v -> {
            showSettingDialog();
        });

        btnSpeak.setOnClickListener(v -> {
            speakQuestion();
        });

        // Cập nhật điểm số hiện tại
        tvScore.setText(String.valueOf(score));

        // Tải câu hỏi và bắt đầu trò chơi
        loadQuizzes();
    }

    private void loadQuizzes() {
        // Lấy 10 câu hỏi ngẫu nhiên từ cơ sở dữ liệu
        quizList = quizDao.getRandomQuizzes(TOTAL_QUESTIONS);

        // Nếu không đủ 10 câu hỏi, lấy tất cả câu hỏi có sẵn
        if (quizList.size() < TOTAL_QUESTIONS) {
            quizList = quizDao.getAllQuizzes();
        }

        // Nếu vẫn không có câu hỏi nào, hiển thị thông báo và quay lại
        if (quizList.isEmpty()) {
            Toast.makeText(this, "Không có câu hỏi nào", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bắt đầu với câu hỏi đầu tiên
        showQuestion(0);
    }

    private void showQuestion(int index) {
        // Đặt lại trạng thái
        isAnswered = false;

        // Hủy timer cũ nếu có
        if (timer != null) {
            timer.cancel();
        }

        // Kiểm tra xem đã hết câu hỏi chưa
        if (index >= quizList.size()) {
            showGameOverDialog();
            return;
        }

        // Lấy câu hỏi hiện tại
        Quiz currentQuiz = quizList.get(index);

        // Cập nhật số câu hỏi hiện tại
        tvCurrentQuestion.setText((index + 1) + "/" + quizList.size());

        // Hiển thị câu hỏi
        tvQuestion.setText("What is this?");

        // Hiển thị hình ảnh
        if (currentQuiz.getImagePath() != null && !currentQuiz.getImagePath().isEmpty()) {
            File imgFile = new File(currentQuiz.getImagePath());
            if (imgFile.exists()) {
                imgAnswer.setImageURI(android.net.Uri.fromFile(imgFile));
            } else {
                imgAnswer.setImageResource(R.drawable.question);
            }
        } else {
            imgAnswer.setImageResource(R.drawable.question);
        }

        // Tạo danh sách đáp án - chỉ hiển thị 2 đáp án: đúng và sai
        answerOptions.clear();
        answerOptions.add(currentQuiz.getCorrectAnswer());
        answerOptions.add(currentQuiz.getWrongAnswer());

        // Xáo trộn đáp án
        Collections.shuffle(answerOptions);

        // Cập nhật adapter
        answerAdapter.notifyDataSetChanged();

        // Đặt lại ProgressBar
        progressBar.setProgress(100);

        // Bắt đầu đếm ngược
        startTimer();
    }

    private void startTimer() {
        timer = new CountDownTimer(TIME_PER_QUESTION, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished * 100 / TIME_PER_QUESTION);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                if (!isAnswered) {
                    // Nếu hết thời gian mà chưa trả lời, chuyển sang câu hỏi tiếp theo
                    Toast.makeText(GuessImageActivity.this, "Hết thời gian!", Toast.LENGTH_SHORT).show();
                    currentQuestionIndex++;
                    showQuestion(currentQuestionIndex);
                }
            }
        }.start();
    }

    @Override
    public void onAnswerClick(String answer) {
        // Đánh dấu đã trả lời
        isAnswered = true;

        // Hủy timer
        if (timer != null) {
            timer.cancel();
        }

        // Kiểm tra đáp án
        Quiz currentQuiz = quizList.get(currentQuestionIndex);
        if (answer.equals(currentQuiz.getCorrectAnswer())) {
            // Đáp án đúng
            Toast.makeText(this, "Đúng!", Toast.LENGTH_SHORT).show();
            score += 10; // Cộng 10 điểm cho mỗi câu trả lời đúng
            tvScore.setText(String.valueOf(score));
        } else {
            // Đáp án sai
            Toast.makeText(this, "Sai! Đáp án đúng là: " + currentQuiz.getCorrectAnswer(), Toast.LENGTH_SHORT).show();
        }

        // Chờ 1 giây trước khi chuyển sang câu hỏi tiếp theo
        new android.os.Handler().postDelayed(() -> {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }, 1000);
    }

    private void speakQuestion() {
        if (textToSpeech != null) {
            textToSpeech.speak(tvQuestion.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
        }
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
            Intent intent = new Intent(GuessImageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(GuessImageActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận thoát");
        builder.setMessage("Bạn có chắc chắn muốn thoát? Tiến trình chơi sẽ không được lưu.");
        builder.setPositiveButton("Thoát", (dialog, which) -> {
            // Quay về màn hình chính
            Intent intent = new Intent(GuessImageActivity.this, GameActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kết thúc trò chơi");
        builder.setMessage("Điểm của bạn: " + score);
        builder.setCancelable(false);
        builder.setPositiveButton("Quay về", (dialog, which) -> {
            // Cập nhật điểm cho người dùng
            if (!username.equals("admin") && !username.isEmpty()) {
                User user = userDao.getUserByUsername(username);
                if (user != null) {
                    userDao.updateScore(username, user.getScore() + score);
                }
            }

            // Quay về màn hình chính
            Intent intent = new Intent(GuessImageActivity.this, GameActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
