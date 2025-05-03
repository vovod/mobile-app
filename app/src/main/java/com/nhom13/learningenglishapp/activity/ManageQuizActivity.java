package com.nhom13.learningenglishapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.adapters.QuizAdapter;
import com.nhom13.learningenglishapp.database.dao.QuizDao;
import com.nhom13.learningenglishapp.database.models.Quiz;

import java.util.ArrayList;
import java.util.List;

public class ManageQuizActivity extends AppCompatActivity implements QuizAdapter.OnQuizListener {

    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private List<Quiz> quizList;
    private QuizDao quizDao;
    private ImageButton btnBack, btnAdd;
    private Uri selectedImageUri;
    private ImageView selectedImageView;

    private ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (selectedImageView != null) {
                        selectedImageView.setImageURI(uri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_quiz);

        // Khởi tạo DAO
        quizDao = new QuizDao(this);

        // Khởi tạo danh sách quiz
        quizList = new ArrayList<>();

        // Ánh xạ các view
        recyclerView = findViewById(R.id.rcvQuiz);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAddQuiz);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(this, quizList, this);
        recyclerView.setAdapter(quizAdapter);

        // Tải dữ liệu quiz
        loadQuizData();

        // Thiết lập sự kiện click cho các nút
        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnAdd.setOnClickListener(v -> {
            showAddQuizDialog();
        });
    }

    private void loadQuizData() {
        quizList.clear();
        quizList.addAll(quizDao.getAllQuizzes());
        quizAdapter.notifyDataSetChanged();
    }

    private void showAddQuizDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_quiz, null);
        builder.setView(dialogView);

        EditText etCorrectAnswer = dialogView.findViewById(R.id.etCorrectAnswer);
        EditText etWrongAnswer = dialogView.findViewById(R.id.etWrongAnswer);
        ImageView imgQuiz = dialogView.findViewById(R.id.imgQuiz);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Lưu trữ ImageView để cập nhật sau khi chọn hình ảnh
        selectedImageView = imgQuiz;
        selectedImageUri = null;

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String correctAnswer = etCorrectAnswer.getText().toString().trim();
            String wrongAnswer = etWrongAnswer.getText().toString().trim();

            if (correctAnswer.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đáp án đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (wrongAnswer.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đáp án sai", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            Quiz quiz = new Quiz(correctAnswer, wrongAnswer, "");
            boolean success = quizDao.insertQuiz(quiz, selectedImageUri);

            if (success) {
                Toast.makeText(this, "Thêm quiz thành công", Toast.LENGTH_SHORT).show();
                loadQuizData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Thêm quiz thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void showEditQuizDialog(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_quiz, null);
        builder.setView(dialogView);

        EditText etCorrectAnswer = dialogView.findViewById(R.id.etCorrectAnswer);
        EditText etWrongAnswer = dialogView.findViewById(R.id.etWrongAnswer);
        ImageView imgQuiz = dialogView.findViewById(R.id.imgQuiz);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Hiển thị thông tin quiz hiện tại
        etCorrectAnswer.setText(quiz.getCorrectAnswer());
        etWrongAnswer.setText(quiz.getWrongAnswer());

        // Hiển thị hình ảnh nếu có
        if (quiz.getImagePath() != null && !quiz.getImagePath().isEmpty()) {
            imgQuiz.setImageURI(Uri.parse(quiz.getImagePath()));
        }

        // Lưu trữ ImageView để cập nhật sau khi chọn hình ảnh
        selectedImageView = imgQuiz;
        selectedImageUri = null;

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String correctAnswer = etCorrectAnswer.getText().toString().trim();
            String wrongAnswer = etWrongAnswer.getText().toString().trim();

            if (correctAnswer.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đáp án đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (wrongAnswer.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đáp án sai", Toast.LENGTH_SHORT).show();
                return;
            }

            quiz.setCorrectAnswer(correctAnswer);
            quiz.setWrongAnswer(wrongAnswer);

            boolean success = quizDao.updateQuiz(quiz, selectedImageUri);

            if (success) {
                Toast.makeText(this, "Cập nhật quiz thành công", Toast.LENGTH_SHORT).show();
                loadQuizData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Cập nhật quiz thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onQuizClick(Quiz quiz) {
        showEditQuizDialog(quiz);
    }

    @Override
    public void onQuizDeleteClick(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa quiz này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = quizDao.deleteQuiz(quiz.getId());
            if (success) {
                Toast.makeText(this, "Xóa quiz thành công", Toast.LENGTH_SHORT).show();
                loadQuizData();
            } else {
                Toast.makeText(this, "Xóa quiz thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
