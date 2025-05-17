package com.nhom13.learningenglishapp.activity.admin;

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
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.dao.QuizResultDao;
import com.nhom13.learningenglishapp.database.models.User;
import com.nhom13.learningenglishapp.database.models.QuizResult;
import com.nhom13.learningenglishapp.adapters.QuizResultAdapter;

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

    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);


        userDao = new UserDao(this);
        quizResultDao = new QuizResultDao(this);


        tvUsernameDetail = findViewById(R.id.tvUsernameDetail);
        tvScoreDetail = findViewById(R.id.tvScoreDetail);
        rcvQuizResults = findViewById(R.id.rcvQuizResults);
        btnBack = findViewById(R.id.btnBack);


        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getIntExtra("userId", -1);
            if (userId == -1) {
                Toast.makeText(this, "Lỗi: Không có thông tin người dùng.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "Lỗi: Không có thông tin người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }



        quizResultList = new ArrayList<>();
        rcvQuizResults.setLayoutManager(new LinearLayoutManager(this));
        quizResultAdapter = new QuizResultAdapter(this, quizResultList);
        rcvQuizResults.setAdapter(quizResultAdapter);


        loadUserDataAndQuizResults(userId);


        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserDataAndQuizResults(int userId) {

        User user = userDao.getUserById(userId);

        if (user != null) {
            tvUsernameDetail.setText("Người dùng: " + user.getUsername());
            tvScoreDetail.setText("Tổng điểm: " + user.getScore());


            List<QuizResult> results = quizResultDao.getQuizResultsByUser(userId);

            quizResultList.clear();
            quizResultList.addAll(results);
            quizResultAdapter.notifyDataSetChanged();

            if (results.isEmpty()) {

                Toast.makeText(this, "Người dùng này chưa chơi game nào.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            Log.e("AdminUserDetail", "User not found with ID: " + userId);
            finish();
        }
    }
}
