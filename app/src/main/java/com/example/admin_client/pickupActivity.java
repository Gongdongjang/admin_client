package com.example.admin_client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
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

public class pickupActivity extends AppCompatActivity {

    //변수 선언
    Activity mActivity;
    String store_id; //intent값 넣기
    private RecyclerView mRecyclerView;
    private ArrayList<MdDetailInfo> mList;
    private PickupDetailAdapter mStoreDetailAdapter;


    //retrofit연결  .baseUrl("http://3.37.223.12:5000/api/")
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://3.37.223.12:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    pickUpService  service = retrofit.create(pickUpService.class);
    JsonParser jsonParser = new JsonParser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickuplist);
        Intent intent;
        intent=getIntent(); //intent 값 받기
        store_id=intent.getStringExtra("store_id");



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
                Intent intent = new Intent(pickupActivity.this, HomeActivity.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });
    }


    void item() {

        Call<ResponseBody> call = service.items(store_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonArray jpArray= (JsonArray) jsonParser.parse(response.body().string());


                        firstInit();

                        //어뎁터 적용
                        mStoreDetailAdapter = new PickupDetailAdapter(mList);
                        mRecyclerView.setAdapter(mStoreDetailAdapter);

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(pickupActivity.this, 1, GridLayoutManager.VERTICAL, false);
                        mRecyclerView.setLayoutManager(gridLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                        //String s="";
                        for(int i=0;i<jpArray.size();i++){
                            //s=s+jpArray.get(i).getAsJsonObject().get("md_name").getAsString();

                            addStoreJointPurchase(
                                    "https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + jpArray.get(i).getAsJsonObject().get("mdimg_thumbnail").getAsString(),
                                    jpArray.get(i).getAsJsonObject().get("stk_confirm").getAsString(),
                                    jpArray.get(i).getAsJsonObject().get("md_name").getAsString(),
                                    jpArray.get(i).getAsJsonObject().get("farm_name").getAsString(),
                                    jpArray.get(i).getAsJsonObject().get("pu_start").getAsString(),
                                    jpArray.get(i).getAsJsonObject().get("md_id").getAsString()
                            );
                        }


                        mStoreDetailAdapter.setOnItemClickListener(
                                new PickupDetailAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int pos) {
                                        Intent intent = new Intent(pickupActivity.this, PickupList.class);
                                        intent.putExtra("store_id", store_id);
                                        intent.putExtra("md_id", mList.get(pos).getMdId());
                                        startActivity(intent);
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
    public void firstInit(){
        mRecyclerView = findViewById(R.id.tabPickupView);
        mList = new ArrayList<>();
    }

    public void addStoreJointPurchase(String prodImgName, String confirm, String prodName, String farmName,  String puTime,String id){
        MdDetailInfo mdDetail = new MdDetailInfo();

        mdDetail.setProdImg(prodImgName);
        mdDetail.setConfirm(confirm);
        mdDetail.setProdName(prodName);
        mdDetail.setStoreName(farmName);

        mdDetail.setPuTime(puTime);
        mdDetail.setMdId(id);

        mList.add(mdDetail);
    }

}
