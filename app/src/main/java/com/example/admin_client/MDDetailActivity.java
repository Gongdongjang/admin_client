package com.example.admin_client;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import android.location.Address;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface MDDetailService {

    @GET("admin/{md_id}") //md
    Call<ResponseBody> md(@Path("md_id") String md_id);


}
public class MDDetailActivity extends AppCompatActivity {

    //속성연결
    TextView test;
    String md_id,md_status,pu_date,store_id;
    //변수 선언
    TextView farmName,comp,goal,mdName;
    ImageView mdImg;
    ImageView order_status1,order_status2,order_status3,order_status4,order_status5,order_status6;
    TextView order_status,txt_order_status1,txt_order_status2,txt_order_status3,txt_order_status4,txt_order_status5,txt_order_status6;

    //retrofit연결  .baseUrl("http://3.37.223.12:5000/api/")
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://3.37.223.12:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    MDDetailService service = retrofit.create(MDDetailService.class);
    JsonParser jsonParser = new JsonParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_storeitem);
        setContentView(R.layout.activity_item_detail);
        test = findViewById(R.id.tt);
        Intent intent;
        intent = getIntent(); //intent 값 받기
        md_id=intent.getStringExtra("md_id");
        store_id=intent.getStringExtra("store_id");

        mdImg = findViewById(R.id.mdImg2);
        farmName = findViewById(R.id.farmName2);
        mdName = findViewById(R.id.mdName2);
        goal=  findViewById(R.id.goal2);
        comp= findViewById(R.id.comp2);

         order_status = (TextView) findViewById(R.id.order_status);
         order_status1 = (ImageView) findViewById(R.id.order_status1);
         order_status2 = (ImageView) findViewById(R.id.order_status2);
         order_status3 = (ImageView) findViewById(R.id.order_status3);
         order_status4 = (ImageView) findViewById(R.id.order_status4);
         order_status5 = (ImageView) findViewById(R.id.order_status5);
         order_status6 = (ImageView) findViewById(R.id.order_status6);
         txt_order_status1 = (TextView) findViewById(R.id.txt_order_status1);
         txt_order_status2 = (TextView) findViewById(R.id.txt_order_status2);
         txt_order_status3 = (TextView) findViewById(R.id.txt_order_status3);
         txt_order_status4 = (TextView) findViewById(R.id.txt_order_status4);
         txt_order_status5 = (TextView) findViewById(R.id.txt_order_status5);
         txt_order_status6 = (TextView) findViewById(R.id.txt_order_status6);


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
                Intent intent = new Intent(MDDetailActivity.this, StoreItem.class);
                intent.putExtra("store_id", store_id);
                startActivity(intent);
            }
        });

        md();

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


                        //상품 현재 픽업 상태 세팅
                        md_status = jpArray.get(0).getAsJsonObject().get("stk_confirm").getAsString();
                        pu_date = jpArray.get(0).getAsJsonObject().get("pu_start").getAsString();
                        switch (md_status) {
                            case "공동구매 진행중":
                                order_status.setText("픽업 예정일 : " + pu_date);
                                order_status1.setImageResource(R.drawable.order_status_off);
                                txt_order_status1.setTextColor(Color.parseColor("#1EAA95"));

                                break;
                            case "공동구매 완료":
                                order_status.setText("픽업 예정일 : " + pu_date);
                                order_status2.setImageResource(R.drawable.order_status_off);
                                txt_order_status2.setTextColor(Color.parseColor("#1EAA95"));
                                break;
                            case "상품 준비중":
                                order_status.setText("픽업 예정일 : " + pu_date);
                                order_status3.setImageResource(R.drawable.order_status_off);
                                txt_order_status3.setTextColor(Color.parseColor("#1EAA95"));
                                break;
                            case "상품 배송중":
                                order_status.setText("픽업 예정일 : " + pu_date);
                                order_status4.setImageResource(R.drawable.order_status_off);
                                txt_order_status4.setTextColor(Color.parseColor("#1EAA95"));
                                break;
                            case "스토어 도착":
                                order_status.setText("픽업 예정일 : " + pu_date);
                                order_status5.setImageResource(R.drawable.order_status_off);
                                txt_order_status5.setTextColor(Color.parseColor("#1EAA95"));
                                break;
                            case "픽업완료":
                                order_status.setText("픽업 완료");
                                order_status6.setImageResource(R.drawable.order_status_off);
                                txt_order_status6.setTextColor(Color.parseColor("#1EAA95"));
                                break;

                            default:
                                order_status.setText(md_status);
                                break;
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
    /*
    void map(){
        final Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> address = null;
        try {
            address = geocoder.getFromLocationName(store_loc, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address location = address.get(0);
        double store_lat = location.getLatitude();
        double store_long = location.getLongitude();

        //지도
        MapView mapView = new MapView(this);
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(store_lat, store_long), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(1, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.store_map_view);
        mapViewContainer.addView(mapView);

        //스토어위치 마커 아이콘 띄우기
        MapPoint f_MarkPoint = MapPoint.mapPointWithGeoCoord(store_lat, store_long);  //마커찍기

        MapPOIItem store_marker = new MapPOIItem();
        store_marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        store_marker.setCustomImageResourceId(R.drawable.ic_shop);
        store_marker.setItemName(store_name); //클릭했을때 가게이름 나오기
        store_marker.setTag(0);
        store_marker.setMapPoint(f_MarkPoint);   //좌표입력받아 현위치로 출력

        mapView.addPOIItem(store_marker);

    }*/
}