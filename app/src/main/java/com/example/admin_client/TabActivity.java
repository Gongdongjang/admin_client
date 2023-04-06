package com.example.admin_client;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class TabActivity extends AppCompatActivity {
    private static final String TAG = "tab_Activity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentAdapter adapter;
    String store_id;/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickuplist);

        Intent intent;
        intent=getIntent(); //intent 값 받기
        store_id=intent.getStringExtra("store_id");

        tabLayout=findViewById(R.id.tabs);
        viewPager=findViewById(R.id.view_pager);
        adapter=new FragmentAdapter(getSupportFragmentManager(),1);

        //FragmentAdapter에 컬렉션 담기
        adapter.addFragment(new tabPickup());
        adapter.addFragment(new tabDelivery());

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("픽업");
        tabLayout.getTabAt(1).setText("배송");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { // 선택 X -> 선택 O
                if (tab.getPosition() ==0){ //탭레이아웃 포지션 얻기 0 이 Tab 1

                    FragmentManager m =getSupportFragmentManager();
                    FragmentTransaction t=m.beginTransaction();
                    Bundle bundle = new Bundle();

                    bundle.putString("store_id", "48" );
                    tabPickup fragment = new tabPickup();
                    fragment.setArguments(bundle);
                    //t.replace()
                }else if (tab.getPosition() == 1){

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { // 선택 O -> 선택

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { // 선택 O -> 선택 O

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

    }*/
}