package com.splitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private static final String TAG = "TAG";
    EditText mName, mEmail, mPwd, mPhone;
    Button mRegBtn;
    TextView mLoginLink;
    ProgressBar mRegProgBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPwd = findViewById(R.id.pwd);
        mPhone = findViewById(R.id.phone);
        mRegBtn = findViewById(R.id.reg_button);
        mLoginLink = findViewById(R.id.login_link);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mRegProgBar = findViewById(R.id.reg_progressBar);


        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String pwd = mPwd.getText().toString().trim();
                final String name = mName.getText().toString();
                final String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Valid email is Required");
                    mEmail.setFocusable(true);
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    mPwd.setError("Password is Required");
                    mPwd.setFocusable(true);
                    return;
                }
                if(pwd.length() < 6){
                    mPwd.setError("Password must be 6 characters or more");
                    mPwd.setFocusable(true);
                    return;
                }
                registrationToFirebase(email, pwd, name, phone);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
        }
        });
    }

    @Override
    //to go to previous activity
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void registrationToFirebase(String email, String pwd, String name, String phone){
        mRegProgBar.setVisibility(View.VISIBLE);
        // User registration to firebase
        fAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // email verification by link
                    mRegProgBar.setVisibility(View.INVISIBLE);
                    FirebaseUser user = fAuth.getCurrentUser();
                    String uid = user.getUid();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            storeUserData(uid, name, email, phone);
                            Toast.makeText(Register.this, "Verification email was sent ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //to log failure
                            mRegProgBar.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "Failure: Verification email was NOT sent "+ e.getMessage());
                        }
                    });
                    Toast.makeText(Register.this,"User created.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Register.this, "Error! "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    mRegProgBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void storeUserData(String uid, String name, String email, String phone){
        FirebaseDatabase fDb = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = fDb.getReference("Users");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", uid);
        hashMap.put("name", name);
        hashMap.put("onlineStatus", "online");
        hashMap.put("email", email);
        hashMap.put("phone", phone);
        hashMap.put("image", "");
        hashMap.put("cover", "");
        dbRef.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Success: User Profile is created for "+ uid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //to log failure
                Log.d(TAG, "User Profile is created for "+ uid + " "+ e.getMessage());
            }
        });
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
        FirebaseUser fUser = fAuth.getCurrentUser();
        if(fUser != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}