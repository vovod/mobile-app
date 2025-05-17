package com.nhom13.learningenglishapp.activity.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.adapters.VideoAdapter;
import com.nhom13.learningenglishapp.database.dao.VideoDao;
import com.nhom13.learningenglishapp.database.models.Video;

import java.util.ArrayList;
import java.util.List;

public class ManageVideoActivity extends AppCompatActivity implements VideoAdapter.OnVideoClickListener {

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<Video> videoList;
    private VideoDao videoDao;
    private ImageButton btnBack, btnAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_video);


        videoDao = new VideoDao(this);


        videoList = new ArrayList<>();


        recyclerView = findViewById(R.id.rcvVideo);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAddVideo);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this, videoList, this);
        recyclerView.setAdapter(videoAdapter);


        loadVideoData();


        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnAdd.setOnClickListener(v -> {
            showAddVideoDialog();
        });
    }

    private void loadVideoData() {
        videoList.clear();
        videoList.addAll(videoDao.getAllVideos());
        videoAdapter.notifyDataSetChanged();
    }

    private void showAddVideoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_video, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etThumbnail = dialogView.findViewById(R.id.etThumbnail);
        EditText etVideoUrl = dialogView.findViewById(R.id.etVideoUrl);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String thumbnail = etThumbnail.getText().toString().trim();
            String videoUrl = etVideoUrl.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }

            if (thumbnail.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập URL thumbnail", Toast.LENGTH_SHORT).show();
                return;
            }

            if (videoUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập URL video", Toast.LENGTH_SHORT).show();
                return;
            }

            Video video = new Video(title, thumbnail, videoUrl);
            boolean success = videoDao.insertVideo(video);

            if (success) {
                Toast.makeText(this, "Thêm video thành công", Toast.LENGTH_SHORT).show();
                loadVideoData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Thêm video thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void showEditVideoDialog(Video video) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_video, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etThumbnail = dialogView.findViewById(R.id.etThumbnail);
        EditText etVideoUrl = dialogView.findViewById(R.id.etVideoUrl);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);


        etTitle.setText(video.getTitle());
        etThumbnail.setText(video.getThumbnailUrl());
        etVideoUrl.setText(video.getVideoUrl());

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String thumbnail = etThumbnail.getText().toString().trim();
            String videoUrl = etVideoUrl.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }

            if (thumbnail.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập URL thumbnail", Toast.LENGTH_SHORT).show();
                return;
            }

            if (videoUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập URL video", Toast.LENGTH_SHORT).show();
                return;
            }

            video.setTitle(title);
            video.setThumbnailUrl(thumbnail);
            video.setVideoUrl(videoUrl);

            boolean success = videoDao.updateVideo(video);

            if (success) {
                Toast.makeText(this, "Cập nhật video thành công", Toast.LENGTH_SHORT).show();
                loadVideoData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Cập nhật video thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onVideoClick(Video video) {
        showEditVideoDialog(video);
    }

    @Override
    public void onVideoDeleteClick(Video video) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa video này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = videoDao.deleteVideo(video.getId());
            if (success) {
                Toast.makeText(this, "Xóa video thành công", Toast.LENGTH_SHORT).show();
                loadVideoData();
            } else {
                Toast.makeText(this, "Xóa video thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
