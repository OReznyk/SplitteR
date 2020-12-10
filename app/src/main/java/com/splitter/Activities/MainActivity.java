package com.splitter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Adapters.TabsAdapter;
import com.splitter.Model.User;
import com.splitter.R;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    TabItem tabChats, tabTemp, tabFriends;
    private ViewPager2 viewPager2;
    FirebaseUser fUser;
    DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //look init
        initPageView();
        //database
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user  = snapshot.getValue(User.class);
            }
            //ToDo: handle db error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initPageView(){
        tabLayout =  findViewById(R.id.main_tabBar);
        //ToDo tabItems init
        tabChats = findViewById(R.id.main_chats_tab);
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
                       tab.setText("Profile");
                       break;
                   }
                   case 2: {
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
    public boolean onCreateOptionsMenu(Menu menu){
        //inflating menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //keyboard button "search" pressed
                if(!TextUtils.isEmpty(query.trim())){
                    //ToDo search users/groups
                }
                else {
                    //TODO getAllUsers or getAllChats
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_action_logout:
                setOnlineStatus("offline");
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onStart(){
        checkUserStatus();
        setOnlineStatus("online");
        super.onStart();
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