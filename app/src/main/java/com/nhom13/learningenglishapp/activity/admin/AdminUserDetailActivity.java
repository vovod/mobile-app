package com.nhom13.learningenglishapp.activity.admin;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log; // Import Log

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.dao.QuizResultDao;
import com.nhom13.learningenglishapp.database.models.User;
import com.nhom13.learningenglishapp.database.models.QuizResult;
import com.nhom13.learningenglishapp.adapters.QuizResultAdapter; // Sẽ tạo adapter này sau

import java.util.ArrayList;
import java.util.List;

public class AdminUserDetailActivity extends AppCompatActivity {

    private TextView tvUsernameDetail, tvScoreDetail;
    private RecyclerView rcvQuizResults;
    private ImageButton btnBack;

    private UserDao userDao;
    private QuizResultDao quizResultDao;
    private QuizResultAdapter quizResultAdapter;
    private List<QuizResult> quizResultList;

    private int userId; // Để lưu userId của người dùng đang xem

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail); // Sẽ tạo layout này sau

        // Khởi tạo DAOs
        userDao = new UserDao(this);
        quizResultDao = new QuizResultDao(this);

        // Ánh xạ Views
        tvUsernameDetail = findViewById(R.id.tvUsernameDetail);
        tvScoreDetail = findViewById(R.id.tvScoreDetail);
        rcvQuizResults = findViewById(R.id.rcvQuizResults);
        btnBack = findViewById(R.id.btnBack);

        // Lấy userId từ Intent
        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getIntExtra("userId", -1);
            if (userId == -1) {
                Toast.makeText(this, "Lỗi: Không có thông tin người dùng.", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity nếu không có userId hợp lệ
                return;
            }
        } else {
            Toast.makeText(this, "Lỗi: Không có thông tin người dùng.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có userId
            return;
        }


        // Thiết lập RecyclerView cho lịch sử kết quả Quiz
        quizResultList = new ArrayList<>();
        rcvQuizResults.setLayoutManager(new LinearLayoutManager(this));
        quizResultAdapter = new QuizResultAdapter(this, quizResultList); // Truyền list rỗng ban đầu
        rcvQuizResults.setAdapter(quizResultAdapter);

        // Tải và hiển thị thông tin user và lịch sử game
        loadUserDataAndQuizResults(userId);

        // Thiết lập sự kiện cho nút Back
        btnBack.setOnClickListener(v -> finish()); // Đóng Activity hiện tại
    }

    private void loadUserDataAndQuizResults(int userId) {
        // Lấy thông tin người dùng
        User user = userDao.getUserById(userId); // Cần thêm phương thức getUserById trong UserDao

        if (user != null) {
            tvUsernameDetail.setText("Người dùng: " + user.getUsername());
            tvScoreDetail.setText("Tổng điểm: " + user.getScore());

            // Lấy lịch sử kết quả Quiz của người dùng này
            List<QuizResult> results = quizResultDao.getQuizResultsByUser(userId);

            quizResultList.clear();
            quizResultList.addAll(results);
            quizResultAdapter.notifyDataSetChanged();

            if (results.isEmpty()) {
                // Thông báo nếu không có lịch sử chơi game
                Toast.makeText(this, "Người dùng này chưa chơi game nào.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            Log.e("AdminUserDetail", "User not found with ID: " + userId);
            finish(); // Đóng activity nếu không tìm thấy user
        }
    }
}
