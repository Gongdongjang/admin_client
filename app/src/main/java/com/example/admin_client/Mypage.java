package com.example.admin_client;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface MypageService {
    @GET("admin/store/{store_id}")
    Call<ResponseBody> store(@Path("store_id") String store_id);

}

public class Mypage extends AppCompatActivity {

    //속성연결
    TextView name,info,time,breakTime,phone,location;

    //변수 선언
    String store_id; //intent값 넣기

    //retrofit연결  .baseUrl("http://3.37.223.12:5000/api/")
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://3.37.223.12:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    MypageService service = retrofit.create(MypageService.class);
    JsonParser jsonParser = new JsonParser();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_storeitem);
        setContentView(R.layout.activity_store);

        name = findViewById(R.id.sName);
        info = findViewById(R.id.sInfo);
        time = findViewById(R.id.sTime);
        breakTime = findViewById(R.id.sBreak);
        phone = findViewById(R.id.sPhone);
        location = findViewById(R.id.sLocation);
        Intent intent;
        intent=getIntent(); //intent 값 받기
        store_id=intent.getStringExtra("store_id");
        store();
        Button btn_store = (Button) findViewById(R.id.btn_storePage);
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), storeActivity.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });
        //상단바 뒤로가기
        ImageView gotoBack = findViewById(R.id.gotoBack);
        gotoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mypage.this, HomeActivity.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });

    }


    void store() {

        Call<ResponseBody> call = service.store(store_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonArray jpArray= (JsonArray) jsonParser.parse(response.body().string());

                        name.setText(jpArray.get(0).getAsJsonObject().get("store_name").getAsString());
                        info.setText(jpArray.get(0).getAsJsonObject().get("store_info").getAsString());

                        breakTime.setText(jpArray.get(0).getAsJsonObject().get("hours_week").getAsString());
                        phone.setText(jpArray.get(0).getAsJsonObject().get("store_phone").getAsString());
                        location.setText(jpArray.get(0).getAsJsonObject().get("store_loc").getAsString());

                        String s =jpArray.get(0).getAsJsonObject().get("hours_mon1").getAsString()+"~"+
                                jpArray.get(0).getAsJsonObject().get("hours_mon2").getAsString()+"  "+
                                jpArray.get(0).getAsJsonObject().get("hours_tue1").getAsString()+"~"+
                                jpArray.get(0).getAsJsonObject().get("hours_tue2").getAsString()+"  "+
                                jpArray.get(0).getAsJsonObject().get("hours_wed1").getAsString()+"~"+
                                jpArray.get(0).getAsJsonObject().get("hours_wed2").getAsString()+"  "+
                                jpArray.get(0).getAsJsonObject().get("hours_thu1").getAsString()+"~"+
                                jpArray.get(0).getAsJsonObject().get("hours_thu2").getAsString()+"  "+
                                jpArray.get(0).getAsJsonObject().get("hours_fri1").getAsString()+"~"+
                                jpArray.get(0).getAsJsonObject().get("hours_fri2").getAsString()+"  "+
                                jpArray.get(0).getAsJsonObject().get("hours_sat1").getAsString()+"~"+
                                jpArray.get(0).getAsJsonObject().get("hours_sat2").getAsString()+"  "+
                                jpArray.get(0).getAsJsonObject().get("hours_sun1").getAsString()+"~"+
                                jpArray.get(0).getAsJsonObject().get("hours_sun2").getAsString();


                        time.setText(s);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        Log.i("태그", "Fail " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("태그", "onFailure: e " + t.getMessage());
            }
        });

    }

}