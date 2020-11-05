package com.splitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.splitter.Fragments.ProfileFragment;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase fDb;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseInit();
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, profileFragment, "");
        fragmentTransaction.commit();
    }

    private void firebaseInit(){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        /*fDb = FirebaseDatabase.getInstance();
        dbRef = fDb.getReference("Users");*/
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
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

}