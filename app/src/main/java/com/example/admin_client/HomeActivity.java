package com.example.admin_client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.JsonParser;

import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String store_id="47";
        //mContext = this;

        //버튼 클릭 이벤트
        ImageButton orderBtn = (ImageButton) findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StoreItem.class);
                intent.putExtra("store_id",store_id);
                startActivity(intent);
            }
        });
        ImageButton pickupBtn = (ImageButton) findViewById(R.id.pickupBtn);
        pickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pickupActivity.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });
        ImageView myPage = (ImageView) findViewById(R.id.profile);
        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mypage.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });
    }


}
