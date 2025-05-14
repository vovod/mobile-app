package com.nhom13.learningenglishapp.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.adapters.UserAdapter;
import com.nhom13.learningenglishapp.database.dao.ChapterDao;
import com.nhom13.learningenglishapp.database.dao.QuizDao;
import com.nhom13.learningenglishapp.database.dao.QuizResultDao;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.dao.VocabularyDao;
import com.nhom13.learningenglishapp.database.dao.VideoDao;
import com.nhom13.learningenglishapp.database.models.User;

import java.util.List;
import java.util.Locale; // Để định dạng số thập phân

public class AdminStatisticsActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalChapters, tvTotalVocabulary, tvTotalQuizzes, tvTotalVideos;
    private TextView tvTotalGamesPlayed, tvTotalQuestionsAttempted, tvTotalCorrectAnswers, tvAverageScore;
    private RecyclerView rcvTopUsers;
    private ImageButton btnBack;

    private UserDao userDao;
    private ChapterDao chapterDao;
    private VocabularyDao vocabularyDao;
    private QuizDao quizDao;
    private VideoDao videoDao;
    private QuizResultDao quizResultDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_statistics);

        // Ánh xạ Views
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalChapters = findViewById(R.id.tvTotalChapters);
        tvTotalVocabulary = findViewById(R.id.tvTotalVocabulary);
        tvTotalQuizzes = findViewById(R.id.tvTotalQuizzes);
        tvTotalVideos = findViewById(R.id.tvTotalVideos);
        tvTotalGamesPlayed = findViewById(R.id.tvTotalGamesPlayed);
        tvTotalQuestionsAttempted = findViewById(R.id.tvTotalQuestionsAttempted);
        tvTotalCorrectAnswers = findViewById(R.id.tvTotalCorrectAnswers);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        rcvTopUsers = findViewById(R.id.rcvTopUsers);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo DAOs
        userDao = new UserDao(this);
        chapterDao = new ChapterDao(this);
        vocabularyDao = new VocabularyDao(this);
        quizDao = new QuizDao(this);
        videoDao = new VideoDao(this);
        quizResultDao = new QuizResultDao(this);

        // Thiết lập RecyclerView cho Top Users
        rcvTopUsers.setLayoutManager(new LinearLayoutManager(this));
        // Sử dụng null cho OnUserDeleteListener vì admin không xóa user từ màn hình thống kê
        UserAdapter topUserAdapter = new UserAdapter(this, null, null);
        rcvTopUsers.setAdapter(topUserAdapter);

        // Tải và hiển thị dữ liệu thống kê
        loadStatisticsData();

        // Thiết lập sự kiện cho nút Back
        btnBack.setOnClickListener(v -> finish()); // Đóng Activity hiện tại để quay về AdminHomePageActivity
    }

    private void loadStatisticsData() {
        // Thống kê chung
        int totalUsers = userDao.getTotalUserCount();
        int totalChapters = chapterDao.getTotalChapterCount();
        int totalVocabulary = vocabularyDao.getTotalVocabularyCount();
        int totalQuizzes = quizDao.getQuizCount(); // Sử dụng phương thức đã có
        int totalVideos = videoDao.getTotalVideoCount();

        tvTotalUsers.setText("Tổng số người dùng: " + totalUsers);
        tvTotalChapters.setText("Tổng số chương: " + totalChapters);
        tvTotalVocabulary.setText("Tổng số từ vựng: " + totalVocabulary);
        tvTotalQuizzes.setText("Tổng số Quiz: " + totalQuizzes);
        tvTotalVideos.setText("Tổng số Video: " + totalVideos);

        // Thống kê Game/Quiz
        int totalGamesPlayed = quizResultDao.getTotalGamesPlayed();
        int totalQuestionsAttempted = quizResultDao.getTotalQuestionsAttempted();
        int totalCorrectAnswers = quizResultDao.getTotalCorrectAnswers();
        double averageScore = quizResultDao.getAverageScore();

        tvTotalGamesPlayed.setText("Tổng số lượt Game đã chơi: " + totalGamesPlayed);
        tvTotalQuestionsAttempted.setText("Tổng số câu hỏi đã trả lời: " + totalQuestionsAttempted);
        tvTotalCorrectAnswers.setText("Tổng số câu trả lời đúng: " + totalCorrectAnswers);
        // Định dạng điểm trung bình
        tvAverageScore.setText(String.format(Locale.getDefault(), "Điểm trung bình mỗi game: %.2f", averageScore));

        // Top Users (Lấy top 5 chẳng hạn)
        List<User> topUsers = userDao.getTopUsersByScore(5);
        // Cập nhật dữ liệu cho adapter của RecyclerView
        ((UserAdapter) rcvTopUsers.getAdapter()).updateData(topUsers);

        if (topUsers.isEmpty()) {
            // Có thể hiển thị thông báo nếu không có user nào để hiển thị top
        }
    }
}