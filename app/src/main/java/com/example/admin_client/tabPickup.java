package com.example.admin_client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

interface pickUpService {
    //@GET("admin/store/items")
    //Call<ResponseBody> items(@Query("store_id") String store_id);

    @GET("admin/store/items/{store_id}")
    Call<ResponseBody> items(@Path("store_id") String store_id);
}
public class tabPickup extends Fragment {

    //변수 선언
    Activity mActivity;
    String store_id; //intent값 넣기
    private RecyclerView mRecyclerView;
    private ArrayList<MdDetailInfo> mList;
    private PickupDetailAdapter mStoreDetailAdapter;

//http://172.30.1.22:5000/api/
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

        store_id = this.getArguments().getString("store_id");


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_pickup_view,container,false);
        super.onCreate(savedInstanceState);



        mRecyclerView = v.findViewById(R.id.tabPickupView);
        mRecyclerView.setHasFixedSize(true);
        mList = new ArrayList<>();


        item();
        return v;
    }
    void item() {

        Call<ResponseBody> call = service.items(store_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonArray jpArray= (JsonArray) jsonParser.parse(response.body().string());


                        //firstInit();

                        //어뎁터 적용
                        mStoreDetailAdapter = new PickupDetailAdapter(mList);
                        mRecyclerView.setAdapter(mStoreDetailAdapter);

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
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
                                    jpArray.get(i).getAsJsonObject().get("pu_start").getAsString()
                            );
                        }


                        mStoreDetailAdapter.setOnItemClickListener(
                                new PickupDetailAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int pos) {
                                        Intent intent = new Intent(getActivity(), PickupList.class);
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


    public void addStoreJointPurchase(String prodImgName, String confirm, String prodName, String farmName,  String puTime){
        MdDetailInfo mdDetail = new MdDetailInfo();

        mdDetail.setProdImg(prodImgName);
        mdDetail.setConfirm(confirm);
        mdDetail.setProdName(prodName);
        mdDetail.setStoreName(farmName);

        mdDetail.setPuTime(puTime);

        mList.add(mdDetail);
    }

}
