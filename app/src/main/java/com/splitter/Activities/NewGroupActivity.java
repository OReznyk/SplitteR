package com.splitter.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.splitter.Model.Group;
import com.splitter.Model.Uploader;
import com.splitter.R;

import java.util.ArrayList;
import java.util.List;

public class NewGroupActivity  extends AppCompatActivity {

    private Uri imgURI = null;
    private  Group group;
    private static final String TAG = "TAG";
    private ImageView groupImg;
    private EditText title;
    private RecyclerView participantsView;
    private FloatingActionButton addParticipantBtn;
    private Button saveBtn;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupImg = findViewById(R.id.group_img);
        title = findViewById(R.id.group_title);
        participantsView = findViewById(R.id.group_participantsIv);
        addParticipantBtn = findViewById(R.id.group_addBtn);
        saveBtn = findViewById(R.id.group_saveBtn);
        //ToDo add participants & adminID as this user ID
        List<String> participants = new ArrayList<>();
        List<String> admins = new ArrayList<>();
        groupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDO: set imgListener and get participants
                final String img = "";
                final String gTitle = title.getText().toString();
                if (TextUtils.isEmpty(gTitle)) {
                    title.setError("Title is Required");
                    title.setFocusable(true);
                    return;
                }
                saveToFirebase(img, gTitle, participants, admins);
            }
        });
    }

    private void showImagePickDialog() {
        Intent i = new Intent(this, ImagePicker.class);
        startActivityForResult(i, 1);
    }

    private void saveToFirebase(String img, String gTitle, List<String>participants, List<String>adminsIDs) {
        //ToDo: Do We want to save the creator of item or save "items by types" in users data? Set img
        FirebaseDatabase fDb = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = fDb.getReference("Groups");
        DatabaseReference newID = dbRef.push();
        group = new Group(newID.getKey(), gTitle, imgURI.toString(), participants, adminsIDs, new ArrayList<>(), new ArrayList<>());
        newID.setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Uploader uploader = new Uploader();
                uploader.uploadPhoto(imgURI, "profile", user.getUid(), group.getId());
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newGroupID", group.getId());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //to log failure
                Log.d(TAG, "Group is not created for "+ "id "+ e.getMessage());
                setResult(RESULT_CANCELED);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if (resultCode == RESULT_OK) {
                imgURI = Uri.parse(data.getStringExtra("uriAsString"));
                groupImg.setImageURI(imgURI);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onStart(){
        checkUserStatus();
        super.onStart();
    }
    private void checkUserStatus(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

}