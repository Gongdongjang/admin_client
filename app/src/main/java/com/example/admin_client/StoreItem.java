package com.example.admin_client;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

interface storeItemService {
    @GET("md/35")
    Call<ResponseBody> test();

    //@GET("admin/store/items")
    //Call<ResponseBody> items(@Query("store_id") String store_id);

    @GET("admin/store/items/{store_id}")
    Call<ResponseBody> items(@Path("store_id") String store_id);
}

public class StoreItem extends AppCompatActivity {

    //속성연결
    TextView test;

    //변수 선언
    String store_id; //intent값 넣기
    private RecyclerView mRecyclerView;
    private ArrayList<MdDetailInfo> mList;
    private FarmDetailAdapter mStoreDetailAdapter;


    //retrofit연결  .baseUrl("http://3.37.223.12:5000/api/")
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://3.37.223.12:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    storeItemService service = retrofit.create(storeItemService.class);
    JsonParser jsonParser = new JsonParser();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_storeitem);
        setContentView(R.layout.activity_store_detail);
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
                Intent intent = new Intent(StoreItem.this, HomeActivity.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });
    }
    void test1() {

        Call<ResponseBody> call = service.test();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        //test.setText(response.body().string());
                        JsonArray jpArray= (JsonArray) jsonParser.parse(response.body().string());
                        //md정보
                        //JsonArray jpArray = res.get("jp_result").getAsJsonArray();
                        //진행중인 공동구매 md
                        for(int i=0;i<jpArray.size();i++){
                            test.setText(jpArray.get(i).getAsJsonObject().get("md_name").getAsString());

                        }

                        //test.setText("access_token");
                        Log.i("태그", "성공");
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

        Call<ResponseBody> call = service.items(store_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonArray jpArray= (JsonArray) jsonParser.parse(response.body().string());


                        firstInit();

                        //어뎁터 적용
                        mStoreDetailAdapter = new FarmDetailAdapter(mList);
                        mRecyclerView.setAdapter(mStoreDetailAdapter);

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(StoreItem.this, 1, GridLayoutManager.VERTICAL, false);
                        mRecyclerView.setLayoutManager(gridLayoutManager);

                        //String s="";
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


                        mStoreDetailAdapter.setOnItemClickListener(
                                new FarmDetailAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int pos) {
                                        Intent intent = new Intent(StoreItem.this, MDDetailActivity.class);

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