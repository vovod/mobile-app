package com.nhom13.learningenglishapp.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AdminStatisticsActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private TextView tvTotalUsers, tvTotalChapters, tvTotalVocabulary, tvTotalQuizzes, tvTotalVideos;
    private TextView tvTotalGamesPlayed, tvTotalQuestionsAttempted, tvTotalCorrectAnswers, tvAverageScore;
    private TextView tvActiveUsersLast7Days, tvScoreRange0_100, tvScoreRange101_500, tvScoreRange501_Plus;
    private RecyclerView rcvTopUsers;
    private ImageButton btnBack;

    private UserDao userDao;
    private ChapterDao chapterDao;
    private VocabularyDao vocabularyDao;
    private QuizDao quizDao;
    private VideoDao videoDao;
    private QuizResultDao quizResultDao;

    private static final String TAG = "AdminStatsActivity"; // Thêm TAG cho logging

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
        tvActiveUsersLast7Days = findViewById(R.id.tvActiveUsersLast7Days);
        tvScoreRange0_100 = findViewById(R.id.tvScoreRange0_100);
        tvScoreRange101_500 = findViewById(R.id.tvScoreRange101_500);
        tvScoreRange501_Plus = findViewById(R.id.tvScoreRange501_Plus);

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
        UserAdapter topUserAdapter = new UserAdapter(this, null, null, this);
        rcvTopUsers.setAdapter(topUserAdapter);

        // Tải và hiển thị dữ liệu thống kê
        loadStatisticsData();

        // Thiết lập sự kiện cho nút Back
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadStatisticsData() {
        // Thống kê chung
        int totalUsers = userDao.getTotalUserCount();
        int totalChapters = chapterDao.getTotalChapterCount();
        int totalVocabulary = vocabularyDao.getTotalVocabularyCount();
        int totalQuizzes = quizDao.getQuizCount();
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
        tvAverageScore.setText(String.format(Locale.getDefault(), "Điểm trung bình mỗi game: %.2f", averageScore));

        // Thống kê người dùng hoạt động
        tvActiveUsersLast7Days.setText("Người dùng hoạt động (7 ngày): - (Cần cập nhật DB)");

        // Thống kê phân phối điểm
        int count0_100 = userDao.getUserCountByScoreRange(0, 100);
        int count101_500 = userDao.getUserCountByScoreRange(101, 500);
        int count501_Plus = userDao.getUserCountByScoreRange(501, Integer.MAX_VALUE);

        tvScoreRange0_100.setText("Điểm 0-100: " + count0_100);
        tvScoreRange101_500.setText("Điểm 101-500: " + count101_500);
        tvScoreRange501_Plus.setText("Điểm >500: " + count501_Plus);

        // Top Users (Lấy top 5)
        List<User> topUsers = userDao.getTopUsersByScore(5);

        // Thêm log chi tiết cho topUsers
        Log.d(TAG, "Fetched topUsers list size: " + topUsers.size());
        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);
            Log.d(TAG, "Top User " + (i + 1) + ": " + user.getUsername() + " (Score: " + user.getScore() + ")");
        }

        ((UserAdapter) rcvTopUsers.getAdapter()).updateData(topUsers);

        if (topUsers.isEmpty()) {
            Log.d(TAG, "No non-admin users found for Top Users list (list is empty).");
        }
    }

    @Override
    public void onUserClick(User user) {
        Toast.makeText(this, "Xem chi tiết người dùng: " + user.getUsername(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminStatisticsActivity.this, AdminUserDetailActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }
}