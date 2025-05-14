package com.nhom13.learningenglishapp.activity.user;

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
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.adapters.AnswerAdapter;
import com.nhom13.learningenglishapp.database.dao.QuizDao;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.dao.QuizResultDao; // Import QuizResultDao
import com.nhom13.learningenglishapp.database.models.Quiz;
import com.nhom13.learningenglishapp.database.models.User;
import com.nhom13.learningenglishapp.database.models.QuizResult; // Import QuizResult
import com.nhom13.learningenglishapp.activity.LoginActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Date; // Import Date

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
    private QuizResultDao quizResultDao; // Khai báo QuizResultDao
    private List<Quiz> quizList;
    private List<String> answerOptions;
    private AnswerAdapter answerAdapter;

    private int currentQuestionIndex = 0;
    private int currentScore = 0;
    private int correctAnswersCount = 0; // Biến mới đếm số câu trả lời đúng
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
        quizResultDao = new QuizResultDao(this); // Khởi tạo QuizResultDao

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
        correctAnswersCount = 0; // Reset số câu đúng khi bắt đầu game mới
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
            // Game kết thúc
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
                // Kiểm tra xem ảnh có phải từ assets hay không
                // Logic FileUtils.saveImageToInternalStorage lưu ảnh vào internal storage
                // Nếu ảnh lưu trong internal storage thì đường dẫn sẽ là absolute path
                // Nếu ảnh vẫn nằm trong assets thì đường dẫn sẽ là relative path (ví dụ: images/quiz/...)
                // Cần kiểm tra xem đường dẫn là loại nào để load cho đúng

                if (currentQuiz.getImagePath().startsWith("images/")) { // Giả định ảnh trong assets có tiền tố này
                    inputStream = getAssets().open(currentQuiz.getImagePath());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgAnswer.setImageBitmap(bitmap);
                } else { // Giả định ảnh lưu trong internal storage
                    imgAnswer.setImageURI(Uri.parse(currentQuiz.getImagePath()));
                }

            } catch (IOException e) {
                Log.e("GuessImageActivity", "Error loading image: " + currentQuiz.getImagePath(), e);
                imgAnswer.setImageResource(R.drawable.question); // Ảnh mặc định nếu lỗi
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
            Log.e("GuessImageActivity", "Quiz ID " + currentQuiz.getId() + " has no correct answer!");
            // Cần xử lý trường hợp này tốt hơn, có thể bỏ qua câu hỏi hoặc thêm đáp án giả
        }

        if (currentQuiz.getWrongAnswer() != null && !currentQuiz.getWrongAnswer().isEmpty()) {
            // Chỉ thêm đáp án sai nếu nó khác đáp án đúng và không rỗng
            if (!currentQuiz.getCorrectAnswer().equals(currentQuiz.getWrongAnswer())) {
                answerOptions.add(currentQuiz.getWrongAnswer());
            } else {
                Log.w("GuessImageActivity", "Quiz ID " + currentQuiz.getId() + " has correct and wrong answers identical.");
                // Nếu đáp án đúng và sai trùng nhau, có thể lấy một đáp án sai khác từ DB
                // hoặc thêm một đáp án giả. Để đơn giản, tạm thời chỉ dùng 1 đáp án nếu trùng.
            }

        } else {
            Log.e("GuessImageActivity", "Quiz ID " + currentQuiz.getId() + " has no wrong answer!");
            // Cần xử lý trường hợp này tốt hơn, có thể bỏ qua câu hỏi hoặc thêm đáp án giả
        }

        // Đảm bảo luôn có ít nhất 2 đáp án nếu game yêu cầu
        // Nếu sau khi thêm đáp án đúng và sai vẫn < 2, có thể lấy thêm quiz khác hoặc tạo đáp án giả.
        // Đối với game Đoán hình ảnh với 2 lựa chọn (đúng/sai), việc đảm bảo DB có đủ 2 đáp án khác nhau là quan trọng.
        if (answerOptions.size() < 2 && currentQuiz.getCorrectAnswer() != null && !currentQuiz.getCorrectAnswer().isEmpty()) {
            // Nếu chỉ có đáp án đúng, thử lấy thêm 1 đáp án sai ngẫu nhiên từ các quiz khác (trừ đáp án đúng hiện tại)
            Quiz randomWrongQuiz = quizDao.getRandomQuiz(); // Lấy 1 quiz ngẫu nhiên khác
            if (randomWrongQuiz != null && randomWrongQuiz.getWrongAnswer() != null &&
                    !randomWrongQuiz.getWrongAnswer().isEmpty() &&
                    !randomWrongQuiz.getWrongAnswer().equals(currentQuiz.getCorrectAnswer())) {
                answerOptions.add(randomWrongQuiz.getWrongAnswer());
            } else if (randomWrongQuiz != null && randomWrongQuiz.getCorrectAnswer() != null &&
                    !randomWrongQuiz.getCorrectAnswer().isEmpty() &&
                    !randomWrongQuiz.getCorrectAnswer().equals(currentQuiz.getCorrectAnswer())) {
                // Nếu không có đáp án sai khác, thử lấy đáp án đúng của quiz khác làm đáp án sai giả
                answerOptions.add(randomWrongQuiz.getCorrectAnswer());
            } else {
                // Trường hợp xấu nhất: không tìm được đáp án sai khác, thêm đáp án giả
                if(answerOptions.size() < 2) answerOptions.add("Another Option");
            }
            Log.w("GuessImageActivity", "Quiz ID " + currentQuiz.getId() + " initially had less than 2 distinct options. Added fallback.");
        } else if (answerOptions.size() == 1 && currentQuiz.getCorrectAnswer() != null && !currentQuiz.getCorrectAnswer().isEmpty()){
            // Nếu chỉ có đáp án đúng và đáp án sai trùng nhau hoặc trống ban đầu, thêm đáp án giả nếu chưa có đủ 2
            if (answerOptions.size() < 2) answerOptions.add("Another Option");
            Log.w("GuessImageActivity", "Quiz ID " + currentQuiz.getId() + " initially had less than 2 distinct options. Added fallback.");
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
        if (isAnswered) return; // Tránh click nhiều lần

        isAnswered = true;
        if (timer != null) {
            timer.cancel();
        }

        Quiz currentQuiz = quizList.get(currentQuestionIndex);
        if (currentQuiz.getCorrectAnswer() != null && answer.equals(currentQuiz.getCorrectAnswer())) {
            Toast.makeText(this, "Đúng!", Toast.LENGTH_SHORT).show();
            currentScore += 10;
            correctAnswersCount++; // Tăng số câu đúng
            tvScore.setText(String.valueOf(currentScore));
        } else {
            if (currentQuiz.getCorrectAnswer() != null) {
                Toast.makeText(this, "Sai! Đáp án đúng là: " + currentQuiz.getCorrectAnswer(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Sai!", Toast.LENGTH_LONG).show(); // Trường hợp không có đáp án đúng
            }
        }

        // Chuyển sang câu hỏi tiếp theo sau một khoảng thời gian
        new android.os.Handler().postDelayed(() -> {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }, 1500); // Delay 1.5 giây
    }

    private void speakQuestion() {
        // Cần kiểm tra xem TextToSpeech đã sẵn sàng chưa trước khi nói
        if (textToSpeech != null && TextToSpeech.LANG_MISSING_DATA != textToSpeech.setLanguage(Locale.US) && TextToSpeech.LANG_NOT_SUPPORTED != textToSpeech.setLanguage(Locale.US)) {
            // Bạn có thể muốn đọc đáp án đúng thay vì câu hỏi "What is this?" cho game đoán hình ảnh
            // Ví dụ: speak(quizList.get(currentQuestionIndex).getCorrectAnswer());
            textToSpeech.speak(tvQuestion.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);

        } else {
            Toast.makeText(this, "TextToSpeech không sẵn sàng", Toast.LENGTH_SHORT).show();
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
            if (timer != null) {
                timer.cancel(); // Hủy timer nếu thoát giữa chừng
            }
            Intent intent = new Intent(GuessImageActivity.this, GameActivity.class);
            intent.putExtra("username", username);
            // Không cần gửi điểm nếu thoát giữa chừng và không lưu kết quả ván này
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showGameOverDialog() {
        if (timer != null) {
            timer.cancel(); // Hủy timer khi game kết thúc
        }

        // --- LƯU KẾT QUẢ VÁN GAME HIỆN TẠI ---
        // 1. Tìm User ID từ username
        User currentUser = userDao.getUserByUsername(username);
        if (currentUser != null && !username.equals("admin")) { // Chỉ lưu kết quả cho user thường, không phải admin
            int userId = currentUser.getId();
            int totalQuestions = quizList.size();
            long timestamp = new Date().getTime(); // Lấy timestamp hiện tại

            QuizResult gameResult = new QuizResult(userId, currentScore, totalQuestions, correctAnswersCount, timestamp);

            // 2. Lưu kết quả vào bảng quiz_results
            boolean isResultSaved = quizResultDao.insertQuizResult(gameResult);
            if (isResultSaved) {
                Log.d("GuessImageActivity", "Game result saved successfully for user " + username);
            } else {
                Log.e("GuessImageActivity", "Failed to save game result for user " + username);
            }

            // 3. Cập nhật điểm tổng cho user trong bảng users
            int finalTotalScoreForUser = currentUser.getScore() + currentScore;
            boolean isScoreUpdated = userDao.updateScore(username, finalTotalScoreForUser);
            if (isScoreUpdated) {
                Log.d("GuessImageActivity", "User score updated successfully for user " + username);
            } else {
                Log.e("GuessImageActivity", "Failed to update user score for user " + username);
            }

        } else if (username != null && username.equals("admin")) {
            // Admin không lưu điểm và kết quả game
            Log.d("GuessImageActivity", "Admin finished game. Score not saved.");
        } else {
            Log.e("GuessImageActivity", "User not found in DB when trying to save game result.");
            // Xử lý trường hợp user không tồn tại (không nên xảy ra nếu luồng đăng nhập/đăng ký đúng)
        }
        // --- KẾT THÚC LƯU KẾT QUẢ ---


        // Hiển thị Dialog kết thúc game
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Bạn có thể tạo một layout dialog tùy chỉnh cho màn hình kết thúc game,
        // ví dụ sử dụng dialog_score.xml bạn đã tải lên.
        // Tạm thời sử dụng dialog mặc định:
        builder.setTitle("Kết thúc trò chơi");
        builder.setMessage("Điểm của bạn: " + currentScore + "\nSố câu đúng: " + correctAnswersCount + "/" + quizList.size());
        builder.setCancelable(false); // Không cho phép đóng dialog bằng cách click ra ngoài hoặc nút back
        builder.setPositiveButton("Quay về", (dialog, which) -> {
            // Chuyển về màn hình chọn Game
            Intent intent = new Intent(GuessImageActivity.this, GameActivity.class);
            intent.putExtra("username", username);
            // GameActivity sẽ tự lấy điểm tổng mới nhất từ DB
            startActivity(intent);
            finish();
        });
        // Thêm nút chơi lại nếu muốn
        // builder.setNegativeButton("Chơi lại", (dialog, which) -> {
        //     currentScore = 0;
        //     correctAnswersCount = 0;
        //     tvScore.setText(String.valueOf(currentScore));
        //     loadQuizzes(); // Tải lại quiz và bắt đầu game mới
        // });
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
