package com.example.admin_client;

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

interface deliveryService {
    @GET("md/35")
    Call<ResponseBody> test();

    //@GET("admin/store/items")
    //Call<ResponseBody> items(@Query("store_id") String store_id);

    @GET("admin/store/items/{store_id}")
    Call<ResponseBody> items(@Path("store_id") String store_id);
}
public class tabDelivery extends Fragment {

    //변수 선언
    String store_id="48"; //intent값 넣기
    private RecyclerView mRecyclerView;
    private ArrayList<MdDetailInfo> mList;
    private PickupDetailAdapter mStoreDetailAdapter;


    //retrofit연결  .baseUrl("http://3.37.223.12:5000/api/")
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://3.37.223.12:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    deliveryService  service = retrofit.create(deliveryService.class);
    JsonParser jsonParser = new JsonParser();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_delivery_view,container,false);
        super.onCreate(savedInstanceState);
        mRecyclerView = v.findViewById(R.id.tabDeliveryView);
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
