package com.nhom13.learningenglishapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.adapters.WordAdapter;
import com.nhom13.learningenglishapp.database.dao.ChapterDao;
import com.nhom13.learningenglishapp.database.dao.VocabularyDao;
import com.nhom13.learningenglishapp.database.models.Chapter;
import com.nhom13.learningenglishapp.database.models.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class ManageWordActivity extends AppCompatActivity implements WordAdapter.OnWordListener {
    private RecyclerView recyclerView;
    private WordAdapter wordAdapter;
    private List<Vocabulary> vocabularyList;
    private VocabularyDao vocabularyDao;
    private ChapterDao chapterDao;
    private ImageButton btnBack, btnAddWord;
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quanlytuvung);

        // Khởi tạo các DAO
        vocabularyDao = new VocabularyDao(this);
        chapterDao = new ChapterDao(this);

        // Khởi tạo danh sách từ vựng
        vocabularyList = new ArrayList<>();

        // Ánh xạ các view
        recyclerView = findViewById(R.id.rcv_listTV);
        btnBack = findViewById(R.id.btnBackListWords);
        btnAddWord = findViewById(R.id.btnAddWord);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wordAdapter = new WordAdapter(this, vocabularyList, this);
        recyclerView.setAdapter(wordAdapter);

        // Đăng ký launcher để chọn hình ảnh
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        // Hiển thị hình ảnh đã chọn trong dialog
                        if (selectedImageView != null) {
                            selectedImageView.setImageURI(selectedImageUri);
                        }
                    }
                });

        // Thiết lập sự kiện click cho các nút
        btnBack.setOnClickListener(v -> finish());

        btnAddWord.setOnClickListener(v -> showAddWordDialog());

        // Tải dữ liệu từ vựng
        loadVocabularyData();
    }

    private void loadVocabularyData() {
        vocabularyList.clear();
        vocabularyList.addAll(vocabularyDao.getAllVocabulary());
        wordAdapter.notifyDataSetChanged();
    }

    private ImageView selectedImageView; // Biến tạm để lưu trữ ImageView trong dialog

    private void showAddWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_word, null);
        builder.setView(dialogView);

        EditText etWord = dialogView.findViewById(R.id.etWord);
        Spinner spinnerChapter = dialogView.findViewById(R.id.spinnerChapter);
        ImageView imgWord = dialogView.findViewById(R.id.imgWord);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Lưu trữ ImageView để cập nhật sau khi chọn hình ảnh
        selectedImageView = imgWord;
        selectedImageUri = null;

        // Tải danh sách chương
        List<Chapter> chapters = chapterDao.getAllChapters();
        ArrayAdapter<Chapter> chapterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, chapters);
        chapterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChapter.setAdapter(chapterAdapter);

        AlertDialog dialog = builder.create();

        btnSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String word = etWord.getText().toString().trim();
            if (word.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập từ vựng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinnerChapter.getSelectedItem() == null) {
                Toast.makeText(this, "Vui lòng chọn chương", Toast.LENGTH_SHORT).show();
                return;
            }

            Chapter selectedChapter = (Chapter) spinnerChapter.getSelectedItem();
            Vocabulary vocabulary = new Vocabulary(word, selectedChapter.getId(), "");

            boolean success = vocabularyDao.insertVocabulary(vocabulary, selectedImageUri);
            if (success) {
                Toast.makeText(this, "Thêm từ vựng thành công", Toast.LENGTH_SHORT).show();
                loadVocabularyData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Thêm từ vựng thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showEditWordDialog(Vocabulary vocabulary) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_word, null);
        builder.setView(dialogView);

        EditText etWord = dialogView.findViewById(R.id.etWord);
        Spinner spinnerChapter = dialogView.findViewById(R.id.spinnerChapter);
        ImageView imgWord = dialogView.findViewById(R.id.imgWord);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Hiển thị thông tin từ vựng hiện tại
        etWord.setText(vocabulary.getWord());

        // Hiển thị hình ảnh nếu có
        if (vocabulary.getImagePath() != null && !vocabulary.getImagePath().isEmpty()) {
            imgWord.setImageURI(Uri.parse(vocabulary.getImagePath()));
        }

        // Lưu trữ ImageView để cập nhật sau khi chọn hình ảnh
        selectedImageView = imgWord;
        selectedImageUri = null;

        // Tải danh sách chương
        List<Chapter> chapters = chapterDao.getAllChapters();
        ArrayAdapter<Chapter> chapterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, chapters);
        chapterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChapter.setAdapter(chapterAdapter);

        // Chọn chương hiện tại
        for (int i = 0; i < chapters.size(); i++) {
            if (chapters.get(i).getId() == vocabulary.getChapterId()) {
                spinnerChapter.setSelection(i);
                break;
            }
        }

        AlertDialog dialog = builder.create();

        btnSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String word = etWord.getText().toString().trim();
            if (word.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập từ vựng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinnerChapter.getSelectedItem() == null) {
                Toast.makeText(this, "Vui lòng chọn chương", Toast.LENGTH_SHORT).show();
                return;
            }

            Chapter selectedChapter = (Chapter) spinnerChapter.getSelectedItem();
            vocabulary.setWord(word);
            vocabulary.setChapterId(selectedChapter.getId());

            boolean success = vocabularyDao.updateVocabulary(vocabulary, selectedImageUri);
            if (success) {
                Toast.makeText(this, "Cập nhật từ vựng thành công", Toast.LENGTH_SHORT).show();
                loadVocabularyData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Cập nhật từ vựng thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmDialog(int position) {
        Vocabulary vocabulary = wordAdapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa từ vựng \"" + vocabulary.getWord() + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = vocabularyDao.deleteVocabulary(vocabulary.getId());
            if (success) {
                Toast.makeText(this, "Xóa từ vựng thành công", Toast.LENGTH_SHORT).show();
                loadVocabularyData();
            } else {
                Toast.makeText(this, "Xóa từ vựng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public void onEditClick(int position) {
        Vocabulary vocabulary = wordAdapter.getItem(position);
        showEditWordDialog(vocabulary);
    }

    @Override
    public void onDeleteClick(int position) {
        showDeleteConfirmDialog(position);
    }
}
