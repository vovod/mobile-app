package com.nhom13.learningenglishapp.activity.admin;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.adapters.UserAdapter;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.User;

import java.util.ArrayList;
import java.util.List;

public class ManageUserActivity extends AppCompatActivity implements UserAdapter.OnUserDeleteListener {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private UserDao userDao;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);


        userDao = new UserDao(this);


        userList = new ArrayList<>();


        recyclerView = findViewById(R.id.lv_listUser);
        btnBack = findViewById(R.id.btnBackListUser);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, userList, this);
        recyclerView.setAdapter(userAdapter);


        btnBack.setOnClickListener(v -> finish());


        loadUserData();
    }

    private void loadUserData() {
        userList.clear();
        userList.addAll(userDao.getAllNonAdminUsers());
        userAdapter.notifyDataSetChanged();

        if (userList.isEmpty()) {
            Toast.makeText(this, "Không có người dùng nào", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserDeleted() {

        loadUserData();
    }
}
