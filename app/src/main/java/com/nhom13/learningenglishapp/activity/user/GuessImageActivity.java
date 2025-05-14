package com.nhom13.learningenglishapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
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
import com.nhom13.learningenglishapp.adapters.AnswerAdapter;
import com.nhom13.learningenglishapp.database.dao.QuizDao;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.Quiz;
import com.nhom13.learningenglishapp.database.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
// Bỏ java.util.Random nếu không dùng để tạo đáp án ngẫu nhiên nữa

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
    private int currentScore = 0;
    // private int userId = 0; // Có vẻ không được sử dụng trực tiếp
    private String username;
    private CountDownTimer timer;
    private TextToSpeech textToSpeech;
    private boolean isAnswered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_seeandchoose);

        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        // Điểm tổng của user sẽ được lấy từ DB khi kết thúc game, không cần truyền qua lại nhiều

        quizDao = new QuizDao(this);
        userDao = new UserDao(this);

        btnBack = findViewById(R.id.btnBacKDH);
        btnSetting = findViewById(R.id.btnSettingGameDH);
        btnSpeak = findViewById(R.id.igbSpeakDH);
        tvScore = findViewById(R.id.tvDiem);
        tvCurrentQuestion = findViewById(R.id.tvCurrentQuestion);
        tvQuestion = findViewById(R.id.tvNoiDungCauHoiDH);
        imgAnswer = findViewById(R.id.imgDapAn);
        progressBar = findViewById(R.id.progressBarCount);
        recyclerView = findViewById(R.id.rcvTraLoiDH);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        answerOptions = new ArrayList<>();
        answerAdapter = new AnswerAdapter(this, answerOptions, this);
        recyclerView.setAdapter(answerAdapter);

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

        btnBack.setOnClickListener(v -> showExitConfirmDialog());
        btnSetting.setOnClickListener(v -> showSettingDialog());
        btnSpeak.setOnClickListener(v -> speakQuestion());

        currentScore = 0;
        tvScore.setText(String.valueOf(currentScore));

        loadQuizzes();
    }

    private void loadQuizzes() {
        quizList = quizDao.getRandomQuizzes(TOTAL_QUESTIONS);
        if (quizList.isEmpty()) { // Nếu không có quiz nào
            Toast.makeText(this, "Không có câu hỏi nào trong cơ sở dữ liệu.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Nếu có ít hơn TOTAL_QUESTIONS, game sẽ chơi với số lượng hiện có.

        currentQuestionIndex = 0; // Đảm bảo bắt đầu từ câu hỏi đầu tiên
        showQuestion(currentQuestionIndex);
    }

    private void showQuestion(int index) {
        isAnswered = false;
        if (timer != null) {
            timer.cancel();
        }

        if (index >= quizList.size()) {
            showGameOverDialog();
            return;
        }

        Quiz currentQuiz = quizList.get(index);
        tvCurrentQuestion.setText((index + 1) + "/" + quizList.size());
        tvQuestion.setText("What is this?"); // Hoặc bạn có thể đặt câu hỏi khác

        // HIỂN THỊ HÌNH ẢNH TỪ ASSETS
        if (currentQuiz.getImagePath() != null && !currentQuiz.getImagePath().isEmpty()) {
            InputStream inputStream = null;
            try {
                inputStream = getAssets().open(currentQuiz.getImagePath());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAnswer.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("GuessImageActivity", "Error loading image from assets: " + currentQuiz.getImagePath(), e);
                imgAnswer.setImageResource(R.drawable.question);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e("GuessImageActivity", "Error closing InputStream for image: " + currentQuiz.getImagePath(), e);
                    }
                }
            }
        } else {
            imgAnswer.setImageResource(R.drawable.question);
        }

        // TẠO DANH SÁCH 2 ĐÁP ÁN (1 ĐÚNG, 1 SAI TỪ DB)
        answerOptions.clear();
        if (currentQuiz.getCorrectAnswer() != null && !currentQuiz.getCorrectAnswer().isEmpty()) {
            answerOptions.add(currentQuiz.getCorrectAnswer());
        } else {
            // Xử lý trường hợp không có đáp án đúng (không nên xảy ra)
            Log.e("GuessImageActivity", "Quiz ID " + currentQuiz.getId() + " has no correct answer!");
            // Có thể thêm một đáp án mặc định hoặc báo lỗi
        }

        if (currentQuiz.getWrongAnswer() != null && !currentQuiz.getWrongAnswer().isEmpty()) {
            answerOptions.add(currentQuiz.getWrongAnswer());
        } else {
            // Xử lý trường hợp không có đáp án sai (không nên xảy ra nếu game cần 2 lựa chọn)
            Log.e("GuessImageActivity", "Quiz ID " + currentQuiz.getId() + " has no wrong answer!");
            // Nếu muốn luôn có 2 đáp án, bạn có thể cần tạo 1 đáp án sai giả ở đây
            // Ví dụ: if (answerOptions.size() < 2) answerOptions.add("A different option");
            // Tuy nhiên, tốt nhất là đảm bảo dữ liệu nguồn (DB) luôn có đủ 2 đáp án.
        }

        // Nếu chỉ có 1 đáp án (do lỗi dữ liệu), game có thể không hợp lý.
        // Cần đảm bảo mỗi quiz trong DB có ít nhất 1 đáp án đúng và 1 đáp án sai.
        if (answerOptions.size() < 2) {
            Log.w("GuessImageActivity", "Quiz ID " + currentQuiz.getId() + " has less than 2 options. Check data.");
            // Có thể hiển thị thông báo lỗi cho người dùng hoặc bỏ qua câu hỏi này
            // For now, we shuffle what we have.
        }

        Collections.shuffle(answerOptions); // Xáo trộn các đáp án
        answerAdapter.notifyDataSetChanged();

        progressBar.setProgress(100);
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
                    Toast.makeText(GuessImageActivity.this, "Hết thời gian!", Toast.LENGTH_SHORT).show();
                    currentQuestionIndex++;
                    showQuestion(currentQuestionIndex);
                }
            }
        }.start();
    }

    @Override
    public void onAnswerClick(String answer) {
        isAnswered = true;
        if (timer != null) {
            timer.cancel();
        }

        Quiz currentQuiz = quizList.get(currentQuestionIndex);
        if (currentQuiz.getCorrectAnswer() != null && answer.equals(currentQuiz.getCorrectAnswer())) {
            Toast.makeText(this, "Đúng!", Toast.LENGTH_SHORT).show();
            currentScore += 10;
            tvScore.setText(String.valueOf(currentScore));
        } else {
            if (currentQuiz.getCorrectAnswer() != null) {
                Toast.makeText(this, "Sai! Đáp án đúng là: " + currentQuiz.getCorrectAnswer(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Sai!", Toast.LENGTH_LONG).show(); // Trường hợp không có đáp án đúng
            }
        }

        new android.os.Handler().postDelayed(() -> {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }, 1500);
    }

    private void speakQuestion() {
        if (textToSpeech != null && !tvQuestion.getText().toString().isEmpty()) {
            textToSpeech.speak(tvQuestion.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
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
            Intent intent = new Intent(GuessImageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity();
            Toast.makeText(GuessImageActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận thoát");
        builder.setMessage("Bạn có chắc chắn muốn thoát? Tiến trình chơi sẽ không được lưu.");
        builder.setPositiveButton("Thoát", (dialog, which) -> {
            Intent intent = new Intent(GuessImageActivity.this, GameActivity.class);
            intent.putExtra("username", username);
            // Không cần gửi điểm nếu thoát giữa chừng và không lưu
            // GameActivity sẽ lấy điểm tổng của user từ DB nếu cần
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kết thúc trò chơi");
        builder.setMessage("Điểm của bạn: " + currentScore);
        builder.setCancelable(false);
        builder.setPositiveButton("Quay về", (dialog, which) -> {
            int finalTotalScoreForUser = -1; // Khởi tạo giá trị để biết có cập nhật không

            if (username != null && !username.equals("admin") && !username.isEmpty()) {
                User user = userDao.getUserByUsername(username);
                if (user != null) {
                    finalTotalScoreForUser = user.getScore() + currentScore;
                    userDao.updateScore(username, finalTotalScoreForUser);
                } else {
                    // User không tồn tại trong DB, có thể là lỗi hoặc trường hợp đặc biệt
                    Log.e("GuessImageActivity", "User " + username + " not found in DB to update score.");
                    // Lấy điểm ban đầu được truyền vào (nếu có) hoặc mặc định
                    finalTotalScoreForUser = getIntent().getIntExtra("score", 0) + currentScore;
                }
            } else if (username != null && username.equals("admin")) {
                // Admin không lưu điểm, lấy điểm ban đầu (nếu có)
                finalTotalScoreForUser = getIntent().getIntExtra("score", 0) + currentScore;
            } else {
                // Không có username, trường hợp lạ, lấy điểm ban đầu
                finalTotalScoreForUser = getIntent().getIntExtra("score", 0) + currentScore;
            }

            Intent intent = new Intent(GuessImageActivity.this, GameActivity.class);
            intent.putExtra("username", username);
            if (finalTotalScoreForUser != -1) {
                intent.putExtra("score", finalTotalScoreForUser); // Gửi điểm tổng mới (hoặc điểm tính toán được)
            }
            // Nếu không có điểm nào được tính, không gửi "score" hoặc gửi giá trị mặc định
            // mà GameActivity có thể xử lý

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
