package com.example.admin_client;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface PickupListService {

    @GET("admin/{md_id}") //md
    Call<ResponseBody> md(@Path("md_id") String md_id);

    @GET("admin/pickup/{md_id}") //order
    Call<ResponseBody> items(@Path("md_id") String md_id);

    @POST("admin/pickup/user/{order_id}") //pickup
    Call<ResponseBody> pickup(@Path("order_id") String order_id);
}
public class PickupList  extends AppCompatActivity {

        //속성연결
        TextView farmName,comp,goal,mdName;
        ImageView mdImg;
        Dialog dilaog01;
        //변수 선언
        String store_id;
        String md_id;
        private RecyclerView mRecyclerView;
        private ArrayList<orderInfo> mList;
        private orderAdapter mStoreDetailAdapter;


        //retrofit연결  .baseUrl("http://3.37.223.12:5000/api/")
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://3.37.223.12:5000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PickupListService service = retrofit.create(PickupListService.class);
        JsonParser jsonParser = new JsonParser();





        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                //setContentView(R.layout.activity_storeitem);
                setContentView(R.layout.activity_pickup_check);

                mdImg = findViewById(R.id.mdImg);
                farmName = findViewById(R.id.farmName);
                mdName = findViewById(R.id.mdName);
                goal=  findViewById(R.id.goal);
                comp= findViewById(R.id.comp);
                dilaog01 = new Dialog(PickupList.this);       // Dialog 초기화
                dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
                dilaog01.setContentView(R.layout.pickup_dialog);
            Intent intent;
            intent=getIntent(); //intent 값 받기
            store_id=intent.getStringExtra("store_id");
            md_id=intent.getStringExtra("md_id");
                md();
                item();

            ImageView myPage = (ImageView) findViewById(R.id.profile);
            myPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Mypage.class);
                    intent.putExtra("store_id", store_id);
                    startActivity(intent);
                }
            });
            //상단바 뒤로가기
            ImageView gotoBack = findViewById(R.id.gotoBack);
            gotoBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PickupList.this, pickupActivity.class);
                    intent.putExtra("store_id", store_id);
                    startActivity(intent);
                }
            });

        }

        void md() {

        Call<ResponseBody> call = service.md(md_id);
        call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                try {
                JsonArray jpArray= (JsonArray) jsonParser.parse(response.body().string());

                String imageUrl ="https://ggdjang.s3.ap-northeast-2.amazonaws.com/"+jpArray.get(0).getAsJsonObject().get("mdimg_thumbnail").getAsString();
                Glide.with(getApplicationContext()).load(imageUrl).into(mdImg);
                farmName.setText(jpArray.get(0).getAsJsonObject().get("farm_name").getAsString());
                mdName.setText(jpArray.get(0).getAsJsonObject().get("md_name").getAsString());
                goal.setText(jpArray.get(0).getAsJsonObject().get("stk_total").getAsString());
                comp.setText(jpArray.get(0).getAsJsonObject().get("pay_comp").getAsString());
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
        void item() {

        Call<ResponseBody> call = service.items(md_id);
        call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
             if (response.isSuccessful()) {
                 try {
                      JsonArray jpArray= (JsonArray) jsonParser.parse(response.body().string());
                      firstInit();

                      //어뎁터 적용
                      mStoreDetailAdapter = new orderAdapter(mList);
                      mRecyclerView.setAdapter(mStoreDetailAdapter);

                      GridLayoutManager gridLayoutManager = new GridLayoutManager(PickupList.this, 1, GridLayoutManager.VERTICAL, false);
                      mRecyclerView.setLayoutManager(gridLayoutManager);

                      //String s="";
                      for(int i=0;i<jpArray.size();i++){
                         addOrder(
                            jpArray.get(i).getAsJsonObject().get("order_pu_date").getAsString(),
                            jpArray.get(i).getAsJsonObject().get("order_pu_time").getAsString(),
                            jpArray.get(i).getAsJsonObject().get("order_name").getAsString(),
                            jpArray.get(i).getAsJsonObject().get("md_name").getAsString(),
                            jpArray.get(i).getAsJsonObject().get("order_md_status").getAsString(),
                            jpArray.get(i).getAsJsonObject().get("order_select_qty").getAsString(),
                            jpArray.get(i).getAsJsonObject().get("order_id").getAsString()
                         );
                      }


                      mStoreDetailAdapter.setOnItemClickListener(
                           new orderAdapter.OnItemClickListener() {
                           @Override
                           public void onItemClick(View v, int pos) {
                               showDialog01(mList.get(pos).getName(),mList.get(pos).getDate(),mList.get(pos).getMdName(),mList.get(pos).getDone(),mList.get(pos).getQty(),mList.get(pos).getOrderId());
                                 //Intent intent = new Intent(PickupList.this, HomeActivity.class);

                                 //intent.putExtra("order_id", mList.get(pos).getOrderId());
                                 //startActivity(intent);
                            }
                           }
                      );


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
        void pickup(String order_id) {

        Call<ResponseBody> call = service.pickup(order_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

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
        public void firstInit(){
                mRecyclerView = findViewById(R.id.pickupListView);
                mList = new ArrayList<>();
        }

        public void addOrder(String date,String time,String name, String mdName, String isDone, String qty ,String id){
            orderInfo detail = new orderInfo();

            detail.setDate(date);
            detail.setTime(time);
            detail.setName(name);
            detail.setMdName(mdName);

            if(isDone.equals("0"))
               detail.setDone("픽업 진행전");
            else
                detail.setDone("픽업 완료");
            detail.setQty(qty);
            detail.setOrderId(id);

            mList.add(detail);
        }
    public void showDialog01(String name,String time, String mdName, String isDone, String qty, String id){
        dilaog01.show(); // 다이얼로그 띄우기
        TextView dName = dilaog01.findViewById(R.id.dName);
        TextView dTime = dilaog01.findViewById(R.id.dTime);
        TextView dName2 = dilaog01.findViewById(R.id.dName2);
        TextView dMd = dilaog01.findViewById(R.id.dMd);
        TextView dDone = dilaog01.findViewById(R.id.dDone);
        TextView dCount = dilaog01.findViewById(R.id.dCount);

        dName.setText(name);
        dTime.setText(time);
        dName2.setText(name);
        dMd.setText(mdName);
        dDone.setText(isDone);
        dCount.setText(qty);

        // 아니오 버튼
        Button noBtn = dilaog01.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                // 원하는 기능 구현
               dilaog01.dismiss(); // 다이얼로그 닫기
            }
        });
        // 네 버튼
        dilaog01.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                pickup(id);

                Intent intent = (PickupList.this).getIntent();
                (PickupList.this).finish(); //현재 액티비티 종료 실시
                (PickupList.this).overridePendingTransition(0, 0); //효과 없애기
                (PickupList.this).startActivity(intent); //현재 액티비티 재실행 실시
                (PickupList.this).overridePendingTransition(0, 0);
                dilaog01.dismiss();
            }
        });
    }
}

