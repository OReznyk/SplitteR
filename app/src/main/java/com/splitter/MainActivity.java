package com.splitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
import com.splitter.Model.PagerAdapter;
import com.splitter.Model.User;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
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
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user  = snapshot.getValue(User.class);
                //Toast.makeText(MainActivity.this, "HI, "+user.getName(), Toast.LENGTH_SHORT).show();
            }
            //ToDo: handle db error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void initPageView(){
        //toolbar
        /*toolbar = findViewById(R.id.main_toolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Main Page");
        }*/
        //other
        tabLayout =  findViewById(R.id.main_tabBar);
        //ToDo tabItems init
        TabItem tabChats = findViewById(R.id.main_chats_tab);
        TabItem tabTemp1 = findViewById(R.id.temp1);
        TabItem tabTemp2 = findViewById(R.id.temp2);
        //pager init
        viewPager2 =  findViewById(R.id.main_viewPager);
        viewPager2.setAdapter(new PagerAdapter(this, tabLayout.getTabCount()));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               switch (position){
                   case 0: {
                       tab.setText("Chats");
                       break;
                   }
                   case 1: {
                       tab.setText("Temp");
                       break;
                   }
                   case 2: {
                       tab.setText("Temp2");
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                return true;
            case R.id.menu_action_settings:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}