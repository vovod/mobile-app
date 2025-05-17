package com.nhom13.learningenglishapp.activity.admin;

import android.app.AlertDialog;
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
import com.nhom13.learningenglishapp.adapters.ChapterAdapter;
import com.nhom13.learningenglishapp.database.dao.ChapterDao;
import com.nhom13.learningenglishapp.database.models.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ManageChapterActivity extends AppCompatActivity implements ChapterAdapter.OnChapterListener {

    private RecyclerView recyclerView;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList;
    private ChapterDao chapterDao;
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
        setContentView(R.layout.activity_manage_chapter);


        chapterDao = new ChapterDao(this);


        chapterList = new ArrayList<>();


        recyclerView = findViewById(R.id.rcvChapter);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAddChapter);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chapterAdapter = new ChapterAdapter(this, chapterList, this);
        recyclerView.setAdapter(chapterAdapter);


        loadChapterData();


        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnAdd.setOnClickListener(v -> {
            showAddChapterDialog();
        });
    }

    private void loadChapterData() {
        chapterList.clear();
        chapterList.addAll(chapterDao.getAllChapters());
        chapterAdapter.notifyDataSetChanged();
    }

    private void showAddChapterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_chapter, null);
        builder.setView(dialogView);

        EditText etChapterId = dialogView.findViewById(R.id.etChapterId);
        EditText etChapterName = dialogView.findViewById(R.id.etChapterName);
        ImageView imgChapter = dialogView.findViewById(R.id.imgChapter);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);


        selectedImageView = imgChapter;
        selectedImageUri = null;

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String chapterIdStr = etChapterId.getText().toString().trim();
            String chapterName = etChapterName.getText().toString().trim();

            if (chapterIdStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập ID chapter", Toast.LENGTH_SHORT).show();
                return;
            }

            if (chapterName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên chapter", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int chapterId = Integer.parseInt(chapterIdStr);
                Chapter chapter = new Chapter(chapterId, chapterName, "");
                boolean success = chapterDao.insertChapter(chapter, selectedImageUri);

                if (success) {
                    Toast.makeText(this, "Thêm chapter thành công", Toast.LENGTH_SHORT).show();
                    loadChapterData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Thêm chapter thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID chapter phải là số", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void showEditChapterDialog(Chapter chapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_chapter, null);
        builder.setView(dialogView);

        EditText etChapterId = dialogView.findViewById(R.id.etChapterId);
        EditText etChapterName = dialogView.findViewById(R.id.etChapterName);
        ImageView imgChapter = dialogView.findViewById(R.id.imgChapter);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);


        etChapterId.setText(String.valueOf(chapter.getId()));
        etChapterId.setEnabled(false);
        etChapterName.setText(chapter.getName());


        if (chapter.getImagePath() != null && !chapter.getImagePath().isEmpty()) {
            imgChapter.setImageURI(Uri.parse(chapter.getImagePath()));
        }


        selectedImageView = imgChapter;
        selectedImageUri = null;

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String chapterName = etChapterName.getText().toString().trim();

            if (chapterName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên chapter", Toast.LENGTH_SHORT).show();
                return;
            }

            chapter.setName(chapterName);
            boolean success = chapterDao.updateChapter(chapter, selectedImageUri);

            if (success) {
                Toast.makeText(this, "Cập nhật chapter thành công", Toast.LENGTH_SHORT).show();
                loadChapterData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Cập nhật chapter thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onChapterClick(Chapter chapter) {
        showEditChapterDialog(chapter);
    }

    @Override
    public void onChapterDeleteClick(Chapter chapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa chapter này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = chapterDao.deleteChapter(chapter.getId());
            if (success) {
                Toast.makeText(this, "Xóa chapter thành công", Toast.LENGTH_SHORT).show();
                loadChapterData();
            } else {
                Toast.makeText(this, "Xóa chapter thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
