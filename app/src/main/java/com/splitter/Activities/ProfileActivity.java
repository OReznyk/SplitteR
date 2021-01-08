package com.splitter.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.splitter.Fragments.ProfileFragment;
import com.splitter.R;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseInit();
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_viewPager, profileFragment, "");
        fragmentTransaction.commit();
    }

    private void firebaseInit(){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
    }

    @Override
    protected void onStart(){
        checkUserStatus();
        super.onStart();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    private void checkUserStatus(){
        if(fUser == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

}