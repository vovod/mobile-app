package com.nhom13.learningenglishapp.activity.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.LoginActivity;
import com.nhom13.learningenglishapp.adapters.UserVideoAdapter;
import com.nhom13.learningenglishapp.database.dao.VideoDao;
import com.nhom13.learningenglishapp.database.models.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity implements UserVideoAdapter.OnVideoClickListener {

    private RecyclerView recyclerView;
    private UserVideoAdapter videoAdapter;
    private List<Video> videoList;
    private VideoDao videoDao;
    private ImageButton btnBack, btnSetting;
    private String username;
    private int score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);


        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }
        if (getIntent().hasExtra("score")) {
            score = getIntent().getIntExtra("score", 0);
        }


        videoDao = new VideoDao(this);


        videoList = new ArrayList<>();


        recyclerView = findViewById(R.id.rcvVideo);
        btnBack = findViewById(R.id.btnBack);
        btnSetting = findViewById(R.id.btnSetting);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new UserVideoAdapter(this, videoList, this);
        recyclerView.setAdapter(videoAdapter);


        loadVideoData();


        btnBack.setOnClickListener(v -> {
            // Quay về màn hình chính
            Intent intent = new Intent(VideoListActivity.this, UserHomePageActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        });

        btnSetting.setOnClickListener(v -> {
            showSettingDialog();
        });
    }

    private void loadVideoData() {
        videoList.clear();
        videoList.addAll(videoDao.getAllVideos());
        videoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onVideoClick(Video video) {

        Intent intent = new Intent(VideoListActivity.this, PlayVideoActivity.class);
        intent.putExtra("video", video);
        startActivity(intent);
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

            Intent intent = new Intent(VideoListActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(VideoListActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }
}
