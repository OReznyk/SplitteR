package com.splitter.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.splitter.Adapters.TabsAdapter;
import com.splitter.R;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    TabItem tabChats, tabGroups, tabTemp, tabFriends;
    private ViewPager2 viewPager2;
    FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //look init
        initPageView();
        //database
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initPageView(){
        tabLayout =  findViewById(R.id.main_tabBar);
        //ToDo tabItems init
        tabChats = findViewById(R.id.main_chats_tab);
        tabGroups = findViewById(R.id.main_groups_tab);
        tabTemp = findViewById(R.id.main_profile_tab);
        tabFriends = findViewById(R.id.main_friends_tab);
        //pager init
        viewPager2 =  findViewById(R.id.main_viewPager);
        viewPager2.setAdapter(new TabsAdapter(this, tabLayout.getTabCount()));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               switch (position){
                   case 0: {
                       tab.setText("Chats");
                       break;
                   }
                   case 1: {
                       tab.setText("Groups");
                       break;
                   }
                   case 2: {
                       tab.setText("Profile");
                       break;
                   }
                   case 3: {
                       tab.setText("Friends");
                       break;
                   }
               }
            }
        }); tabLayoutMediator.attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onPause(){
        setOnlineStatus("offline");
        super.onPause();
    }
    @Override
    protected void onResume() {
        setOnlineStatus("online");
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        setOnlineStatus("offline");
    }
    @Override
    protected void onStart(){
        checkUserStatus();
        setOnlineStatus("online");
        super.onStart();
    }

    private void checkUserStatus(){
        if(fUser == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    private void setOnlineStatus(String status){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        databaseReference.updateChildren(hashMap);
    }

}