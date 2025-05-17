package com.nhom13.learningenglishapp.activity.admin;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import java.util.Random;

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


        quizDao = new QuizDao(this);


        quizList = new ArrayList<>();


        recyclerView = findViewById(R.id.rcvQuiz);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAddQuiz);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(this, quizList, this);
        recyclerView.setAdapter(quizAdapter);


        loadQuizData();


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
        View dialogView = inflater.inflate(R.layout.dialog_add_question, null);
        builder.setView(dialogView);


        Spinner spinnerQuestionType = dialogView.findViewById(R.id.spinnerQuestionType);
        LinearLayout layoutTrueFalse = dialogView.findViewById(R.id.layoutTrueFalse);
        LinearLayout layoutGuessImage = dialogView.findViewById(R.id.layoutGuessImage);
        Button buttonSaveQuestion = dialogView.findViewById(R.id.buttonSaveQuestion);


        EditText editTextTrueFalseQuestion = dialogView.findViewById(R.id.editTextTrueFalseQuestion);
        ImageView imageTrueFalse = dialogView.findViewById(R.id.imageTrueFalse);
        Button buttonAddImageTrueFalse = dialogView.findViewById(R.id.buttonAddImageTrueFalse);
        RadioButton radioTrue = dialogView.findViewById(R.id.radioTrue);
        RadioButton radioFalse = dialogView.findViewById(R.id.radioFalse);


        ImageView imageGuess = dialogView.findViewById(R.id.imageGuess);
        Button buttonAddImageGuess = dialogView.findViewById(R.id.buttonAddImageGuess);
        EditText answer1 = dialogView.findViewById(R.id.answer1);
        EditText answer2 = dialogView.findViewById(R.id.answer2);
        EditText answer3 = dialogView.findViewById(R.id.answer3);
        EditText answer4 = dialogView.findViewById(R.id.answer4);
        RadioButton radio1 = dialogView.findViewById(R.id.radio1);
        RadioButton radio2 = dialogView.findViewById(R.id.radio2);
        RadioButton radio3 = dialogView.findViewById(R.id.radio3);
        RadioButton radio4 = dialogView.findViewById(R.id.radio4);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.question_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestionType.setAdapter(adapter);


        spinnerQuestionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) { // Đúng/Sai
                    layoutTrueFalse.setVisibility(View.VISIBLE);
                    layoutGuessImage.setVisibility(View.GONE);
                    selectedImageView = imageTrueFalse;
                } else {
                    layoutTrueFalse.setVisibility(View.GONE);
                    layoutGuessImage.setVisibility(View.VISIBLE);
                    selectedImageView = imageGuess;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        layoutTrueFalse.setVisibility(View.VISIBLE);
        layoutGuessImage.setVisibility(View.GONE);
        selectedImageView = imageTrueFalse;
        selectedImageUri = null;

        AlertDialog dialog = builder.create();
        dialog.show();


        buttonAddImageTrueFalse.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });


        buttonAddImageGuess.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });


        buttonSaveQuestion.setOnClickListener(v -> {
            int selectedQuestionType = spinnerQuestionType.getSelectedItemPosition();

            if (selectedImageUri == null) {
                Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedQuestionType == 0) {
                String question = editTextTrueFalseQuestion.getText().toString().trim();
                if (question.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!radioTrue.isChecked() && !radioFalse.isChecked()) {
                    Toast.makeText(this, "Vui lòng chọn đáp án đúng", Toast.LENGTH_SHORT).show();
                    return;
                }

                String correctAnswer = radioTrue.isChecked() ? "true" : "false";
                String wrongAnswer = radioTrue.isChecked() ? "false" : "true";

                Quiz quiz = new Quiz(correctAnswer, wrongAnswer, "");
                boolean success = quizDao.insertQuiz(quiz, selectedImageUri);

                if (success) {
                    Toast.makeText(this, "Thêm quiz thành công", Toast.LENGTH_SHORT).show();
                    loadQuizData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Thêm quiz thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                String ans1 = answer1.getText().toString().trim();
                String ans2 = answer2.getText().toString().trim();


                if (ans1.isEmpty() || ans2.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ các đáp án", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!radio1.isChecked() && !radio2.isChecked()) {
                    Toast.makeText(this, "Vui lòng chọn đáp án đúng", Toast.LENGTH_SHORT).show();
                    return;
                }

                String correctAnswer = "";
                if (radio1.isChecked()) correctAnswer = ans1;
                else if (radio2.isChecked()) correctAnswer = ans2;


                String wrongAnswer = radio1.isChecked() ? ans2 : ans1;

                Quiz quiz = new Quiz(correctAnswer, wrongAnswer, "");
                boolean success = quizDao.insertQuiz(quiz, selectedImageUri);

                if (success) {
                    Toast.makeText(this, "Thêm quiz thành công", Toast.LENGTH_SHORT).show();
                    loadQuizData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Thêm quiz thất bại", Toast.LENGTH_SHORT).show();
                }
            }
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


        etCorrectAnswer.setText(quiz.getCorrectAnswer());
        etWrongAnswer.setText(quiz.getWrongAnswer());


        if (quiz.getImagePath() != null && !quiz.getImagePath().isEmpty()) {
            imgQuiz.setImageURI(Uri.parse(quiz.getImagePath()));
        }


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
