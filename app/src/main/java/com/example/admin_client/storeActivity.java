package com.example.admin_client;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

interface StoreService {
    @POST("admin/storeDetail/")
    Call<ResponseBody> StoreDetail(@Body JsonObject body);
}

public class storeActivity  extends AppCompatActivity {

    StoreService service;
    JsonParser jsonParser;
    JsonObject res;
    JsonArray storeArray, jpArray,rvwArray, pu_start, pu_end, storeDate, dDay;
    String store_name;
    Context mContext;
    String store_id;
    private RecyclerView mRecyclerView;
    private ArrayList<MdDetailInfo> mList;
    private FarmDetailAdapter mStoreDetailAdapter;

    String user_id, standard_address, day;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storepage);

        mContext = this;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://3.37.223.12:5000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(StoreService.class);
        jsonParser = new JsonParser();

        Intent intent;
        intent=getIntent();
        store_id=intent.getStringExtra("store_id");
        //standard_address=intent.getStringExtra("standard_address");


        ImageView StoreMainImg = findViewById(R.id.StoreMainImg);
        ImageView StoreStoryImg = findViewById(R.id.StoreStoryImg);
        TextView StoreName = (TextView) findViewById(R.id.StoreName);
        TextView StoreExplain = (TextView) findViewById(R.id.StoreExplain);
        TextView StoreLocation = (TextView) findViewById(R.id.StoreLocation);
        TextView StoreStart = (TextView) findViewById(R.id.StoreStart);
        TextView StoreEnd = (TextView) findViewById(R.id.StoreEnd);
        TextView StoreWeek = (TextView) findViewById(R.id.StoreWeek);
        TextView StoreCall = (TextView) findViewById(R.id.StoreCall);
        TextView StoreJointPurchaseCount = (TextView) findViewById(R.id.StoreJointPurchaseCount);



        JsonObject body = new JsonObject();
        body.addProperty("id", store_id);
        Call<ResponseBody> call = service.StoreDetail(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    try {
                        res= (JsonObject) jsonParser.parse(response.body().string());

                        //store정보
                        storeArray= res.get("store_result").getAsJsonArray();

                        //md정보
                        jpArray = res.get("jp_result").getAsJsonArray();
                        pu_start = res.get("pu_start").getAsJsonArray();
                        pu_end = res.get("pu_end").getAsJsonArray();
                        dDay = res.get("dDay").getAsJsonArray();


                        //영업 or 휴무일 정보
                        storeDate = res.get("store_date").getAsJsonArray();
                        day = res.get("day").getAsString();


                        Glide.with(storeActivity.this)
                                .load("https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + storeArray.get(0).getAsJsonObject().get("store_mainImg").getAsString())
                                .into(StoreMainImg);
                        Glide.with(storeActivity.this)
                                .load("https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + storeArray.get(0).getAsJsonObject().get("store_detailImg").getAsString())
                                .into(StoreStoryImg);

                        StoreName.setText(storeArray.get(0).getAsJsonObject().get("store_name").getAsString());
                        StoreExplain.setText(storeArray.get(0).getAsJsonObject().get("store_info").getAsString());
                        StoreLocation.setText(storeArray.get(0).getAsJsonObject().get("store_loc").getAsString());


                        if(day.equals("일")){
                            StoreStart.setText(storeDate.get(0).getAsJsonObject().get("hours_sun1").getAsString());
                            StoreEnd.setText(storeDate.get(0).getAsJsonObject().get("hours_sun2").getAsString());
                        }
                        else if(day.equals("월")){
                            StoreStart.setText(storeDate.get(0).getAsJsonObject().get("hours_mon1").getAsString());
                            StoreEnd.setText(storeDate.get(0).getAsJsonObject().get("hours_mon2").getAsString());
                        }
                        else if(day.equals("화")){
                            StoreStart.setText(storeDate.get(0).getAsJsonObject().get("hours_tue1").getAsString());
                            StoreEnd.setText(storeDate.get(0).getAsJsonObject().get("hours_tue2").getAsString());
                        }
                        else if(day.equals("수")){
                            StoreStart.setText(storeDate.get(0).getAsJsonObject().get("hours_wed1").getAsString());
                            StoreEnd.setText(storeDate.get(0).getAsJsonObject().get("hours_wed2").getAsString());
                        }
                        else if(day.equals("목")){
                            StoreStart.setText(storeDate.get(0).getAsJsonObject().get("hours_thu1").getAsString());
                            StoreEnd.setText(storeDate.get(0).getAsJsonObject().get("hours_thu2").getAsString());
                        }
                        else if(day.equals("금")){
                            StoreStart.setText(storeDate.get(0).getAsJsonObject().get("hours_fri1").getAsString());
                            StoreEnd.setText(storeDate.get(0).getAsJsonObject().get("hours_fri2").getAsString());
                        }
                        else if(day.equals("토")){
                            StoreStart.setText(storeDate.get(0).getAsJsonObject().get("hours_sat1").getAsString());
                            StoreEnd.setText(storeDate.get(0).getAsJsonObject().get("hours_sat2").getAsString());
                        }

                        // 휴무일 다시 처리 -> 어떻게 출력되는지 확인
                        StoreWeek.setText(storeDate.get(0).getAsJsonObject().get("hours_week").getAsString());
                        StoreCall.setText(storeArray.get(0).getAsJsonObject().get("store_phone").getAsString());
                        StoreJointPurchaseCount.setText(String.valueOf(jpArray.size()));

                        firstInit();

                        //어뎁터 적용
                        mStoreDetailAdapter = new FarmDetailAdapter(mList);
                        mRecyclerView.setAdapter(mStoreDetailAdapter);

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(storeActivity.this, 2, GridLayoutManager.VERTICAL, false);
                        mRecyclerView.setLayoutManager(gridLayoutManager);


                        //진행중인 공동구매 md
                        for(int i=0;i<jpArray.size();i++){
                            //s=s+jpArray.get(i).getAsJsonObject().get("md_name").getAsString();

                            addStoreJointPurchase(
                                    "https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + jpArray.get(i).getAsJsonObject().get("mdimg_thumbnail").getAsString(),

                                    jpArray.get(i).getAsJsonObject().get("md_name").getAsString(),
                                    jpArray.get(i).getAsJsonObject().get("farm_name").getAsString(),
                                    jpArray.get(i).getAsJsonObject().get("pu_start").getAsString(),
                                    jpArray.get(i).getAsJsonObject().get("md_id").getAsString()
                            );
                        }

//



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(storeActivity.this, "스토어 세부 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("세부", t.getMessage());
            }

        });

    }

    public void firstInit(){
        mRecyclerView = findViewById(R.id.FarmPurchaseView);
        mList = new ArrayList<>();
    }

    public void addStoreJointPurchase(String prodImgName, String prodName, String farmName,  String puTime,String id){
        MdDetailInfo mdDetail = new MdDetailInfo();

        mdDetail.setProdImg(prodImgName);
        mdDetail.setProdName(prodName);
        mdDetail.setStoreName(farmName);

        mdDetail.setPuTime(puTime);
        mdDetail.setMdId(id);

        mList.add(mdDetail);
    }


}
