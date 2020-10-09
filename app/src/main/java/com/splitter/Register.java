package com.splitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText mName, mEmail, mPwd, mPhone;
    Button mRegBtn;
    TextView mLoginLink;
    ProgressBar mRegProgBar;
    FirebaseAuth fAuth;

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

        mRegProgBar = findViewById(R.id.reg_progressBar);

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pwd = mPwd.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    mPwd.setError("Password is Required");
                    return;
                }
                if(pwd.length() < 6){
                    mPwd.setError("Password must be 6 characters or more");
                    return;
                }

                mRegProgBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"User created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Error! "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            mRegProgBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
        }
        });
    }
}