package com.splitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fAuth = FirebaseAuth.getInstance();
    }

    private void userStatusCheck(){
        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null){
            //user signed in; stay
        }else{
            //user not signed in; go to Login Activity
            startActivity(new Intent(ProfileActivity.this, Login.class));
            finish();
        }
    }


    @Override
    protected void onStart(){
        userStatusCheck();
        super.onStart();
    }


}