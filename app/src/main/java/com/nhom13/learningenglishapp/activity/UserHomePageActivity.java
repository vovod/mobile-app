package com.nhom13.learningenglishapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;

public class UserHomePageActivity extends AppCompatActivity {

    LinearLayout videoTiengAnh;
    LinearLayout choiGame;
    LinearLayout tiengAnh;
    String username;
    ImageButton Setting;
    int score;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);

        videoTiengAnh = findViewById(R.id.item1);
        choiGame = findViewById(R.id.item2);
        tiengAnh = findViewById(R.id.item3);
        Setting = findViewById(R.id.btnsetting);

        videoTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to videoTiengAnh");
            }
        });

        choiGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to choiGame");
            }
        });

        tiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to tiengAnh");
            }
        });
    }


}
